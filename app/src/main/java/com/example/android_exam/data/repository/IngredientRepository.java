package com.example.android_exam.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.android_exam.data.local.database.AppDatabase;
import com.example.android_exam.data.local.entity.Ingredient;

import java.util.List;
import java.util.ArrayList;

public class IngredientRepository {

    // Cache để tránh tạo quá nhiều LiveData objects
    private static MutableLiveData<List<Ingredient>> cachedExpiringIngredients;
    private static MutableLiveData<List<Ingredient>> cachedAllIngredients;
    private static int lastUserId = -1;

    public static LiveData<List<Ingredient>> getExpiringIngredients(int userId) {
        return getExpiringIngredients(userId, 10);
    }

    public static LiveData<List<Ingredient>> getExpiringIngredients(int userId, int days) {
        // Kiểm tra cache nếu cùng userId
        if (cachedExpiringIngredients != null && lastUserId == userId) {
            return cachedExpiringIngredients;
        }

        // Tạo mới nếu chưa có hoặc userId khác
        cachedExpiringIngredients = new MutableLiveData<>();
        lastUserId = userId;

        // Load data một lần
        loadExpiringIngredientsFromDB(userId, days);

        return cachedExpiringIngredients;
    }

    private static void loadExpiringIngredientsFromDB(int userId, int days) {
        AppDatabase.getExpiringIngredients(userId, days, new AppDatabase.DatabaseCallback<List<Ingredient>>() {
            @Override
            public void onSuccess(List<Ingredient> result) {
                if (cachedExpiringIngredients != null) {
                    cachedExpiringIngredients.postValue(result != null ? result : new ArrayList<>());
                }
            }

            @Override
            public void onError(String error) {
                if (cachedExpiringIngredients != null) {
                    cachedExpiringIngredients.postValue(new ArrayList<>());
                }
            }
        });
    }

    public static LiveData<List<Ingredient>> getAllIngredient(int userId) {
        // Tương tự cho getAllIngredient
        if (cachedAllIngredients != null && lastUserId == userId) {
            return cachedAllIngredients;
        }

        cachedAllIngredients = new MutableLiveData<>();

        AppDatabase.getAllIngredients(userId, new AppDatabase.DatabaseCallback<List<Ingredient>>() {
            @Override
            public void onSuccess(List<Ingredient> result) {
                if (cachedAllIngredients != null) {
                    cachedAllIngredients.postValue(result != null ? result : new ArrayList<>());
                }
            }

            @Override
            public void onError(String error) {
                if (cachedAllIngredients != null) {
                    cachedAllIngredients.postValue(new ArrayList<>());
                }
            }
        });

        return cachedAllIngredients;
    }

    // Method để refresh data khi cần
    public static void refreshExpiringIngredients(int userId) {
        if (cachedExpiringIngredients != null) {
            loadExpiringIngredientsFromDB(userId, 10);
        }
    }

    // Method để clear cache khi cần
    public static void clearCache() {
        cachedExpiringIngredients = null;
        cachedAllIngredients = null;
        lastUserId = -1;
    }
}