package com.example.mealreceiptapp;

import java.util.ArrayList;
import java.util.List;

public class SavedMealsHelper {

    private static List<Integer> savedMeals = new ArrayList<>();

    public static void addMeal(int mealID) {
        if (!savedMeals.contains(mealID)) {
            savedMeals.add(mealID);
        }
    }

    public static List<Integer> getSavedMeals() {
        return new ArrayList<>(savedMeals);
    }
}
