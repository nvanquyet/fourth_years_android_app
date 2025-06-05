package com.example.android_exam.data.local.dao;

import androidx.room.*;
import com.example.android_exam.data.local.entity.NutritionGoal;

@Dao
public interface NutritionGoalDao {
    @Query("SELECT * FROM nutrition_goals WHERE userId = :userId LIMIT 1")
    NutritionGoal getByUserId(int userId);

    @Insert
    long insert(NutritionGoal goal);

    @Update
    int update(NutritionGoal goal);

    @Query("DELETE FROM nutrition_goals WHERE userId = :userId")
    int deleteByUserId(int userId);
}