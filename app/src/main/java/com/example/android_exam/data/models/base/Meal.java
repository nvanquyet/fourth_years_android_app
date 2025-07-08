package com.example.android_exam.data.models.base;

import com.example.android_exam.data.models.enums.MealType;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Meal extends BaseEntity {
    @SerializedName("mealType")
    private MealType mealType;
    @SerializedName("mealDate")
    private Date mealDate;
    @SerializedName("consumedAt")
    private Date consumedAt;
    @SerializedName("totalCalories")
    private double totalCalories;
    @SerializedName("totalProtein")
    private double totalProtein;
    @SerializedName("totalCarbs")
    private double totalCarbs;
    @SerializedName("totalFat")
    private double totalFat;

    @SerializedName("totalFiber")
    private double totalFiber;


    // Getters and Setters
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

    public Date getConsumedAt() {
        return consumedAt;
    }

    public void setConsumedAt(Date consumedAt) {
        this.consumedAt = consumedAt;
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
}