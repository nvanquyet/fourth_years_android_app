package com.example.android_exam.data.local.database;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.android_exam.utils.DateConverter;
import com.example.android_exam.data.local.dao.IngredientDao;
import com.example.android_exam.data.local.dao.MealHistoryDao;
import com.example.android_exam.data.local.dao.NutritionTargetDao;
import com.example.android_exam.data.local.entity.IngredientEntity;
import com.example.android_exam.data.local.entity.MealHistoryEntity;
import com.example.android_exam.data.local.entity.NutritionTargetEntity;

@Database(
        entities = {
                IngredientEntity.class,
                MealHistoryEntity.class,
                NutritionTargetEntity.class
        },
        version = 1,
        exportSchema = false
)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract IngredientDao ingredientDao();
    public abstract MealHistoryDao mealHistoryDao();
    public abstract NutritionTargetDao nutritionTargetDao();
}