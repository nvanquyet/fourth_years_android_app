package com.example.android_exam.data.local.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "meals",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = CASCADE
        ),
        indices = {@Index(value = {"userId"})}
)
public class Meal {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;

    @NonNull
    public String date; // yyyy-MM-dd

    public MealType mealType;

    public double totalCalories;

    public Meal() {
        date = "1970-01-01"; // Giá trị mặc định
        mealType = MealType.BREAKFAST;
    }

    // Getters and setters
    @NonNull
    public String getDate() { return date; }
    public void setDate(@NonNull String date) { this.date = date.isEmpty() ? "1970-01-01" : date; }

    public MealType getMealType() { return mealType; }
    public void setMealType(MealType mealType) { this.mealType = mealType; }

    public double getTotalCalories() { return totalCalories; }
    public void setTotalCalories(double totalCalories) { this.totalCalories = totalCalories; }
}