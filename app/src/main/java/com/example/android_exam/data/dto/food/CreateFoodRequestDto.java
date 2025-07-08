package com.example.android_exam.data.dto.food;


import com.example.android_exam.data.models.enums.MealType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CreateFoodRequestDto {

    private String name = "";

    private String description;

    private Integer preparationTimeMinutes;


    private Integer cookingTimeMinutes;


    private BigDecimal calories;


    private BigDecimal protein;


    private BigDecimal carbohydrates;


    private BigDecimal fat;


    private BigDecimal fiber;


    private List<String> instructions = new ArrayList<>();


    private List<String> tips = new ArrayList<>();

    // Difficulty level (1-5 scale)
    private int difficultyLevel = 1;

    private String mealDate;

    private MealType mealType = MealType.BREAKFAST;

    private String consumedAt;

    private List<FoodIngredientDto> ingredients = new ArrayList<>();

    // Constructors
    public CreateFoodRequestDto() {}

    // Getters and Setters
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

    public Integer getPreparationTimeMinutes() {
        return preparationTimeMinutes;
    }

    public void setPreparationTimeMinutes(Integer preparationTimeMinutes) {
        this.preparationTimeMinutes = preparationTimeMinutes;
    }

    public Integer getCookingTimeMinutes() {
        return cookingTimeMinutes;
    }

    public void setCookingTimeMinutes(Integer cookingTimeMinutes) {
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

    public String getMealDate() {
        return mealDate;
    }

    public void setMealDate(String mealDate) {
        this.mealDate = mealDate;
    }

    public String getConsumedAt() {
        return consumedAt;
    }

    public void setConsumedAt(String consumedAt) {
        this.consumedAt = consumedAt;
    }

    public MealType getMealType() {
        return mealType;
    }

    public void setMealType(MealType mealType) {
        this.mealType = mealType;
    }

    public List<FoodIngredientDto> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<FoodIngredientDto> ingredients) {
        this.ingredients = ingredients;
    }


    public static CreateFoodRequestDto fromFoodData(FoodDataResponseDto foodData) {
        CreateFoodRequestDto dto = new CreateFoodRequestDto();
        dto.setName(foodData.getName());
        dto.setDescription(foodData.getDescription());
        dto.setPreparationTimeMinutes(foodData.getPreparationTimeMinutes());
        dto.setCookingTimeMinutes(foodData.getCookingTimeMinutes());
        dto.setCalories(foodData.getCalories());
        dto.setProtein(foodData.getProtein());
        dto.setCarbohydrates(foodData.getCarbohydrates());
        dto.setFat(foodData.getFat());
        dto.setFiber(foodData.getFiber());
        dto.setConsumedAt(foodData.getConsumedAt());
        dto.setInstructions(new ArrayList<>(foodData.getInstructions()));
        dto.setTips(new ArrayList<>(foodData.getTips()));
        dto.setDifficultyLevel(foodData.getDifficultyLevel());
        dto.setMealDate(foodData.getMealDate());
        dto.setMealType(foodData.getMealType());
        dto.setIngredients(new ArrayList<>(foodData.getIngredients()));
        return dto;
    }
}