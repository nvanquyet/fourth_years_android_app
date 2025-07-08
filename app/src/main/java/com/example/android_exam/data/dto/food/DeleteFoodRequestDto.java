package com.example.android_exam.data.dto.food;


import androidx.annotation.NonNull;

public class DeleteFoodRequestDto {

    private Integer id;

    // Constructors
    public DeleteFoodRequestDto() {}

    public DeleteFoodRequestDto(Integer id) {
        this.id = id;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return new com.google.gson.Gson().toJson(this);
    }
}