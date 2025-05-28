package com.example.android_exam.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import com.example.android_exam.data.local.entity.MealHistoryEntity;

import java.util.Date;
import java.util.List;

@Dao
public interface MealHistoryDao {

    @Query("SELECT * FROM meal_history ORDER BY date DESC")
    LiveData<List<MealHistoryEntity>> getAllHistory();

    @Query("SELECT * FROM meal_history WHERE date = :targetDate")
    LiveData<List<MealHistoryEntity>> getHistoryByDate(Date targetDate);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MealHistoryEntity history);

    @Delete
    void delete(MealHistoryEntity history);

    @Query("DELETE FROM meal_history")
    void deleteAll();
}