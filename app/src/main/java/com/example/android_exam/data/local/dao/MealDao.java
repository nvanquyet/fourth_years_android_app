package com.example.android_exam.data.local.dao;

import androidx.room.*;
import com.example.android_exam.data.local.entity.Meal;
import java.util.List;

@Dao
public interface MealDao {
    @Query("SELECT * FROM meals WHERE userId = :userId ORDER BY date DESC")
    List<Meal> getAllByUserId(int userId);

    @Query("SELECT * FROM meals WHERE userId = :userId AND date = :date")
    List<Meal> getMealsByDate(int userId, String date);

    @Query("SELECT SUM(totalCalories) FROM meals WHERE userId = :userId AND date = :date")
    Double getTotalCaloriesByDate(int userId, String date);

    @Insert
    long insert(Meal meal);

    @Update
    int update(Meal meal);

    @Delete
    int delete(Meal meal);
}