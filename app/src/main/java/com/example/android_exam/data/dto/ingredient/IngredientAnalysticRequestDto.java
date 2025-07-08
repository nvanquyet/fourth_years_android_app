package com.example.android_exam.data.dto.ingredient;


import java.io.File;

public class IngredientAnalysticRequestDto {
    private File image;

    public IngredientAnalysticRequestDto() {
    }

    public IngredientAnalysticRequestDto(File image) {
        this.image = image;
    }

    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }
}