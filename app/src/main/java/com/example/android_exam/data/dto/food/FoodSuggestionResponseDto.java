package com.example.android_exam.data.dto.food;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// FoodSuggestionResponseDto
public class FoodSuggestionResponseDto implements Serializable {

    private String name = "";
    private String image = "";
    private int difficulty;
    private BigDecimal kcal;
    private int prepTimeMinutes;
    private int cookTimeMinutes;
    private List<FoodIngredientDto> ingredients = new ArrayList<>();

    // Constructors
    public FoodSuggestionResponseDto() {}

    public FoodSuggestionResponseDto(String name, String image, int difficulty,
                                     BigDecimal kcal, int prepTimeMinutes, int cookTimeMinutes,
                                     List<FoodIngredientDto> ingredients) {
        this.name = name;
        this.image = image;
        this.difficulty = difficulty;
        this.kcal = kcal;
        this.prepTimeMinutes = prepTimeMinutes;
        this.cookTimeMinutes = cookTimeMinutes;
        this.ingredients = ingredients;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public BigDecimal getKcal() {
        return kcal;
    }

    public void setKcal(BigDecimal kcal) {
        this.kcal = kcal;
    }

    public int getPrepTimeMinutes() {
        return prepTimeMinutes;
    }

    public void setPrepTimeMinutes(int prepTimeMinutes) {
        this.prepTimeMinutes = prepTimeMinutes;
    }

    public int getCookTimeMinutes() {
        return cookTimeMinutes;
    }

    public void setCookTimeMinutes(int cookTimeMinutes) {
        this.cookTimeMinutes = cookTimeMinutes;
    }

    public List<FoodIngredientDto> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<FoodIngredientDto> ingredients) {
        this.ingredients = ingredients;
    }
}
