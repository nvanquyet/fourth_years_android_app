package com.example.android_exam.data.local.database;
import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.android_exam.data.local.dao.FoodDao;
import com.example.android_exam.data.local.dao.NutritionGoalDao;
import com.example.android_exam.data.local.entity.Food;
import com.example.android_exam.data.local.entity.NutritionGoal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.room.TypeConverters;

import com.example.android_exam.data.local.entity.*;
import com.example.android_exam.data.local.dao.*;
import com.example.android_exam.utils.Converters;

import java.util.List;

@Database(
        entities = {User.class, Ingredient.class, Food.class, Meal.class, NutritionGoal.class},
        version = 1,
        exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    // Abstract DAO methods
    public abstract UserDao userDao();
    public abstract IngredientDao ingredientDao();
    public abstract FoodDao foodDao();
    public abstract MealDao mealDao();
    public abstract NutritionGoalDao nutritionGoalDao();

    // Singleton instance
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // Database instance
    static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "food_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Static context holder
    private static Context appContext;
    public static void initialize(Context context) {
        appContext = context.getApplicationContext();
    }

    // =====================================================
    // STATIC METHODS - API ABSTRACTION LAYER
    // =====================================================

    // ========== USER AUTHENTICATION ==========
    public static void login(String username, String password, DatabaseCallback<User> callback) {
        databaseWriteExecutor.execute(() -> {
            try {
                AppDatabase db = getDatabase(appContext);
                User user = db.userDao().login(username, password);
                callback.onSuccess(user);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public static void register(String username, String password, DatabaseCallback<User> callback) {
        databaseWriteExecutor.execute(() -> {
            try {
                AppDatabase db = getDatabase(appContext);
                // Check if user exists
                User existingUser = db.userDao().getUserByUsername(username);
                if (existingUser != null) {
                    callback.onError("Username already exists");
                    return;
                }

                // Create new user
                User newUser = new User();
                newUser.username = username;
                newUser.password = password;
                long userId = db.userDao().insert(newUser);
                newUser.id = (int) userId;
                callback.onSuccess(newUser);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    // ========== INGREDIENTS MANAGEMENT ==========
    public static void getAllIngredients(int userId, DatabaseCallback<List<Ingredient>> callback) {
        databaseWriteExecutor.execute(() -> {
            try {
                AppDatabase db = getDatabase(appContext);
                List<Ingredient> ingredients = db.ingredientDao().getAllByUserId(userId);
                callback.onSuccess(ingredients);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public static void addIngredient(Ingredient ingredient, DatabaseCallback<Ingredient> callback) {
        databaseWriteExecutor.execute(() -> {
            try {
                AppDatabase db = getDatabase(appContext);
                long id = db.ingredientDao().insert(ingredient);
                ingredient.id = (int) id;
                callback.onSuccess(ingredient);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public static void updateIngredient(Ingredient ingredient, DatabaseCallback<Boolean> callback) {
        databaseWriteExecutor.execute(() -> {
            try {
                AppDatabase db = getDatabase(appContext);
                int rowsUpdated = db.ingredientDao().update(ingredient);
                callback.onSuccess(rowsUpdated > 0);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public static void deleteIngredient(int ingredientId, DatabaseCallback<Boolean> callback) {
        databaseWriteExecutor.execute(() -> {
            try {
                AppDatabase db = getDatabase(appContext);
                int rowsDeleted = db.ingredientDao().deleteById(ingredientId);
                callback.onSuccess(rowsDeleted > 0);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public static void getExpiringIngredients(int userId, int limit, DatabaseCallback<List<Ingredient>> callback) {
        databaseWriteExecutor.execute(() -> {
            try {
                AppDatabase db = getDatabase(appContext);
                List<Ingredient> expiring = db.ingredientDao().getTopExpiringIngredients(userId, limit);
                callback.onSuccess(expiring);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    // ========== MEALS MANAGEMENT ==========
    public static void getAllMeals(int userId, DatabaseCallback<List<Meal>> callback) {
        databaseWriteExecutor.execute(() -> {
            try {
                AppDatabase db = getDatabase(appContext);
                List<Meal> meals = db.mealDao().getAllByUserId(userId);
                callback.onSuccess(meals);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public static void addMeal(Meal meal, DatabaseCallback<Meal> callback) {
        databaseWriteExecutor.execute(() -> {
            try {
                AppDatabase db = getDatabase(appContext);
                long id = db.mealDao().insert(meal);
                meal.id = (int) id;
                callback.onSuccess(meal);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public static void getMealsByDate(int userId, String date, DatabaseCallback<List<Meal>> callback) {
        databaseWriteExecutor.execute(() -> {
            try {
                AppDatabase db = getDatabase(appContext);
                List<Meal> meals = db.mealDao().getMealsByDate(userId, date);
                callback.onSuccess(meals);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public static void getFoodsInMeal(int mealId, DatabaseCallback<List<Food>> callback) {
        databaseWriteExecutor.execute(() -> {
            try {
                AppDatabase db = getDatabase(appContext);
                List<Food> foods = db.foodDao().getAllInMeal(mealId);
                callback.onSuccess(foods); // Trả về danh sách Food
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public static void getTotalCaloriesByDate(int userId, String date, DatabaseCallback<Double> callback) {
        databaseWriteExecutor.execute(() -> {
            try {
                AppDatabase db = getDatabase(appContext);
                Double totalCalories = db.mealDao().getTotalCaloriesByDate(userId, date);
                callback.onSuccess(totalCalories != null ? totalCalories : 0.0);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    // ========== NUTRITION GOALS ==========
    public static void getNutritionGoal(int userId, DatabaseCallback<NutritionGoal> callback) {
        databaseWriteExecutor.execute(() -> {
            try {
                AppDatabase db = getDatabase(appContext);
                NutritionGoal goal = db.nutritionGoalDao().getByUserId(userId);
                callback.onSuccess(goal);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public static void setNutritionGoal(NutritionGoal goal, DatabaseCallback<NutritionGoal> callback) {
        databaseWriteExecutor.execute(() -> {
            try {
                AppDatabase db = getDatabase(appContext);
                // Delete existing goal first
                db.nutritionGoalDao().deleteByUserId(goal.userId);
                // Insert new goal
                long id = db.nutritionGoalDao().insert(goal);
                goal.id = (int) id;
                callback.onSuccess(goal);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    // ========== AI SUGGESTIONS (Mock for now) ==========
    public static void getSuggestedMeals(int userId, DatabaseCallback<List<Food>> callback) {
        databaseWriteExecutor.execute(() -> {
            try {
                // TODO: Replace with actual AI logic
                AppDatabase db = getDatabase(appContext);
                List<Ingredient> ingredients = db.ingredientDao().getAllByUserId(userId);

                // Mock AI suggestions based on available ingredients
                List<Food> suggestions = generateMockSuggestions(ingredients);
                callback.onSuccess(suggestions);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    // ========== HELPER METHODS ==========
    private static List<Food> generateMockSuggestions(List<Ingredient> ingredients) {
        // TODO: Implement AI logic or API call
        // For now, return mock data
        return List.of();
    }


    // ========== FOOD MANAGEMENT ==========
    public static void addFood(Food food, DatabaseCallback<Food> callback) {
        databaseWriteExecutor.execute(() -> {
            try {
                AppDatabase db = getDatabase(appContext);
                long id = db.foodDao().insert(food);
                food.id = (int) id;
                callback.onSuccess(food);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    public static void updateFood(Food food, DatabaseCallback<Boolean> callback) {
        databaseWriteExecutor.execute(() -> {
            try {
                AppDatabase db = getDatabase(appContext);
                int rowsUpdated = db.foodDao().update(food);
                callback.onSuccess(rowsUpdated > 0);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }

    // ========== CALLBACK INTERFACE ==========
    public interface DatabaseCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}