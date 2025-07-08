package com.example.android_exam.data.dto.ingredient;

import com.example.android_exam.data.models.enums.IngredientCategory;
import com.example.android_exam.data.models.enums.IngredientUnit;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class IngredientFilterDto {
    @SerializedName("category")
    private IngredientCategory category;
    @SerializedName("isExpired")
    private Boolean isExpired;
    @SerializedName("searchTerm")
    private String searchTerm;
    @SerializedName("sortBy")
    private String sortBy;
    @SerializedName("sortDirection")
    private String sortDirection;

    // Getters and Setters
    public IngredientCategory getCategory() { return category; }
    public void setCategory(IngredientCategory category) { this.category = category; }

    public Boolean getIsExpired() { return isExpired; }
    public void setIsExpired(Boolean isExpired) { this.isExpired = isExpired; }

    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }
}
