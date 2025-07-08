package com.example.android_exam.data.models.enums;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public enum ActivityLevel implements Serializable {

    @SerializedName("sedentary")
    SEDENTARY,

    @SerializedName("light")
    LIGHT,

    @SerializedName("moderate")
    MODERATE,

    @SerializedName("active")
    ACTIVE,

    @SerializedName("very_active")
    VERY_ACTIVE;

    @NonNull
    @Override
    public String toString() {
        return switch (this) {
            case SEDENTARY -> "Ít vận động";
            case LIGHT -> "Vận động nhẹ";
            case MODERATE -> "Vận động vừa phải";
            case ACTIVE -> "Vận động nhiều";
            case VERY_ACTIVE -> "Rất năng động";
        };
    }

    public static ActivityLevel parseActivityLevel(String levelString) {
        if (levelString == null) {
            throw new IllegalArgumentException("Activity level string is null");
        }

        switch (levelString.trim().toLowerCase()) {
            // API key
            case "sedentary":
            case "ít vận động":
            case "it van dong":
                return SEDENTARY;

            case "light":
            case "vận động nhẹ":
            case "van dong nhe":
                return LIGHT;

            case "moderate":
            case "vận động vừa phải":
            case "van dong vua phai":
                return MODERATE;

            case "active":
            case "vận động nhiều":
            case "van dong nhieu":
                return ACTIVE;

            case "very_active":
            case "rất năng động":
            case "rat nang dong":
                return VERY_ACTIVE;

            default:
                throw new IllegalArgumentException("Unknown activity level: " + levelString);
        }
    }
}
