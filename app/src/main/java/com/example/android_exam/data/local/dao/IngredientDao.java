package com.example.android_exam.data.local.dao;


import androidx.lifecycle.LiveData;
import androidx.room.*;

import com.example.android_exam.data.local.entity.IngredientEntity;

import java.util.Date;
import java.util.List;

@Dao
public interface IngredientDao {

    @Query("SELECT * FROM ingredients ORDER BY name ASC")
    LiveData<List<IngredientEntity>> getAllIngredients();

    @Query("SELECT * FROM ingredients WHERE expiration_date IS NOT NULL AND expiration_date < :today")
    LiveData<List<IngredientEntity>> getExpiredIngredients(Date today);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(IngredientEntity ingredient);

    @Update
    void update(IngredientEntity ingredient);

    @Delete
    void delete(IngredientEntity ingredient);

    @Query("DELETE FROM ingredients")
    void deleteAll();
}