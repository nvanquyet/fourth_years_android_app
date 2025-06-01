package com.example.android_exam.data.local.dao;

import androidx.room.*;
import com.example.android_exam.data.local.entity.Ingredient;
import java.util.List;

@Dao
public interface IngredientDao {
    @Query("SELECT * FROM ingredients WHERE userId = :userId ORDER BY expiryDate ASC")
    List<Ingredient> getAllByUserId(int userId);

    @Query("SELECT * FROM ingredients WHERE userId = :userId AND date(expiryDate) <= date('now', '+' || :daysAhead || ' days') ORDER BY expiryDate ASC")
    List<Ingredient> getExpiringIngredients(int userId, int daysAhead);

    @Query("SELECT * FROM ingredients WHERE userId = :userId ORDER BY date(expiryDate) ASC LIMIT :limit")
    List<Ingredient> getTopExpiringIngredients(int userId, int limit);

    @Query("SELECT * FROM ingredients WHERE id = :id")
    Ingredient getById(int id);

    @Insert
    long insert(Ingredient ingredient);

    @Update
    int update(Ingredient ingredient);

    @Delete
    int delete(Ingredient ingredient);

    @Query("DELETE FROM ingredients WHERE id = :id")
    int deleteById(int id);

    @Query("UPDATE ingredients SET quantity = quantity - :usedQuantity WHERE id = :id")
    int reduceQuantity(int id, double usedQuantity);
}
