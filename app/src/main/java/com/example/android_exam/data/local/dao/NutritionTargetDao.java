package com.example.android_exam.data.local.dao;


import androidx.lifecycle.LiveData;
import androidx.room.*;

import com.example.android_exam.data.local.entity.NutritionTargetEntity;

import java.util.List;

@Dao
public interface NutritionTargetDao {

    @Query("SELECT * FROM nutrition_target LIMIT 1")
    LiveData<NutritionTargetEntity> getCurrentTarget();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(NutritionTargetEntity target);

    @Update
    void update(NutritionTargetEntity target);

    @Delete
    void delete(NutritionTargetEntity target);
}