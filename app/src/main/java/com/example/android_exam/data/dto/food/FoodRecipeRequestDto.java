package com.example.android_exam.data.dto.food;

import java.util.List;

public class FoodRecipeRequestDto {

    private String foodName = "";
    private List<FoodIngredientDto> ingredients;

    // Constructors
    public FoodRecipeRequestDto() {}

    public FoodRecipeRequestDto(String foodName) {
        this.foodName = foodName;
    }

    public FoodRecipeRequestDto(String foodName, List<FoodIngredientDto> ingredients) {
        this.foodName = foodName;
        this.ingredients = ingredients;
    }

    // Getters and Setters
    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public List<FoodIngredientDto> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<FoodIngredientDto> ingredients) {
        this.ingredients = ingredients;
    }
}