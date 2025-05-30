package com.example.android_exam.data.local.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
@Entity(
        tableName = "foods",
        foreignKeys = @ForeignKey(
                entity = Meal.class,
                parentColumns = "id",
                childColumns = "mealId",
                onDelete = CASCADE
        )
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
        name = "";
    }
}
