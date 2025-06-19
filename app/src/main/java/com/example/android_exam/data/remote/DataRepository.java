package com.example.android_exam.data.remote;
import com.example.android_exam.data.models.Food;
import com.example.android_exam.data.models.Ingredient;
import com.example.android_exam.data.models.Meal;
import com.example.android_exam.data.models.enums.NutritionGoal;
import com.example.android_exam.data.models.User;

import java.util.List;

/**
 * Repository interface - Abstraction layer for data operations
 * This allows easy switching between local database and remote API
 * Updated to use internal user validation instead of userId parameters
 */
public interface DataRepository {
    interface AuthCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    // ========== USER AUTHENTICATION ==========
    void login(String username, String password, AuthCallback<User> callback);
    void getUserInformation(String token, AuthCallback<User> callback);

    void register(String username, String password, String email, AuthCallback<User> callback);

    void logout(AuthCallback<Boolean> callback);

    // ========== INGREDIENTS MANAGEMENT ==========
    /**
     * Get all ingredients for the currently logged-in user
     */
    void getAllIngredients(AuthCallback<List<Ingredient>> callback);
    void getIngredientById(int id, AuthCallback<Ingredient> callback);

    void getFilteredIngredients(String filter, AuthCallback<List<Ingredient>> callback);

    /**
     * Add a new ingredient for the currently logged-in user
     */
    void addIngredient(Ingredient ingredient, AuthCallback<Ingredient> callback);

    /**
     * Update an existing ingredient
     */
    void updateIngredient(Ingredient ingredient, AuthCallback<Boolean> callback);

    /**
     * Delete an ingredient by ID
     */
    void deleteIngredient(int ingredientId, AuthCallback<Boolean> callback);

    void refreshIngredients(AuthCallback<List<Ingredient>> callback);
    void refreshExpiringIngredients(AuthCallback<List<Ingredient>> callback);

    /**
     * Get ingredients that are expiring soon for the currently logged-in user
     * @param limit maximum number of items to return
     */
    void getExpiringIngredients(int limit, AuthCallback<List<Ingredient>> callback);

    // ========== MEALS MANAGEMENT ==========
    /**
     * Get all meals for the currently logged-in user
     */
    void getAllMeals(AuthCallback<List<Meal>> callback);

    /**
     * Add a new meal for the currently logged-in user
     */
    void addMeal(Meal meal, AuthCallback<Meal> callback);

    /**
     * Get meals by date for the currently logged-in user
     * @param date date in "yyyy-MM-dd" format
     */
    void getMealsByDate(String date, AuthCallback<List<Meal>> callback);

    /**
     * Get all foods in a specific meal
     */
    void getFoodsInMeal(int mealId, AuthCallback<List<Food>> callback);

    /**
     * Get total calories consumed on a specific date for the currently logged-in user
     * @param date date in "yyyy-MM-dd" format
     */
    void getTotalCaloriesByDate(String date, AuthCallback<Double> callback);

    // ========== FOOD MANAGEMENT ==========
    /**
     * Add a new food item to a meal
     */
    void addFood(Food food, AuthCallback<Food> callback);

    /**
     * Update an existing food item
     */
    void updateFood(Food food, AuthCallback<Boolean> callback);

    /**
     * get a food item by ID
     */
    void getFoodsByMealId(int mealId, AuthCallback<List<Food>> callback);

    // ========== NUTRITION GOALS ==========
    /**
     * Get nutrition goal for the currently logged-in user
     */
    void getNutritionGoal(AuthCallback<NutritionGoal> callback);

    /**
     * Set nutrition goal for the currently logged-in user
     */
    void setNutritionGoal(NutritionGoal goal, AuthCallback<NutritionGoal> callback);

    // ========== AI SUGGESTIONS ==========
    /**
     * Get AI-suggested meals based on available ingredients and nutrition goals
     * for the currently logged-in user
     */
    void getSuggestedMeals(AuthCallback<List<Food>> callback);
}