package com.example.android_exam.viewmodel;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.android_exam.data.local.entity.IngredientEntity;
import com.example.android_exam.data.local.entity.MealHistoryEntity;
import com.example.android_exam.data.local.entity.NutritionTargetEntity;
import com.example.android_exam.data.repository.FoodRepository;

import java.util.Date;
import java.util.List;

public class FoodViewModel extends AndroidViewModel {

    private final FoodRepository repository;

    public FoodViewModel(@NonNull Application application) {
        super(application);
        repository = new FoodRepository(application);
    }

    public LiveData<List<IngredientEntity>> getAllIngredients() {
        return repository.getAllIngredients();
    }

    public void insertIngredient(IngredientEntity ingredient) {
        repository.insertIngredient(ingredient);
    }

    public LiveData<List<MealHistoryEntity>> getMealHistory(Date date) {
        return repository.getHistoryByDate(date);
    }

    public void insertMealHistory(MealHistoryEntity history) {
        repository.insertMealHistory(history);
    }

    public LiveData<NutritionTargetEntity> getNutritionTarget() {
        return repository.getNutritionTarget();
    }

    public void updateNutritionTarget(NutritionTargetEntity target) {
        repository.updateNutritionTarget(target);
    }
}
