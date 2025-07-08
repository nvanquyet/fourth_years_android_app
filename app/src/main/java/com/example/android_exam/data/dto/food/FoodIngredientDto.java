package com.example.android_exam.data.dto.food;

import com.example.android_exam.data.models.enums.IngredientUnit;

import java.io.Serializable;
import java.math.BigDecimal;

public class FoodIngredientDto implements Serializable {

    private Integer ingredientId;

    private BigDecimal quantity;

    private IngredientUnit unit;

    // For response
    private String ingredientName;

    // Constructors
    public FoodIngredientDto() {}

    public FoodIngredientDto(Integer ingredientId, BigDecimal quantity, IngredientUnit unit) {
        this.ingredientId = ingredientId;
        this.quantity = quantity;
        this.unit = unit;
    }

    public FoodIngredientDto(Integer ingredientId, BigDecimal quantity, IngredientUnit unit, String ingredientName) {
        this.ingredientId = ingredientId;
        this.quantity = quantity;
        this.unit = unit;
        this.ingredientName = ingredientName;
    }

    // Getters and Setters
    public Integer getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Integer ingredientId) {
        this.ingredientId = ingredientId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public IngredientUnit getUnit() {
        return unit;
    }

    public void setUnit(IngredientUnit unit) {
        this.unit = unit;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }
}