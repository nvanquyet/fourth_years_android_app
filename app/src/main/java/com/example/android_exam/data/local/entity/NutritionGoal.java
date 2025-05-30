package com.example.android_exam.data.local.entity;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "nutrition_goals")
public class NutritionGoal {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;

    public double dailyCalorieGoal;
    public double dailyProteinGoal;
    public double dailyCarbGoal;
    public double dailyFatGoal;

    @NonNull
    public String goalType; // weight_loss, muscle_gain, maintain

    public NutritionGoal(int userId, double dailyCalorieGoal, double dailyProteinGoal,
                         double dailyCarbGoal, double dailyFatGoal, @NonNull String goalType) {
        this.userId = userId;
        this.dailyCalorieGoal = dailyCalorieGoal;
        this.dailyProteinGoal = dailyProteinGoal;
        this.dailyCarbGoal = dailyCarbGoal;
        this.dailyFatGoal = dailyFatGoal;
        this.goalType = goalType;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getDailyCalorieGoal() { return dailyCalorieGoal; }
    public void setDailyCalorieGoal(double dailyCalorieGoal) { this.dailyCalorieGoal = dailyCalorieGoal; }

    public double getDailyProteinGoal() { return dailyProteinGoal; }
    public void setDailyProteinGoal(double dailyProteinGoal) { this.dailyProteinGoal = dailyProteinGoal; }

    public double getDailyCarbGoal() { return dailyCarbGoal; }
    public void setDailyCarbGoal(double dailyCarbGoal) { this.dailyCarbGoal = dailyCarbGoal; }

    public double getDailyFatGoal() { return dailyFatGoal; }
    public void setDailyFatGoal(double dailyFatGoal) { this.dailyFatGoal = dailyFatGoal; }

    public String getGoalType() { return goalType; }
    public void setGoalType(String goalType) { this.goalType = goalType; }
}