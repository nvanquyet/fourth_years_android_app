package com.example.android_exam.data.dto.food;

import java.io.File;

public class FoodAnalysticRequestDto {
    private File image;

    public FoodAnalysticRequestDto(File image) {
        this.image = image;
    }

    public File getImage() {
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }
}