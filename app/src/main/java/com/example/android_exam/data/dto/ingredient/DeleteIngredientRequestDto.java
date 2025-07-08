package com.example.android_exam.data.dto.ingredient;


public class DeleteIngredientRequestDto {

    private Integer id;

    // Constructors
    public DeleteIngredientRequestDto() {}

    public DeleteIngredientRequestDto(Integer id) {
        this.id = id;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}