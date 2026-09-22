package com.example.smartpantrymanager.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
/** Pre-loads the recipe collection the first time the database is created. */
class SeedData {
    static void insertAll(SQLiteDatabase db) {
        add(db, "Tomato Omlette",
                "1. Beat the eggs in a bowl.\n2. Chop the tomato.\n3. Melt the butter in a pan.\n4. Add tomato, then eggs, and cook until set.",
                "egg|3|piece", "tomato|1|piece", "butter|10|g");

        add(db, "Spaghetti Bolognese",
                "1. In a pan, soute onion and garlic in olive oil.\n2. Add ground beef and cook until brown.\n3. Add tomatoes, tomato paste, oregano, simmer for 20 minutes.\n4. Boil spaghetti in a pot.\n5. Mix and serve with parmesan.",
                "Spaghetti|300|g", "Ground Beef|500|g", "Tomatoes|3|piece", "Olive Oil|3|tbsp", "Tomato Paste|2|tbsp", "Oregano|1|tsp", "Onion|1|piece", "Parmesan|50|g");

        add(db, "Cheese Toastie",
                "1. Butter the outside of the bread.\n2. Add cheese between the slices.\n3. Fry until golden on both sides.",
                "bread|2|piece", "cheese|50|g", "butter|10|g");

        add(db, "Shakshuka",
                "1. In a pan saute onion and pepper until soft.\n2. Add garlic and spices.\n3. Stir in tomatoes or tomato paste; simmer until thick.\n4. Add eggs, cover and cook for 5-8 minutes.",
                "Cooking oil|1|tsbp", "Onion|1|piece", "Garlic|1|clove", "Paprika|1|tsp", "Cayenne Pepper|1/4|tsp", "Tomato Paste|2|tbsp", "Eggs|3|piece", "Bread");

        add(db, "French Toast",
                "1. Whisk eggs and milk.\n2. Dip bread slices.\n3. Fry in butter until golden.",
                "bread|4|piece", "butter|10|g", "eggs|3|piece", "milk|100|ml");

        add(db, "Porridge",
                "1. Boil water in a pot.\n2. Pour maize meal into pot.\n3. Mix until there are no lumps.\n4. Simmer, stirring occasionally, for 10-20 minutes until thickens to your desired consistency.\n5. Sweeten with sugar.\n6. Add butter.",
                "Water|1.5|l", "Maize Meal|1|cup", "Sugar|3|tsp", "Butter|10|g");

        add(db, "Pancakes",
                "1. Whisk flour, milk, eggs and sugar into a smooth batter.\n2. Rest for 10 minutes.\n3. Fry ladlefuls until golden on both sides.",
                "flour|200|g", "milk|300|ml", "eggs|2|piece", "sugar|1|tbsp");

        add(db, "Egg Fried Rice",
                "1. Cook and cool the rice.\n2. Fry chopped onion, add rice.\n3. Scramble eggs in the pan.\n4. Mix everything with worcestershire sauce or soy sauce.",
                "rice|200|g", "egg|2|piece", "onion|1|piece", "worcestershire sauce|2|tbsp", "soy sauce|2|tbsp");

        add(db, "Chicken Rice Bowl",
                "1. Season and pan-fry chicken in oil.\n2. Cook rice.\n3. Fry onion and garlic, slice chicken and serve over rice.",
                "Chicken breast|2|piece", "rice|200|g", "onion|1|piece", "garlic|1|clove", "oil|1|tbsp");

        add(db, "Lentil Soup",
                "1. Fry chopped onion, carrot, garlic.\n2. Add lentils and water.\n3. Simmer 30 minutes, season with salt.",
                "lentils|200|g", "carrot|2|piece", "onion|1|piece", "garlic|2|clove", "water|1|l", "salt|1|tsp");
    }

    /**
     * Inserts one recipe plus its ingredient rows.
     */
    private static void add(SQLiteDatabase db, String name, String steps, String... ingredients) {
        ContentValues rv = new ContentValues();
        rv.put("name", name);
        rv.put("steps", steps);
        long id = db.insert("recipes", null, rv);

        for (String line : ingredients) {
            String[] parts = line.split("\\|");    //["egg", "3", "piece"]
            ContentValues iv = new ContentValues();
            iv.put("recipe_id", id);
            iv.put("name", parts[0]);
            iv.put("quantity", Double.parseDouble(parts[1]));
            iv.put("unit", parts[2]);
            db.insert("recipe_ingredients", null, iv);
        }
    }
}
