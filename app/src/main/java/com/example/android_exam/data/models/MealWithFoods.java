package com.example.android_exam.data.models;

import com.example.android_exam.data.local.entity.Food;
import com.example.android_exam.data.local.entity.Meal;

import java.util.List;

public class MealWithFoods {
    public Meal meal;
    public List<Food> foods;

    public MealWithFoods(Meal meal, List<Food> foods) {
        this.meal = meal;
        this.foods = foods;
    }
}