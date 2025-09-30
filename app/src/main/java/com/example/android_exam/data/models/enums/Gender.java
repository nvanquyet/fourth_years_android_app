package com.example.android_exam.data.models.enums;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public enum Gender implements Serializable {
    @SerializedName("male")
    MALE,

    @SerializedName("female")
    FEMALE,

    @SerializedName("other")
    OTHER,

    @SerializedName("preferNotToSay")
    PREFER_NOT_TO_SAY;

    @NonNull
    @Override
    public String toString() {
        return switch (this) {
            case MALE -> "Nam";
            case FEMALE -> "Nữ";
            case OTHER -> "Khác";
            case PREFER_NOT_TO_SAY -> "Không muốn tiết lộ";
            default -> "Unknown";
        };
    }

    public static Gender parseGender(String genderString) {
        if (genderString == null || genderString.isEmpty()) {
            genderString = "nam";
        }

        return switch (genderString.trim().toLowerCase()) {
            // Parse theo API key
            case "male", "nam" -> Gender.MALE;
            case "female", "nữ", "nu" -> Gender.FEMALE;
            case "other", "khác", "khac" -> Gender.OTHER;
            case "prefernottosay", "không muốn tiết lộ", "khong muon tiet lo" ->
                    Gender.PREFER_NOT_TO_SAY;
            default -> throw new IllegalArgumentException("Unknown gender: " + genderString);
        };
    }
}
