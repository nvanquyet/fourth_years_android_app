package com.example.android_exam.data.local.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "meals",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = CASCADE
        )
)
public class Meal {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;

    @NonNull
    public String date; // yyyy-MM-dd

    public MealType mealType;

    public double totalCalories;

    public Meal(){
        date = "";
        mealType = MealType.BREAKFAST;
    }
}