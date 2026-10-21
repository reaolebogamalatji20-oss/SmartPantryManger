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
                "Cooking oil|1|tbsp", "Onion|1|piece", "Garlic|1|clove", "Paprika|1|tsp", "Cayenne Pepper|1|tsp", "Tomato Paste|2|tbsp", "Eggs|3|piece", "Bread|2|piece");

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

        add(db, "Scrambled Eggs on Toast",
                "1. Toast the bread.\n2. Melt butter, add whisked eggs and salt.\n3. Stir gently until just set and serve on toast.",
                "egg|2|piece", "bread|2|piece", "butter|10|g", "salt|1|tsp");

        add(db, "Garlic Butter Pasta",
                "1. Boil pasta in salted water.\n2. Melt butter with crushed garlic.\n3. Toss drained pasta in the garlic butter.",
                "pasta|200|g", "garlic|2|piece", "butter|30|g", "salt|1|tsp");

        add(db, "Simple Tomato Pasta",
                "1. Boil the pasta.\n2. Fry onion and garlic in olive oil.\n3. Add chopped tomatoes and simmer 15 minutes.\n4. Mix with pasta.",
                "pasta|200|g", "tomato|4|piece", "onion|1|piece", "garlic|2|piece", "olive oil|2|tbsp");

        add(db, "Baked Potato with Cheese",
                "1. Prick the potatoes and bake at 200C for 60 minutes.\n2. Split open, add butter and grated cheese.",
                "potato|2|piece", "cheese|60|g", "butter|10|g");

        add(db, "Mashed Potatoes",
                "1. Peel and boil potatoes until soft.\n2. Drain and mash with butter and warm milk.\n3. Season with salt.",
                "potato|4|piece", "butter|30|g", "milk|100|ml", "salt|1|tsp");

        add(db, "Tuna Pasta Salad",
                "1. Cook and cool the pasta.\n2. Drain the tuna and dice tomatoes.\n3. Toss everything with olive oil.",
                "tuna|1|piece", "pasta|150|g", "tomato|2|piece", "olive oil|1|tbsp");
    }

    /**Inserts one recipe plus its ingredient rows.*/
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
