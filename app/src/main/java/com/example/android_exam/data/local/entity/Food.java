package com.example.android_exam.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "foods",
        foreignKeys = @ForeignKey(
                entity = Meal.class,
                parentColumns = "id",
                childColumns = "mealId",
                onDelete = CASCADE
        ),
        indices = {@Index(value = {"mealId"})}
)
public class Food {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int mealId;

    @NonNull
    public String name;

    public double totalCalories;

    public String description;

    public String ingredients; // Lưu nguyên liệu dưới dạng JSON hoặc String, ví dụ: "200g chicken, 100g rice"

    public String preparation; // Cách chế biến từ API

    public Food() {
        name = "Unknown";
        totalCalories = 0.0;
        description = "";
        ingredients = "";
        preparation = "";
    }

    public Food(@NonNull String name, double totalCalories, String description,
                String ingredients, String preparation, int mealId) {
        this.name = name.isEmpty() ? "Unknown" : name;
        this.totalCalories = totalCalories;
        this.description = description;
        this.ingredients = ingredients;
        this.preparation = preparation;
        this.mealId = mealId;
    }

    // Getters and setters
    @NonNull
    public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name.isEmpty() ? "Unknown" : name; }

    public double getTotalCalories() { return totalCalories; }
    public void setTotalCalories(double totalCalories) { this.totalCalories = totalCalories; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIngredients() { return ingredients; }
    public void setIngredients(String ingredients) { this.ingredients = ingredients; }

    public String getPreparation() { return preparation; }
    public void setPreparation(String preparation) { this.preparation = preparation; }

    public int getMealId() { return mealId; }
    public void setMealId(int mealId) { this.mealId = mealId; }
}