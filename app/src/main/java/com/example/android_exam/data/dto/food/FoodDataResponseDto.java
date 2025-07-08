package com.example.android_exam.data.dto.food;



import androidx.annotation.NonNull;

import com.example.android_exam.data.models.enums.MealType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FoodDataResponseDto implements Serializable {

    private int id;
    private String name = "";
    private String description;
    private String imageUrl;
    private int preparationTimeMinutes;
    private int cookingTimeMinutes;
    private BigDecimal calories;
    private BigDecimal protein;
    private BigDecimal carbohydrates;
    private BigDecimal fat;
    private BigDecimal fiber;
    private List<String> instructions = new ArrayList<>();
    private List<String> tips = new ArrayList<>();
    private int difficultyLevel = 1;
    private MealType mealType;
    private String mealDate;

    private String consumedAt; // Optional field for consumed time

    private List<FoodIngredientDto> ingredients = new ArrayList<>();

    // Constructors
    public FoodDataResponseDto() {}

    public FoodDataResponseDto(int id, String name, String description, String imageUrl,
                               int preparationTimeMinutes, int cookingTimeMinutes,
                               BigDecimal calories, BigDecimal protein, BigDecimal carbohydrates,
                               BigDecimal fat, BigDecimal fiber, List<String> instructions,
                               List<String> tips, int difficultyLevel, MealType mealType,
                               String mealDate, List<FoodIngredientDto> ingredients) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.preparationTimeMinutes = preparationTimeMinutes;
        this.cookingTimeMinutes = cookingTimeMinutes;
        this.calories = calories;
        this.protein = protein;
        this.carbohydrates = carbohydrates;
        this.fat = fat;
        this.fiber = fiber;
        this.instructions = instructions;
        this.tips = tips;
        this.difficultyLevel = difficultyLevel;
        this.mealType = mealType;
        this.mealDate = mealDate;
        this.ingredients = ingredients;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPreparationTimeMinutes() {
        return preparationTimeMinutes;
    }

    public void setPreparationTimeMinutes(int preparationTimeMinutes) {
        this.preparationTimeMinutes = preparationTimeMinutes;
    }

    public int getCookingTimeMinutes() {
        return cookingTimeMinutes;
    }

    public void setCookingTimeMinutes(int cookingTimeMinutes) {
        this.cookingTimeMinutes = cookingTimeMinutes;
    }

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

    public BigDecimal getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(BigDecimal carbohydrates) {
        this.carbohydrates = carbohydrates;
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

    public List<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }

    public List<String> getTips() {
        return tips;
    }

    public void setTips(List<String> tips) {
        this.tips = tips;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public String getMealDate() {
        return mealDate;
    }

    public void setMealDate(String mealDate) {
        this.mealDate = mealDate;
    }

    public List<FoodIngredientDto> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<FoodIngredientDto> ingredients) {
        this.ingredients = ingredients;
    }

    public String getConsumedAt() {
        return consumedAt;
    }
    public void setConsumedAt(String consumedAt) {
        this.consumedAt = consumedAt;
    }

    @NonNull
    @Override
    public String toString() {
        return new com.google.gson.Gson().toJson(this);
    }
}