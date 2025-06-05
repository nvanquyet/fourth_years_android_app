package com.example.android_exam.data.local.dao;

import androidx.room.*;
import com.example.android_exam.data.local.entity.Meal;
import java.util.List;

@Dao
public interface MealDao {
    @Query("SELECT * FROM meals WHERE userId = :userId ORDER BY dateTimestamp DESC")
    List<Meal> getAllByUserId(int userId);

    @Query("SELECT * FROM meals WHERE userId = :userId AND dateTimestamp BETWEEN :startDate AND :endDate ORDER BY dateTimestamp DESC")
    List<Meal> getMealsByDateRange(int userId, long startDate, long endDate);

    @Query("SELECT SUM(totalCalories) FROM meals WHERE userId = :userId AND dateTimestamp BETWEEN :startDate AND :endDate")
    Double getTotalCaloriesByDateRange(int userId, long startDate, long endDate);

    @Query("SELECT * FROM meals WHERE userId = :userId AND mealType = :mealType AND dateTimestamp BETWEEN :startDate AND :endDate ORDER BY dateTimestamp DESC")
    List<Meal> getMealsByTypeAndDateRange(int userId, String mealType, long startDate, long endDate);

    @Query("SELECT * FROM meals WHERE userId = :userId AND DATE(dateTimestamp/1000, 'unixepoch') = :date ORDER BY dateTimestamp DESC")
    List<Meal> getMealsByDate(int userId, String date);

    @Query("SELECT SUM(totalCalories) FROM meals WHERE userId = :userId AND DATE(dateTimestamp/1000, 'unixepoch') = :date")
    Double getTotalCaloriesByDate(int userId, String date);

    @Query("SELECT COUNT(*) FROM meals WHERE userId = :userId")
    int getMealCountByUser(int userId);

    @Query("SELECT * FROM meals WHERE id = :id LIMIT 1")
    Meal getById(int id);

    @Insert
    long insert(Meal meal);

    @Update
    int update(Meal meal);

    @Delete
    int delete(Meal meal);

    @Query("DELETE FROM meals WHERE id = :id")
    int deleteById(int id);
}