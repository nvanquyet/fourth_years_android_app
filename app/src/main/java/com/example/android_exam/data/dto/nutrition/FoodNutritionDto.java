package com.example.android_exam.data.dto.nutrition;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

public class FoodNutritionDto {

    @SerializedName("foodId")
    private int foodId;

    @SerializedName("foodName")
    private String foodName;

    @SerializedName("calories")
    private BigDecimal calories;

    @SerializedName("protein")
    private BigDecimal protein;

    @SerializedName("carbs")
    private BigDecimal carbs;

    @SerializedName("fat")
    private BigDecimal fat;

    @SerializedName("fiber")
    private BigDecimal fiber;

    public FoodNutritionDto() {
        // Default constructor
    }

    public FoodNutritionDto(int foodId, String foodName, BigDecimal calories, BigDecimal protein, BigDecimal carbs, BigDecimal fat, BigDecimal fiber) {
        this.foodId = foodId;
        this.foodName = foodName;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.fat = fat;
        this.fiber = fiber;
    }

    // Getters and Setters for foodId and foodName
    public int getFoodId() {
        return foodId;
    }
    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }
    public String getFoodName() {
        return foodName;
    }
    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    // Getters and Setters
    public BigDecimal getCalories() {
        return calories;
    }
    public void setCalories(BigDecimal calories) {
        this.calories = calories;
    }
    public BigDecimal getProtein() {
        return protein;
    }
    public void setProtein(BigDecimal protein) {
        this.protein = protein;
    }
    public BigDecimal getCarbs() {
        return carbs;
    }
    public void setCarbs(BigDecimal carbs) {
        this.carbs = carbs;
    }
    public BigDecimal getFat() {
        return fat;
    }

    public void setFat(BigDecimal fat) {
        this.fat = fat;
    }
    public BigDecimal getFiber() {
        return fiber;
    }
    public void setFiber(BigDecimal fiber) {
        this.fiber = fiber;
    }
}
