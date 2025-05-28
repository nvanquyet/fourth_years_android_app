package com.example.android_exam.data.local.entity;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "meal_history")
public class MealHistoryEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String mealName;

    @NonNull
    public Date date;

    public int totalCalories;

    // JSON string chứa list nguyên liệu (nếu chưa dùng type converter phức tạp)
    public String usedIngredientsJson;

    public MealHistoryEntity(@NonNull String mealName, @NonNull Date date, int totalCalories, String usedIngredientsJson) {
        this.mealName = mealName;
        this.date = date;
        this.totalCalories = totalCalories;
        this.usedIngredientsJson = usedIngredientsJson;
    }
}