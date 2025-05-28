package com.example.android_exam.data.repository;
import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.example.android_exam.data.local.database.AppDatabase;
import com.example.android_exam.data.local.dao.IngredientDao;
import com.example.android_exam.data.local.dao.MealHistoryDao;
import com.example.android_exam.data.local.dao.NutritionTargetDao;
import com.example.android_exam.data.local.entity.*;

import java.util.Date;
import java.util.List;

public class FoodRepository {

    private final IngredientDao ingredientDao;
    private final MealHistoryDao mealHistoryDao;
    private final NutritionTargetDao nutritionTargetDao;

    public FoodRepository(Application application) {
        AppDatabase db = Room.databaseBuilder(application,
                        AppDatabase.class, "food_database")
                .fallbackToDestructiveMigration()
                .build();

        ingredientDao = db.ingredientDao();
        mealHistoryDao = db.mealHistoryDao();
        nutritionTargetDao = db.nutritionTargetDao();
    }

    // Ingredient
    public LiveData<List<IngredientEntity>> getAllIngredients() {
        return ingredientDao.getAllIngredients();
    }

    public void insertIngredient(IngredientEntity ingredient) {
        new Thread(() -> ingredientDao.insert(ingredient)).start();
    }

    // Meal History
    public LiveData<List<MealHistoryEntity>> getHistoryByDate(Date date) {
        return mealHistoryDao.getHistoryByDate(date);
    }

    public void insertMealHistory(MealHistoryEntity history) {
        new Thread(() -> mealHistoryDao.insert(history)).start();
    }

    // Nutrition Target
    public LiveData<NutritionTargetEntity> getNutritionTarget() {
        return nutritionTargetDao.getCurrentTarget();
    }

    public void updateNutritionTarget(NutritionTargetEntity target) {
        new Thread(() -> nutritionTargetDao.update(target)).start();
    }
}
