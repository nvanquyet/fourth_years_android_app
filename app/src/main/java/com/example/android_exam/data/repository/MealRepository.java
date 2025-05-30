package com.example.android_exam.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.android_exam.data.local.database.AppDatabase;
import com.example.android_exam.data.local.dao.MealDao;
import com.example.android_exam.data.local.dao.FoodDao;
import com.example.android_exam.data.local.entity.Meal;
import com.example.android_exam.data.local.entity.Food;
import java.util.List;

public class MealRepository {

    public static LiveData<List<Meal>> getMealsByDate(int userId, String date) {
        MutableLiveData<List<Meal>> liveData = new MutableLiveData<>();

        AppDatabase.getMealsByDate(userId, date, new AppDatabase.DatabaseCallback<List<Meal>>() {
            @Override
            public void onSuccess(List<Meal> result) {
                liveData.postValue(result);
            }

            @Override
            public void onError(String error) {
                liveData.postValue(null); // Hoặc xử lý lỗi theo cách khác
            }
        });

        return liveData;
    }

    public static LiveData<List<Food>>getFoodsByMealId(int mealId) {
        MutableLiveData<List<Food>> foodData = new MutableLiveData<>();

        AppDatabase.getFoodsInMeal(mealId, new AppDatabase.DatabaseCallback<List<Food>>() {
            @Override
            public void onSuccess(List<Food> result) {
                foodData.postValue(result);
            }

            @Override
            public void onError(String error) {
                foodData.postValue(null); // Hoặc xử lý lỗi theo cách khác
            }
        });

        return foodData;
    }
}