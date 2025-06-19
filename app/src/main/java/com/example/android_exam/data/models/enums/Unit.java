package com.example.android_exam.data.models.enums;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public enum Unit {
    @SerializedName("Liter")
    LITER,
    @SerializedName("Milliliter")
    MILLILITER,
    @SerializedName("Kilogram")
    KILOGRAM,
    @SerializedName("Gram")
    GRAM,
    @SerializedName("Piece")
    PIECE;

    @NonNull
    @Override
    public String toString() {
        return switch (this) {
            case LITER -> "Liter";
            case MILLILITER -> "Milliliter";
            case KILOGRAM -> "Kilogram";
            case GRAM -> "Gram";
            case PIECE -> "Piece";
            default -> name();
        };
    }
}