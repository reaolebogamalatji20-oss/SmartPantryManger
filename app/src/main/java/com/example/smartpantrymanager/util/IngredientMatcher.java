package com.example.smartpantrymanager.util;

import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Core business logic: a recipe is "suggested" ONLY if EVERY ingredient is in the pantry
 * in at least the required quantity.
 */
public final class IngredientMatcher {

    private IngredientMatcher() {} // static utility class, never instantiated

    // --unit tables: alias -> category (MASS / VOLUME / COUNT) and alias
    private static final Map<String, String> UNIT_TYPE = new HashMap<>();
    private static final Map<String, Double> UNIT_FACTOR = new HashMap<>();

    private static void register(String type, double factor, String... aliases) {
        for (String alias : aliases) {
            UNIT_TYPE.put(alias, type);
            UNIT_FACTOR.put(alias, factor);
        }
    }

    static {
        register("MASS", 1, "g", "gram", "grams");                       // base unit: grams
        register("MASS", 1000, "kg", "kilogram", "kilograms");
        register("VOLUME", 1, "ml", "millilitre", "milliliter", "millilitres", "milliliters"); // base: ml
        register("VOLUME", 1000, "l", "litre", "liter", "litres", "liters");
        register("VOLUME", 5, "tsp", "teaspoon", "teaspoons");
        register("VOLUME", 15, "tbsp", "tablespoon", "tablespoons");
        register("VOLUME", 250, "cup", "cups");
        register("COUNT", 1, "piece", "pieces", "pc", "pcs", "whole", "clove", "cloves", "can", "cans");
    }

    /** "Tomatoes " -> "tomato", "Berries" -> "berry". Rule-based, not full NLP. */
    static String normaliseName(String raw) {
        if (raw == null) return "";
        String n = raw.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
        if (n.length() > 4 && n.endsWith("ies")) return n.substring(0, n.length() - 3) + "y"; // berries -> berry
        if (n.length() > 4 && n.endsWith("oes")) return n.substring(0, n.length() - 2);        // tomatoes -> tomato
        if (n.length() > 3 && n.endsWith("s") && !n.endsWith("ss")) return n.substring(0, n.length() - 1); // eggs -> egg
        return n;
    }

    private static String unitType(String unit) {
        String t = UNIT_TYPE.get(unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT));
        return t == null ? "COUNT" : t; // unknown units are treated as a simple count
    }

    private static double toBase(double quantity, String unit) {
        Double f = UNIT_FACTOR.get(unit == null ? "" : unit.trim().toLowerCase(Locale.ROOT));
        return quantity * (f == null ? 1.0 : f);
    }

    private static String key(String name, String unit) {
        return normaliseName(name) + "|" + unitType(unit);
    }

    /** Turns the pantry into a lookup: key -> total quantity in base units. */
    private static Map<String, Double> buildStock(List<PantryItem> pantry) {
        Map<String, Double> stock = new HashMap<>();
        for (PantryItem item : pantry) {
            String k = key(item.getName(), item.getUnit());
            double current = stock.containsKey(k) ? stock.get(k) : 0.0;
            stock.put(k, current + toBase(item.getQuantity(), item.getUnit()));
        }
        return stock;
    }

    /** STRICT RULE: every ingredient must be present in sufficient quantity. */
    static boolean canMake(Recipe recipe, Map<String, Double> stock) {
        if (recipe.getIngredients().isEmpty()) return false; // nothing to match against
        for (RecipeIngredient ing : recipe.getIngredients()) {
            double have = stock.containsKey(key(ing.getName(), ing.getUnit()))
                    ? stock.get(key(ing.getName(), ing.getUnit())) : 0.0;
            double need = toBase(ing.getQuantity(), ing.getUnit());
            if (have + 1e-9 < need) return false; // one missing/short ingredient -> reject whole recipe
        }
        return true;
    }

    public static List<Recipe> getSuggestedRecipes(List<Recipe> allRecipes, List<PantryItem> pantry) {
        Map<String, Double> stock = buildStock(pantry);
        List<Recipe> result = new ArrayList<>();
        for (Recipe r : allRecipes) {
            if (canMake(r, stock)) result.add(r);
        }
        return result;
    }
}
