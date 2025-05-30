package com.example.android_exam.data.models;

import java.util.List;

public class DiaryWithMeals {
    public String date;
    public List<MealWithFoods> mealsWithFoods;

    public DiaryWithMeals(String date, List<MealWithFoods> mealsWithFoods) {
        this.date = date;
        this.mealsWithFoods = mealsWithFoods;
    }
}