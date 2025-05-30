package com.example.android_exam.data.local.dao;

import androidx.room.*;
import com.example.android_exam.data.local.entity.Food;
import java.util.List;

@Dao
public interface FoodDao {
    @Query("SELECT * FROM foods WHERE mealId = :mealId")
    List<Food> getAllInMeal(int mealId);

    @Query("SELECT * FROM foods WHERE id = :id")
    Food getById(int id);

    @Insert
    long insert(Food food);

    @Update
    int update(Food food);

    @Delete
    int delete(Food food);
}