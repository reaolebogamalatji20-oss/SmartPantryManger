package com.example.smartpantrymanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "smart_pantry_manager.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true); // so deleting a recipe cascades to its ingredients
    }

    /** Runs ONCE, the first time the database file is created = "first run". */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE pantry (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL, " +
                "expiry_date TEXT)");

        db.execSQL("CREATE TABLE recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "steps TEXT NOT NULL)");

        db.execSQL("CREATE TABLE recipe_ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipe_id INTEGER NOT NULL, " +
                "name TEXT NOT NULL, " +
                "quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL, " +
                "FOREIGN KEY(recipe_id) REFERENCES recipes(id) ON DELETE CASCADE)");

        SeedData.insertAll(db); // pre-load the recipe collection
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS recipe_ingredients");
        db.execSQL("DROP TABLE IF EXISTS recipes");
        db.execSQL("DROP TABLE IF EXISTS pantry");
        onCreate(db);
    }

    // ---------------------------------------------------------------- PANTRY CRUD

    private ContentValues toValues(PantryItem item) {
        ContentValues v = new ContentValues();
        v.put("name", item.getName());
        v.put("quantity", item.getQuantity());
        v.put("unit", item.getUnit());
        v.put("expiry_date", item.getExpiryDate()); // null is stored as NULL
        return v;
    }

    private PantryItem pantryFromCursor(Cursor c) {
        return new PantryItem(
                c.getLong(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("name")),
                c.getDouble(c.getColumnIndexOrThrow("quantity")),
                c.getString(c.getColumnIndexOrThrow("unit")),
                c.getString(c.getColumnIndexOrThrow("expiry_date")));
    }

    /** CREATE */
    public long insertPantryItem(PantryItem item) {
        return getWritableDatabase().insert("pantry", null, toValues(item));
    }

    /** READ (all) */
    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query("pantry", null, null, null,
                null, null, "name COLLATE NOCASE ASC")) {
            while (c.moveToNext()) list.add(pantryFromCursor(c));
        }
        return list;
    }

    /** READ (one), used to pre-fill the edit form. */
    public PantryItem getPantryItem(long id) {
        try (Cursor c = getReadableDatabase().query("pantry", null, "id = ?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            return c.moveToFirst() ? pantryFromCursor(c) : null;
        }
    }

    /** UPDATE */
    public int updatePantryItem(PantryItem item) {
        return getWritableDatabase().update("pantry", toValues(item), "id = ?",
                new String[]{String.valueOf(item.getId())});
    }

    /** DELETE */
    public void deletePantryItem(long id) {
        getWritableDatabase().delete("pantry", "id = ?", new String[]{String.valueOf(id)});
    }

    // ---------------------------------------------------------------- RECIPES (read-only)

    public List<Recipe> getAllRecipesWithIngredients() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query("recipes", null, null, null, null, null, "name ASC")) {
            while (c.moveToNext()) {
                Recipe r = new Recipe(
                        c.getLong(c.getColumnIndexOrThrow("id")),
                        c.getString(c.getColumnIndexOrThrow("name")),
                        c.getString(c.getColumnIndexOrThrow("steps")));
                r.setIngredients(loadIngredients(db, r.getId()));
                recipes.add(r);
            }
        }
        return recipes;
    }

    public Recipe getRecipe(long id) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query("recipes", null, "id = ?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            if (!c.moveToFirst()) return null;
            Recipe r = new Recipe(id,
                    c.getString(c.getColumnIndexOrThrow("name")),
                    c.getString(c.getColumnIndexOrThrow("steps")));
            r.setIngredients(loadIngredients(db, id));
            return r;
        }
    }

    private List<RecipeIngredient> loadIngredients(SQLiteDatabase db, long recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        try (Cursor c = db.query("recipe_ingredients", null, "recipe_id = ?",
                new String[]{String.valueOf(recipeId)}, null, null, null)) {
            while (c.moveToNext()) {
                list.add(new RecipeIngredient(
                        c.getString(c.getColumnIndexOrThrow("name")),
                        c.getDouble(c.getColumnIndexOrThrow("quantity")),
                        c.getString(c.getColumnIndexOrThrow("unit"))));
            }
        }
        return list;
    }
}