package com.example.android_exam.data.remote;

import android.content.Context;
import android.os.Build;

import com.example.android_exam.data.local.database.AppDatabase;
import com.example.android_exam.data.local.entity.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Optimized Local database implementation of DataRepository
 * This handles all local Room database operations and business logic
 */
public class LocalDataRepository implements DataRepository {
    private final Context context;
    private final ExecutorService executor;
    private final AppDatabase database;
    private static int currentUserId = -1; // Default to -1 (no user logged in)

    // Singleton pattern with thread safety
    private static volatile LocalDataRepository INSTANCE;

    private LocalDataRepository(Context context) {
        this.context = context.getApplicationContext();
        this.executor = Executors.newFixedThreadPool(4);
        this.database = AppDatabase.getDatabase(this.context);
    }

    public static LocalDataRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (LocalDataRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LocalDataRepository(context);
                }
            }
        }
        return INSTANCE;
    }

    public static LocalDataRepository getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        throw new IllegalStateException(
                "Repository not initialized. Call getInstance(Context) first."
        );
    }

    public static boolean validateUserId() {
        return currentUserId > 0;
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }

    // ========== USER AUTHENTICATION ==========
    @Override
    public void login(String username, String password, AuthCallback<User> callback) {
        executor.execute(() -> {
            try {
                // Validate input
                if (isNullOrEmpty(username)) {
                    callback.onError("Username cannot be empty");
                    return;
                }
                if (isNullOrEmpty(password)) {
                    callback.onError("Password cannot be empty");
                    return;
                }

                User user = database.userDao().login(username.trim(), hashPassword(password.trim()));
                if (user != null) {
                    currentUserId = user.id;
                    callback.onSuccess(user);
                } else {
                    callback.onError("Invalid username or password");
                }
            } catch (Exception e) {
                callback.onError("Login failed: " + e.getMessage());
            }
        });
    }

    @Override
    public void register(String username, String password, AuthCallback<User> callback) {
        executor.execute(() -> {
            try {
                // Validate input
                if (isNullOrEmpty(username)) {
                    callback.onError("Username cannot be empty");
                    return;
                }
                if (password == null || password.length() < 6) {
                    callback.onError("Password must be at least 6 characters");
                    return;
                }

                // Check if user exists
                User existingUser = database.userDao().getUserByUsername(username.trim());
                if (existingUser != null) {
                    callback.onError("Username already exists");
                    return;
                }

                // Create new user
                User newUser = new User();
                newUser.setUsername(username.trim());
                newUser.setHashedPassword(hashPassword(password.trim()));
                long userId = database.userDao().insert(newUser);
                newUser.id = (int) userId;
                currentUserId = newUser.id;
                callback.onSuccess(newUser);
            } catch (Exception e) {
                callback.onError("Registration failed: " + e.getMessage());
            }
        });
    }

    @Override
    public void logout(AuthCallback<Boolean> callback) {
        executor.execute(() -> {
            try {
                currentUserId = -1;
                callback.onSuccess(true);
            } catch (Exception e) {
                callback.onError("Logout failed: " + e.getMessage());
            }
        });
    }

    // ========== INGREDIENTS MANAGEMENT ==========
    @Override
    public void getAllIngredients(AuthCallback<List<Ingredient>> callback) {
        executor.execute(() -> {
            try {
                if (!validateUserId()) {
                    callback.onError("User not logged in");
                    return;
                }
                List<Ingredient> ingredients = database.ingredientDao().getAllByUserId(currentUserId);
                callback.onSuccess(ingredients != null ? ingredients : new ArrayList<>());
            } catch (Exception e) {
                callback.onError("Failed to fetch ingredients: " + e.getMessage());
            }
        });
    }

    @Override
    public void addIngredient(Ingredient ingredient, AuthCallback<Ingredient> callback) {
        executor.execute(() -> {
            try {
                if (!validateUserId()) {
                    callback.onError("User not logged in");
                    return;
                }

                // Validate ingredient
                if (ingredient == null || isNullOrEmpty(ingredient.getName())) {
                    callback.onError("Ingredient name cannot be empty");
                    return;
                }
                if (ingredient.quantity < 0) {
                    callback.onError("Quantity cannot be negative");
                    return;
                }

                ingredient.userId = currentUserId;
                ingredient.setName(ingredient.getName().trim());
                long id = database.ingredientDao().insert(ingredient);
                ingredient.id = (int) id;
                callback.onSuccess(ingredient);
            } catch (Exception e) {
                callback.onError("Failed to add ingredient: " + e.getMessage());
            }
        });
    }

    @Override
    public void updateIngredient(Ingredient ingredient, AuthCallback<Boolean> callback) {
        executor.execute(() -> {
            try {
                if (!validateUserId()) {
                    callback.onError("User not logged in");
                    return;
                }

                // Validate ingredient
                if (ingredient == null || ingredient.id <= 0) {
                    callback.onError("Invalid ingredient ID");
                    return;
                }
                if (isNullOrEmpty(ingredient.getName())) {
                    callback.onError("Ingredient name cannot be empty");
                    return;
                }

                ingredient.userId = currentUserId;
                ingredient.setName(ingredient.getName().trim());
                int rowsUpdated = database.ingredientDao().update(ingredient);
                callback.onSuccess(rowsUpdated > 0);
            } catch (Exception e) {
                callback.onError("Failed to update ingredient: " + e.getMessage());
            }
        });
    }

    @Override
    public void deleteIngredient(int ingredientId, AuthCallback<Boolean> callback) {
        executor.execute(() -> {
            try {
                if (!validateUserId()) {
                    callback.onError("User not logged in");
                    return;
                }
                if (ingredientId <= 0) {
                    callback.onError("Invalid ingredient ID");
                    return;
                }

                int rowsDeleted = database.ingredientDao().deleteById(ingredientId);
                callback.onSuccess(rowsDeleted > 0);
            } catch (Exception e) {
                callback.onError("Failed to delete ingredient: " + e.getMessage());
            }
        });
    }

    @Override
    public void getExpiringIngredients(int limit, AuthCallback<List<Ingredient>> callback) {
        executor.execute(() -> {
            try {
                if (!validateUserId()) {
                    callback.onError("User not logged in");
                    return;
                }

                final int finalLimit = Math.max(limit, 1);
                List<Ingredient> expiring = database.ingredientDao().getTopExpiringIngredients(currentUserId, finalLimit);
                callback.onSuccess(expiring != null ? expiring : new ArrayList<>());
            } catch (Exception e) {
                callback.onError("Failed to fetch expiring ingredients: " + e.getMessage());
            }
        });
    }

    // ========== MEALS MANAGEMENT ==========
    @Override
    public void getAllMeals(AuthCallback<List<Meal>> callback) {
        executor.execute(() -> {
            try {
                if (!validateUserId()) {
                    callback.onError("User not logged in");
                    return;
                }

                List<Meal> meals = database.mealDao().getAllByUserId(currentUserId);
                callback.onSuccess(meals != null ? meals : new ArrayList<>());
            } catch (Exception e) {
                callback.onError("Failed to fetch meals: " + e.getMessage());
            }
        });
    }

    @Override
    public void addMeal(Meal meal, AuthCallback<Meal> callback) {
        executor.execute(() -> {
            try {
                if (!validateUserId()) {
                    callback.onError("User not logged in");
                    return;
                }

                if (meal == null || isNullOrEmpty(meal.getName())) {
                    callback.onError("Meal name cannot be empty");
                    return;
                }

                meal.userId = currentUserId;
                meal.setName(meal.getName().trim());

                // Set meal type based on time if not set
                if (isNullOrEmpty(meal.getMealType())) {
                    long timestamp = meal.getDateTimestamp();
                    if (timestamp == 0) {
                        timestamp = System.currentTimeMillis();
                        meal.setDateTimestamp(timestamp);
                    }

                    int hour = extractHourFromTimestamp(timestamp);
                    meal.setMealType(determineMealType(hour));
                }

                long id = database.mealDao().insert(meal);
                meal.id = (int) id;
                callback.onSuccess(meal);
            } catch (Exception e) {
                callback.onError("Failed to add meal: " + e.getMessage());
            }
        });
    }

    @Override
    public void getMealsByDate(String date, AuthCallback<List<Meal>> callback) {
        executor.execute(() -> {
            try {
                if (!validateUserId()) {
                    callback.onError("User not logged in");
                    return;
                }
                if (isNullOrEmpty(date)) {
                    callback.onError("Date cannot be empty");
                    return;
                }

                long[] dateRange = parseDateToRange(date.trim());
                List<Meal> meals = database.mealDao().getMealsByDateRange(currentUserId, dateRange[0], dateRange[1]);
                callback.onSuccess(meals != null ? meals : new ArrayList<>());
            } catch (Exception e) {
                callback.onError("Failed to fetch meals by date: " + e.getMessage());
            }
        });
    }

    @Override
    public void getFoodsInMeal(int mealId, AuthCallback<List<Food>> callback) {
        executor.execute(() -> {
            try {
                if (mealId <= 0) {
                    callback.onError("Invalid meal ID");
                    return;
                }

                List<Food> foods = database.foodDao().getAllInMeal(mealId);
                callback.onSuccess(foods != null ? foods : new ArrayList<>());
            } catch (Exception e) {
                callback.onError("Failed to fetch foods in meal: " + e.getMessage());
            }
        });
    }

    @Override
    public void getTotalCaloriesByDate(String date, AuthCallback<Double> callback) {
        executor.execute(() -> {
            try {
                if (!validateUserId()) {
                    callback.onError("User not logged in");
                    return;
                }
                if (isNullOrEmpty(date)) {
                    callback.onError("Date cannot be empty");
                    return;
                }

                long[] dateRange = parseDateToRange(date.trim());
                Double totalCalories = database.mealDao().getTotalCaloriesByDateRange(currentUserId, dateRange[0], dateRange[1]);
                callback.onSuccess(totalCalories != null ? totalCalories : 0.0);
            } catch (Exception e) {
                callback.onError("Failed to calculate total calories: " + e.getMessage());
            }
        });
    }

    // ========== FOOD MANAGEMENT ==========
    @Override
    public void addFood(Food food, AuthCallback<Food> callback) {
        executor.execute(() -> {
            try {
                // Validate food
                if (food == null || isNullOrEmpty(food.getName())) {
                    callback.onError("Food name cannot be empty");
                    return;
                }
                if (food.totalCalories < 0) {
                    callback.onError("Calories cannot be negative");
                    return;
                }
                if (food.mealId <= 0) {
                    callback.onError("Invalid meal ID");
                    return;
                }

                food.setName(food.getName().trim());
                long id = database.foodDao().insert(food);
                food.id = (int) id;
                callback.onSuccess(food);
            } catch (Exception e) {
                callback.onError("Failed to add food: " + e.getMessage());
            }
        });
    }

    @Override
    public void updateFood(Food food, AuthCallback<Boolean> callback) {
        executor.execute(() -> {
            try {
                // Validate food
                if (food == null || food.id <= 0) {
                    callback.onError("Invalid food ID");
                    return;
                }
                if (isNullOrEmpty(food.getName())) {
                    callback.onError("Food name cannot be empty");
                    return;
                }
                if (food.totalCalories < 0) {
                    callback.onError("Calories cannot be negative");
                    return;
                }

                food.setName(food.getName().trim());
                int rowsUpdated = database.foodDao().update(food);
                callback.onSuccess(rowsUpdated > 0);
            } catch (Exception e) {
                callback.onError("Failed to update food: " + e.getMessage());
            }
        });
    }

    // ========== NUTRITION GOALS ==========
    @Override
    public void getNutritionGoal(AuthCallback<NutritionGoal> callback) {
        executor.execute(() -> {
            try {
                if (!validateUserId()) {
                    callback.onError("User not logged in");
                    return;
                }

                NutritionGoal goal = database.nutritionGoalDao().getByUserId(currentUserId);
                callback.onSuccess(goal);
            } catch (Exception e) {
                callback.onError("Failed to fetch nutrition goal: " + e.getMessage());
            }
        });
    }

    @Override
    public void setNutritionGoal(NutritionGoal goal, AuthCallback<NutritionGoal> callback) {
        executor.execute(() -> {
            try {
                if (!validateUserId()) {
                    callback.onError("User not logged in");
                    return;
                }
                // Validate goal
                if (goal == null) {
                    callback.onError("Nutrition goal cannot be null");
                    return;
                }
                if (goal.dailyCalorieGoal <= 0) {
                    callback.onError("Daily calorie goal must be positive");
                    return;
                }
                if (goal.dailyProteinGoal < 0 || goal.dailyCarbGoal < 0 || goal.dailyFatGoal < 0) {
                    callback.onError("Nutrition goals cannot be negative");
                    return;
                }

                goal.userId = currentUserId;

                if (isNullOrEmpty(goal.getGoalType())) {
                    goal.setGoalType("maintain");
                }

                // Validate goal type
                String goalType = goal.getGoalType().toLowerCase().trim();
                if (!isValidGoalType(goalType)) {
                    callback.onError("Invalid goal type. Must be: weight_loss, muscle_gain, or maintain");
                    return;
                }
                goal.setGoalType(goalType);

                // Delete existing goal first, then insert new one
                database.nutritionGoalDao().deleteByUserId(currentUserId);
                long id = database.nutritionGoalDao().insert(goal);
                goal.id = (int) id;
                callback.onSuccess(goal);
            } catch (Exception e) {
                callback.onError("Failed to set nutrition goal: " + e.getMessage());
            }
        });
    }

    // ========== AI SUGGESTIONS ==========
    @Override
    public void getSuggestedMeals(AuthCallback<List<Food>> callback) {
        executor.execute(() -> {
            try {
                if (!validateUserId()) {
                    callback.onError("User not logged in");
                    return;
                }

                // Get user's ingredients and nutrition goal
                List<Ingredient> ingredients = database.ingredientDao().getAllByUserId(currentUserId);
                NutritionGoal goal = database.nutritionGoalDao().getByUserId(currentUserId);

                // Generate suggestions based on available ingredients and goal
                List<Food> suggestions = generateMealSuggestions(ingredients, goal);
                callback.onSuccess(suggestions);
            } catch (Exception e) {
                callback.onError("Failed to generate meal suggestions: " + e.getMessage());
            }
        });
    }

    // ========== HELPER METHODS ==========
    private List<Food> generateMealSuggestions(List<Ingredient> ingredients, NutritionGoal goal) {
        List<Food> suggestions = new ArrayList<>();

        // Basic logic: suggest meals based on ingredients and goal
        String goalType = goal != null ? goal.getGoalType() : "maintain";
        double calorieTarget = goal != null ? goal.dailyCalorieGoal * 0.3 : 500.0; // 30% of daily goal per meal

        // Check for common ingredients
        boolean hasProtein = false, hasVeggies = false, hasGrains = false;
        for (Ingredient ingredient : ingredients != null ? ingredients : new ArrayList<Ingredient>()) {
            String category = ingredient.getCategory() != null ? ingredient.getCategory().toLowerCase() : "";
            if (category.contains("thịt") || category.contains("cá") || category.contains("trứng")) {
                hasProtein = true;
            }
            if (category.contains("rau") || category.contains("trái cây")) {
                hasVeggies = true;
            }
            if (category.contains("ngũ cốc")) {
                hasGrains = true;
            }
        }

        // Generate suggestions based on available ingredients and goals
        if ("weight_loss".equals(goalType) && hasVeggies) {
            suggestions.add(createSuggestedFood("Vegetable Salad", calorieTarget * 0.7, "Low-calorie salad for weight loss"));
        }

        if ("muscle_gain".equals(goalType) && hasProtein) {
            suggestions.add(createSuggestedFood("Grilled Chicken & Rice", calorieTarget * 1.2, "High-protein meal for muscle gain"));
        }

        if (hasGrains && hasVeggies) {
            suggestions.add(createSuggestedFood("Balanced Bowl", calorieTarget, "A balanced meal with grains and vegetables"));
        }

        // Always provide at least one suggestion
        if (suggestions.isEmpty()) {
            suggestions.add(createSuggestedFood("Simple Balanced Meal", calorieTarget, "A balanced meal for general health"));
        }

        return suggestions;
    }

    private Food createSuggestedFood(String name, double calories, String description) {
        Food food = new Food();
        food.setName(name);
        food.setTotalCalories(calories);
        food.setDescription(description);
        food.setMealId(0); // Will be set when added to a meal
        return food;
    }

    private String determineMealType(int hour) {
        if (hour >= 5 && hour < 11) return "breakfast";
        else if (hour >= 11 && hour < 17) return "lunch";
        else if (hour >= 17 && hour < 22) return "dinner";
        else return "snack";
    }

    private int extractHourFromTimestamp(long timestamp) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Instant.ofEpochMilli(timestamp)
                        .atZone(ZoneId.systemDefault())
                        .getHour();
            }
        } catch (Exception e) {
            // Fallback for older Android versions or errors
        }
        return 12; // Default to noon
    }

    private long[] parseDateToRange(String date) {
        // Simple date parsing - expects format "yyyy-MM-dd"
        // Returns start and end of day in milliseconds
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDate localDate = LocalDate.parse(date);
                long startOfDay = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
                long endOfDay = localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1;
                return new long[]{startOfDay, endOfDay};
            }
        } catch (Exception e) {
            // Fallback for parsing errors or older Android versions
        }

        // Fallback: use current day
        long now = System.currentTimeMillis();
        long dayStart = now - (now % (24 * 60 * 60 * 1000));
        long dayEnd = dayStart + (24 * 60 * 60 * 1000) - 1;
        return new long[]{dayStart, dayEnd};
    }

    private String hashPassword(String password) {
        // TODO: In production, use a proper hashing algorithm like BCrypt
        return "hashed_" + password; // Placeholder for demonstration
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private boolean isValidGoalType(String goalType) {
        return "weight_loss".equals(goalType) || "muscle_gain".equals(goalType) || "maintain".equals(goalType);
    }

    // ========== CLEANUP ==========
    public void cleanup() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
        AppDatabase.closeDatabase();
    }
}