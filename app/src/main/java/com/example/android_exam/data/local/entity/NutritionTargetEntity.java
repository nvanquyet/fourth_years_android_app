package com.example.android_exam.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "nutrition_target")
public class NutritionTargetEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public int dailyCalorieTarget;

    public int proteinTarget;     // gram
    public int fatTarget;         // gram
    public int carbTarget;        // gram

    public NutritionTargetEntity(int dailyCalorieTarget, int proteinTarget, int fatTarget, int carbTarget) {
        this.dailyCalorieTarget = dailyCalorieTarget;
        this.proteinTarget = proteinTarget;
        this.fatTarget = fatTarget;
        this.carbTarget = carbTarget;
    }
}