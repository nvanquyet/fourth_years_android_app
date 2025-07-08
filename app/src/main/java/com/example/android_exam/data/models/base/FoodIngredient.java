package com.example.android_exam.data.models.base;

import com.example.android_exam.data.models.enums.IngredientUnit;
import com.google.gson.annotations.SerializedName;

public class FoodIngredient extends BaseEntity {
    @SerializedName("foodId")
    private int foodId;
    @SerializedName("ingredientId")
    private int ingredientId;
    @SerializedName("quantity")
    private double quantity;
    @SerializedName("unit")
    private IngredientUnit unit;

    // Getters and Setters
    public int getFoodId() { return foodId; }
    public void setFoodId(int foodId) { this.foodId = foodId; }
    public int getIngredientId() { return ingredientId; }
    public void setIngredientId(int ingredientId) { this.ingredientId = ingredientId; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public IngredientUnit getUnit() { return unit; }
    public void setUnit(IngredientUnit unit) { this.unit = unit; }
}