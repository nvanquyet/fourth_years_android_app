package com.example.android_exam.data.models.base;

import com.example.android_exam.data.models.enums.IngredientCategory;
import com.example.android_exam.data.models.enums.IngredientUnit;
import com.example.android_exam.utils.DateUtils;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.util.Date;

public class Ingredient extends BaseEntity {
    @SerializedName("name")
    private String name = "";
    @SerializedName("description")
    private String description;
    @SerializedName("quantity")
    private double quantity;
    @SerializedName("unit")
    private IngredientUnit unit = IngredientUnit.PIECE;
    @SerializedName("category")
    private IngredientCategory category = IngredientCategory.OTHER;
    @SerializedName("expiryDate")
    private Date expiryDate;
    @SerializedName("imageUrl")
    private String imageUrl;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public IngredientUnit getUnit() { return unit; }
    public void setUnit(IngredientUnit unit) { this.unit = unit; }
    public IngredientCategory getCategory() { return category; }
    public void setCategory(IngredientCategory category) { this.category = category; }
    public LocalDate getExpiryDate() { return DateUtils.convertToLocalDate(expiryDate); }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}