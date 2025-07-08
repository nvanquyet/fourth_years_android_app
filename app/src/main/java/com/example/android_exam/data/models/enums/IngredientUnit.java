package com.example.android_exam.data.models.enums;

import androidx.annotation.NonNull;
import android.util.Log;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public enum IngredientUnit implements Serializable {
    @SerializedName("Kilogram")
    KILOGRAM(0),    // "kg"
    @SerializedName("Liter")
    LITER(1),       // "l"
    @SerializedName("Piece")
    PIECE(2),       // "cái"
    @SerializedName("Box")
    BOX(3),         // "hộp"
    @SerializedName("Gram")
    GRAM(4),        // "g"
    @SerializedName("Milliliter")
    MILLILITER(5),  // "ml"
    @SerializedName("Can")
    CAN(6),         // "lon"
    @SerializedName("Cup")
    CUP(7),         // "cốc"
    @SerializedName("Tablespoon")
    TABLESPOON(8),  // "muỗng canh"
    @SerializedName("Teaspoon")
    TEASPOON(9),    // "muỗng cà phê"
    @SerializedName("Package")
    PACKAGE(10),    // "gói"
    @SerializedName("Bottle")
    BOTTLE(11),     // "chai"
    @SerializedName("Other")
    OTHER(12);      // "khác"

    private final int value;

    IngredientUnit(int value) {
        this.value = value;
    }

    public int toInt() {
        return value;
    }

    public static IngredientUnit fromInt(int value) {
        for (IngredientUnit unit : values()) {
            if (unit.value == value) {
                return unit;
            }
        }
        Log.w("IngredientUnit", "Unknown unit value: " + value + ". Falling back to OTHER.");
        return OTHER;
    }

    // Method để convert từ string (API response)
    public static IngredientUnit fromString(String unitString) {
        if (unitString == null) return OTHER;

        return switch (unitString.toLowerCase()) {
            case "kilogram", "kg" -> KILOGRAM;
            case "liter", "l" -> LITER;
            case "piece" -> PIECE;
            case "box" -> BOX;
            case "gram", "g" -> GRAM;
            case "milliliter", "ml" -> MILLILITER;
            case "can" -> CAN;
            case "cup" -> CUP;
            case "tablespoon" -> TABLESPOON;
            case "teaspoon" -> TEASPOON;
            case "package" -> PACKAGE;
            case "bottle" -> BOTTLE;
            default -> {
                Log.w("IngredientUnit", "Unknown unit: " + unitString);
                yield OTHER;
            }
        };
    }

    @NonNull
    @Override
    public String toString() {
        return switch (this) {
            case KILOGRAM -> "kg";
            case LITER -> "l";
            case PIECE -> "cái";
            case BOX -> "hộp";
            case GRAM -> "g";
            case MILLILITER -> "ml";
            case CAN -> "lon";
            case CUP -> "cốc";
            case TABLESPOON -> "muỗng canh";
            case TEASPOON -> "muỗng cà phê";
            case PACKAGE -> "gói";
            case BOTTLE -> "chai";
            case OTHER -> "khác";
        };
    }
}
