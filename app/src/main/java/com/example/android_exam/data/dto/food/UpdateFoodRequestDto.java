package com.example.android_exam.data.dto.food;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class UpdateFoodRequestDto extends CreateFoodRequestDto {

    private Integer id;

    // Constructors
    public UpdateFoodRequestDto() {
        super();
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public static UpdateFoodRequestDto fromFoodData(FoodDataResponseDto food) {
        UpdateFoodRequestDto dto = new UpdateFoodRequestDto();
        dto.setId(food.getId());
        dto.setName(food.getName());
        dto.setDescription(food.getDescription());
        dto.setPreparationTimeMinutes(food.getPreparationTimeMinutes());
        dto.setCookingTimeMinutes(food.getCookingTimeMinutes());
        dto.setCalories(food.getCalories());
        dto.setProtein(food.getProtein());
        dto.setCarbohydrates(food.getCarbohydrates());
        dto.setFat(food.getFat());
        dto.setFiber(food.getFiber());
        dto.setMealType(food.getMealType());
        dto.setConsumedAt(food.getConsumedAt());
        dto.setMealDate(food.getMealDate());
        dto.setDifficultyLevel(food.getDifficultyLevel());
        dto.setIngredients(new ArrayList<>(food.getIngredients()));
        dto.setInstructions(new ArrayList<>(food.getInstructions()));
        return dto;
    }

    @NonNull
    @Override
    public String toString() {
        return new com.google.gson.Gson().toJson(this);
    }
}