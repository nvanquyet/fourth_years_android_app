package com.example.android_exam.data.local.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.android_exam.data.local.entity.*;
import com.example.android_exam.data.local.dao.*;
import com.example.android_exam.utils.Converters;

/**
 * Room Database class - Only handles database configuration and DAO access
 * Business logic moved to LocalDataRepository
 */
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

    // Get database instance
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "food_database"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }

    // Method to close database (for testing or cleanup)
    public static void closeDatabase() {
        if (INSTANCE != null) {
            INSTANCE.close();
            INSTANCE = null;
        }
    }
}