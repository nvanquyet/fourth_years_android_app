package com.example.android_exam.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

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
    public String name;

    public long dateTimestamp; // Lưu ngày và giờ dưới dạng milliseconds since epoch
    public String mealType; // "breakfast", "lunch", "dinner", "snack"
    public double totalCalories;

    public Meal() {
        this.name = "Unknown";
        this.dateTimestamp = System.currentTimeMillis();
        this.mealType = "snack";
        this.totalCalories = 0.0;
    }

    @Ignore
    public Meal(int userId, @NonNull String name, long dateTimestamp, String mealType, double totalCalories) {
        this.userId = userId;
        this.name = name.isEmpty() ? "Unknown" : name;
        this.dateTimestamp = dateTimestamp;
        this.mealType = mealType.isEmpty() ? "snack" : mealType;
        this.totalCalories = totalCalories;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    @NonNull
    public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name.isEmpty() ? "Unknown" : name; }

    public long getDateTimestamp() { return dateTimestamp; }
    public void setDateTimestamp(long dateTimestamp) { this.dateTimestamp = dateTimestamp; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType.isEmpty() ? "snack" : mealType; }

    public double getTotalCalories() { return totalCalories; }
    public void setTotalCalories(double totalCalories) { this.totalCalories = totalCalories; }
}