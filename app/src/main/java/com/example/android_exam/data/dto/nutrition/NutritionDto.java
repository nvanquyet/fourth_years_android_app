package com.example.android_exam.data.dto.nutrition;

import com.example.android_exam.data.models.enums.MealType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NutritionDto {
    private int mealId;
    private MealType mealType; // Bạn cần tạo enum MealType bên Java
    private Date mealDate;

    private double totalCalories;
    private double totalProtein;
    private double totalCarbs;
    private double totalFat;
    private double totalFiber;

    private List<FoodNutritionDto> foods = new ArrayList<>();

    // Getters and Setters
    public int getMealId() {
        return mealId;
    }
    public void setMealId(int mealId) {
        this.mealId = mealId;
    }
    public MealType getMealType() {
        return mealType;
    }
    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }
    public Date getMealDate() {
        return mealDate;
    }
    public void setMealDate(Date mealDate) {
        this.mealDate = mealDate;
    }
    public double getTotalCalories() {
        return totalCalories;
    }
    public void setTotalCalories(double totalCalories) {
        this.totalCalories = totalCalories;
    }
    public double getTotalProtein() {
        return totalProtein;
    }
    public void setTotalProtein(double totalProtein) {
        this.totalProtein = totalProtein;
    }
    public double getTotalCarbs() {
        return totalCarbs;
    }
    public void setTotalCarbs(double totalCarbs) {
        this.totalCarbs = totalCarbs;
    }
    public double getTotalFat() {
        return totalFat;
    }
    public void setTotalFat(double totalFat) {
        this.totalFat = totalFat;
    }
    public double getTotalFiber() {
        return totalFiber;
    }
    public void setTotalFiber(double totalFiber) {
        this.totalFiber = totalFiber;
    }
    public List<FoodNutritionDto> getFoods() {
        return foods;
    }
    public void setFoods(List<FoodNutritionDto> foods) {
        this.foods = foods;
    }
    @Override
    public String toString() {
        return "NutritionDto{" +
                "mealId=" + mealId +
                ", mealType=" + mealType +
                ", mealDate=" + mealDate +
                ", totalCalories=" + totalCalories +
                ", totalProtein=" + totalProtein +
                ", totalCarbs=" + totalCarbs +
                ", totalFat=" + totalFat +
                ", totalFiber=" + totalFiber +
                ", foods=" + foods +
                '}';
    }
}