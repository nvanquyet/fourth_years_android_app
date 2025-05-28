package com.example.android_exam.data.local.entity;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.annotation.Nullable;

import java.util.Date;

@Entity(tableName = "ingredients")
public class IngredientEntity {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "quantity")
    public float quantity;

    @ColumnInfo(name = "unit")
    public String unit;

    @Nullable
    @ColumnInfo(name = "expiration_date")
    public Date expirationDate;

    public IngredientEntity(String name, float quantity, String unit, @Nullable Date expirationDate) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expirationDate = expirationDate;
    }

    public String getName() { return name; }

    public float getQuantity() {return quantity; }

    public String getUnit() { return unit; }

    public Date getExpiryDate() {return expirationDate; }
}