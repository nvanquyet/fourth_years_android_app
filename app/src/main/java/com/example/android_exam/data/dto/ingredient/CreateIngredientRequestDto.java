package com.example.android_exam.data.dto.ingredient;

import com.example.android_exam.data.models.enums.IngredientCategory;
import com.example.android_exam.data.models.enums.IngredientUnit;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CreateIngredientRequestDto {

    @JsonProperty("name")
    private String name = "";

    private String description;

    @JsonProperty("quantity")
    private BigDecimal quantity;

    @JsonProperty("unit")
    private IngredientUnit unit;


    private IngredientCategory category;


    private String expiryDate;


    // Constructors
    public CreateIngredientRequestDto() {}

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

    public IngredientCategory getCategory() {
        return category;
    }

    public void setCategory(IngredientCategory category) {
        this.category = category;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String toJson() {
        return new com.google.gson.Gson().toJson(this);
    }

}