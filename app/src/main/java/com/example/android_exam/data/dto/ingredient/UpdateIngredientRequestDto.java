package com.example.android_exam.data.dto.ingredient;

public class UpdateIngredientRequestDto extends CreateIngredientRequestDto {

    private Integer id;

    // Constructors
    public UpdateIngredientRequestDto() {
        super();
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}