package com.example.android_exam.data.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;

import java.util.Date;

@Entity(tableName = "ingredients")
public class IngredientEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "quantity")
    private float quantity; // số lượng

    @ColumnInfo(name = "unit")
    private String unit; // đơn vị (g, ml, cái, v.v.)

    @ColumnInfo(name = "expiration_date")
    private Date expirationDate; // có thể null nếu không có hạn

    @ColumnInfo(name = "calories_per_unit")
    private float caloriesPerUnit; // calo trên mỗi đơn vị

    // --- Getter & Setter ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public float getQuantity() { return quantity; }
    public void setQuantity(float quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public Date getExpirationDate() { return expirationDate; }
    public void setExpirationDate(Date expirationDate) { this.expirationDate = expirationDate; }

    public float getCaloriesPerUnit() { return caloriesPerUnit; }
    public void setCaloriesPerUnit(float caloriesPerUnit) { this.caloriesPerUnit = caloriesPerUnit; }
}
