package com.example.android_exam.data.models.enums;

import androidx.annotation.NonNull;
import android.util.Log;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public enum IngredientCategory implements Serializable {
    @SerializedName("Dairy")
    DAIRY(0),
    @SerializedName("Meat")
    MEAT(1),
    @SerializedName("Vegetables")
    VEGETABLES(2),
    @SerializedName("Fruits")
    FRUITS(3),
    @SerializedName("Grains")
    GRAINS(4),
    @SerializedName("Seafood")
    SEAFOOD(5),
    @SerializedName("Beverages")
    BEVERAGES(6),
    @SerializedName("Condiments")
    CONDIMENTS(7),
    @SerializedName("Snacks")
    SNACKS(8),
    @SerializedName("Frozen")
    FROZEN(9),
    @SerializedName("Canned")
    CANNED(10),
    @SerializedName("Spices")
    SPICES(11),
    @SerializedName("Other")
    OTHER(12);

    private final int value;

    IngredientCategory(int value) {
        this.value = value;
    }

    public int toInt() {
        return value;
    }

    public static IngredientCategory fromInt(int value) {
        for (IngredientCategory category : values()) {
            if (category.value == value) {
                return category;
            }
        }
        Log.w("IngredientCategory", "Unknown category value: " + value + ". Falling back to OTHER.");
        return OTHER;
    }

    // Method để convert từ string (API response)
    public static IngredientCategory fromString(String categoryString) {
        if (categoryString == null) return OTHER;

        return switch (categoryString.toLowerCase()) {
            case "dairy" -> DAIRY;
            case "meat" -> MEAT;
            case "vegetables" -> VEGETABLES;
            case "fruits" -> FRUITS;
            case "grains" -> GRAINS;
            case "seafood" -> SEAFOOD;
            case "beverages" -> BEVERAGES;
            case "condiments" -> CONDIMENTS;
            case "snacks" -> SNACKS;
            case "frozen" -> FROZEN;
            case "canned" -> CANNED;
            case "spices" -> SPICES;
            default -> {
                Log.w("IngredientCategory", "Unknown category: " + categoryString);
                yield OTHER;
            }
        };
    }

    @NonNull
    @Override
    public String toString() {
        return switch (this) {
            case DAIRY -> "Sữa";
            case MEAT -> "Thịt";
            case VEGETABLES -> "Rau củ";
            case FRUITS -> "Trái cây";
            case GRAINS -> "Ngũ cốc";
            case SEAFOOD -> "Hải sản";
            case BEVERAGES -> "Đồ uống";
            case CONDIMENTS -> "Nước chấm";
            case SNACKS -> "Đồ ăn nhẹ";
            case FROZEN -> "Đông lạnh";
            case CANNED -> "Đóng hộp";
            case SPICES -> "Gia vị";
            case OTHER -> "Khác";
        };
    }
}
