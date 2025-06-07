package com.example.android_exam.data.local.dao;

import androidx.room.*;
import com.example.android_exam.data.local.entity.Ingredient;
import java.util.List;

@Dao
public interface IngredientDao {

    // ========== BASIC QUERIES ==========
    @Query("SELECT * FROM ingredients WHERE userId = :userId ORDER BY expiryDateTimestamp ASC")
    List<Ingredient> getAllByUserId(int userId);

    @Query("SELECT * FROM ingredients WHERE id = :id LIMIT 1")
    Ingredient getById(int id);

    @Query("SELECT * FROM ingredients WHERE userId = :userId AND name LIKE :name ORDER BY expiryDateTimestamp ASC")
    List<Ingredient> searchByName(int userId, String name);

    @Query("SELECT * FROM ingredients WHERE userId = :userId AND category = :category ORDER BY expiryDateTimestamp ASC")
    List<Ingredient> getByCategory(int userId, String category);

    // ========== EXPIRY-RELATED QUERIES ==========
    @Query("SELECT * FROM ingredients WHERE userId = :userId AND expiryDateTimestamp <= :threshold ORDER BY expiryDateTimestamp ASC")
    List<Ingredient> getExpiringIngredients(int userId, long threshold);

    @Query("SELECT * FROM ingredients WHERE userId = :userId ORDER BY expiryDateTimestamp ASC LIMIT :limit")
    List<Ingredient> getTopExpiringIngredients(int userId, int limit);

    @Query("SELECT * FROM ingredients WHERE userId = :userId AND expiryDateTimestamp < :currentTime ORDER BY expiryDateTimestamp ASC")
    List<Ingredient> getExpiredIngredients(int userId, long currentTime);

    @Query("SELECT * FROM ingredients WHERE userId = :userId AND expiryDateTimestamp BETWEEN :currentTime AND :thresholdTime ORDER BY expiryDateTimestamp ASC")
    List<Ingredient> getExpiringSoonIngredients(int userId, long currentTime, long thresholdTime);

    // ========== AGGREGATION QUERIES ==========
    @Query("SELECT COUNT(*) FROM ingredients WHERE userId = :userId")
    int getIngredientCountByUser(int userId);

    @Query("SELECT COUNT(*) FROM ingredients WHERE userId = :userId AND expiryDateTimestamp < :currentTime")
    int getExpiredCount(int userId, long currentTime);

    @Query("SELECT COUNT(*) FROM ingredients WHERE userId = :userId AND expiryDateTimestamp BETWEEN :currentTime AND :thresholdTime")
    int getExpiringSoonCount(int userId, long currentTime, long thresholdTime);

    @Query("SELECT DISTINCT category FROM ingredients WHERE userId = :userId AND category IS NOT NULL AND category != '' ORDER BY category")
    List<String> getAllCategories(int userId);

    // ========== LOW STOCK QUERIES ==========
    @Query("SELECT * FROM ingredients WHERE userId = :userId AND quantity <= :threshold ORDER BY quantity ASC")
    List<Ingredient> getLowStockIngredients(int userId, double threshold);

    @Query("SELECT * FROM ingredients WHERE userId = :userId AND quantity <= 0 ORDER BY name ASC")
    List<Ingredient> getOutOfStockIngredients(int userId);

    // ========== CRUD OPERATIONS ==========
    @Insert
    long insert(Ingredient ingredient);

    @Insert
    void insertAll(List<Ingredient> ingredients);

    @Update
    int update(Ingredient ingredient);

    @Delete
    int delete(Ingredient ingredient);

    @Query("DELETE FROM ingredients WHERE id = :id")
    int deleteById(int id);

    @Query("DELETE FROM ingredients WHERE userId = :userId")
    int deleteAllByUserId(int userId);

    // ========== QUANTITY MANAGEMENT ==========
    @Query("UPDATE ingredients SET quantity = quantity - :usedQuantity WHERE id = :id AND quantity >= :usedQuantity")
    int reduceQuantity(int id, double usedQuantity);

    @Query("UPDATE ingredients SET quantity = quantity + :addedQuantity WHERE id = :id")
    int increaseQuantity(int id, double addedQuantity);

    @Query("UPDATE ingredients SET quantity = :newQuantity WHERE id = :id")
    int updateQuantity(int id, double newQuantity);

    // ========== BATCH OPERATIONS ==========
    @Query("DELETE FROM ingredients WHERE userId = :userId AND expiryDateTimestamp < :currentTime")
    int deleteExpiredIngredients(int userId, long currentTime);

    @Query("UPDATE ingredients SET quantity = 0 WHERE userId = :userId AND expiryDateTimestamp < :currentTime")
    int markExpiredIngredientsAsEmpty(int userId, long currentTime);

    // ========== ADVANCED QUERIES ==========
    @Query("""
        SELECT * FROM ingredients
        WHERE userId = :userId
        AND (:category IS NULL OR category = :category)
        AND (:minQuantity IS NULL OR quantity >= :minQuantity)
        AND (:maxQuantity IS NULL OR quantity <= :maxQuantity)
        ORDER BY
            CASE WHEN :sortBy = 'name' THEN name END ASC,
            CASE WHEN :sortBy = 'expiry' THEN expiryDateTimestamp END ASC,
            CASE WHEN :sortBy = 'quantity' THEN quantity END DESC
        """)
    List<Ingredient> getIngredientsWithFilters(
            int userId,
            String category,
            Double minQuantity,
            Double maxQuantity,
            String sortBy
    );

    // ========== HELPER CLASSES ==========
    class CategoryStats {
        public String category;
        public int count;
        public double totalCalories;
    }
}
