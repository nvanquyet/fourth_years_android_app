package com.example.android_exam.data.local.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
@Entity(
        tableName = "ingredients",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = CASCADE
        )
)
public class Ingredient {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;

    @NonNull
    public String name;

    public double quantity;
    public String unit;
    public String expiryDate;
    public double caloriesPerUnit;
    public String category; // thịt, rau, trái cây,...


    public Ingredient() {
        name = "";
    }

    @NonNull
    public String getName() { return name;}

    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }

    public String getExpiryDate() { return expiryDate; }

    public double getCaloriesPerUnit() { return caloriesPerUnit; }
    public String getCategory() { return category; }

    public int getId() { return  id; }

    public boolean isExpired() {
        return true;
    }

    public boolean isExpiringSoon(){
        return true;
    }

}
