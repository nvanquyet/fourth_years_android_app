package com.example.android_exam.data.models.enums;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public enum Category {
    @SerializedName("Vegetables")
    VEGETABLES,
    @SerializedName("Fruits")
    FRUITS,
    @SerializedName("Meat")
    MEAT,
    @SerializedName("Dairy")
    DAIRY,
    @SerializedName("Grains")
    GRAINS,
    @SerializedName("Others")
    OTHERS;

    @NonNull
    @Override
    public String toString() {
        return switch (this) {
            case VEGETABLES -> "Vegetables";
            case FRUITS -> "Fruits";
            case MEAT -> "Meat";
            case DAIRY -> "Dairy";
            case GRAINS -> "Grains";
            case OTHERS -> "Others";
            default -> name();
        };
    }
}