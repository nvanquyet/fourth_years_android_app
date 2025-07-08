package com.example.android_exam.data.dto.ingredient;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IngredientSearchResultDto implements Serializable {
    @SerializedName("ingredients")
    private List<IngredientDataResponseDto> ingredients = new ArrayList<>();
    @SerializedName("totalCount")
    private int totalCount;

    // Getters and Setters
    public List<IngredientDataResponseDto> getIngredients() { return ingredients; }
    public void setIngredients(List<IngredientDataResponseDto> ingredients) { this.ingredients = ingredients; }
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
}
