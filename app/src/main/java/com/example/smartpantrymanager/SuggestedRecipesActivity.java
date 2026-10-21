package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.adapter.RecipeAdapter;
import com.example.smartpantrymanager.db.DatabaseHelper;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.util.IngredientMatcher;

import java.util.List;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private RecipeAdapter adapter;
    private TextView tvNoMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);
        setTitle("Suggested Recipes");
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);
        tvNoMatch = findViewById(R.id.tvNoMatch);

        RecyclerView rv = findViewById(R.id.rvRecipes);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(recipe -> {
            Intent i = new Intent(this, RecipeDetailActivity.class);
            i.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
            startActivity(i);
        });
        rv.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Re-run the strict matching every time the screen is shown
        List<Recipe> suggested = IngredientMatcher.getSuggestedRecipes(
                db.getAllRecipesWithIngredients(), db.getAllPantryItems());
        adapter.setRecipes(suggested);
        tvNoMatch.setVisibility(suggested.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}