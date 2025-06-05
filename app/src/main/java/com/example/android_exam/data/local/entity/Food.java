package com.example.android_exam.data.local.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

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

    public Food() {
        name = "Unknown"; // Giá trị mặc định
    }

    public Food(@NonNull String name, double totalCalories, String description, int mealId) {
        this.name = name.isEmpty() ? "Unknown" : name;
        this.totalCalories = totalCalories;
        this.description = description;
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

    public int getMealId() { return mealId; }
    public void setMealId(int mealId) { this.mealId = mealId; }
}