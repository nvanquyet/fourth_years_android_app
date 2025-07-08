package com.example.android_exam.data.models.enums;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public enum NutritionGoal implements Serializable {

    @SerializedName("balanced")
    BALANCED,

    @SerializedName("weightLoss")
    WEIGHT_LOSS,

    @SerializedName("weightGain")
    WEIGHT_GAIN,

    @SerializedName("muscleGain")
    MUSCLE_GAIN,

    @SerializedName("lowCarb")
    LOW_CARB,

    @SerializedName("highProtein")
    HIGH_PROTEIN,

    @SerializedName("vegetarian")
    VEGETARIAN,

    @SerializedName("vegan")
    VEGAN,

    @SerializedName("keto")
    KETO,

    @SerializedName("mediterranean")
    MEDITERRANEAN,

    @SerializedName("paleo")
    PALEO,

    @SerializedName("lowSodium")
    LOW_SODIUM,

    @SerializedName("diabeticFriendly")
    DIABETIC_FRIENDLY,

    @SerializedName("heartHealthy")
    HEART_HEALTHY,

    @SerializedName("antiInflammatory")
    ANTI_INFLAMMATORY,

    @SerializedName("other")
    OTHER;

    @NonNull
    @Override
    public String toString() {
        return switch (this) {
            case BALANCED -> "Cân bằng dinh dưỡng";
            case WEIGHT_LOSS -> "Giảm cân";
            case WEIGHT_GAIN -> "Tăng cân";
            case MUSCLE_GAIN -> "Tăng cơ";
            case LOW_CARB -> "Ít carb";
            case HIGH_PROTEIN -> "Nhiều protein";
            case VEGETARIAN -> "Chay";
            case VEGAN -> "Thuần chay";
            case KETO -> "Chế độ Keto";
            case MEDITERRANEAN -> "Địa Trung Hải";
            case PALEO -> "Paleo";
            case LOW_SODIUM -> "Ít natri";
            case DIABETIC_FRIENDLY -> "Thân thiện với người tiểu đường";
            case HEART_HEALTHY -> "Lành mạnh cho tim mạch";
            case ANTI_INFLAMMATORY -> "Chống viêm";
            case OTHER -> "Khác";
        };
    }

    public static NutritionGoal parseNutritionGoal(String goalString) {
        if (goalString == null) {
            throw new IllegalArgumentException("Nutrition goal string is null");
        }

        switch (goalString.trim().toLowerCase()) {
            case "balanced":
            case "cân bằng dinh dưỡng":
            case "can bang dinh duong":
                return BALANCED;

            case "weightloss":
            case "giảm cân":
            case "giam can":
                return WEIGHT_LOSS;

            case "weightgain":
            case "tăng cân":
            case "tang can":
                return WEIGHT_GAIN;

            case "musclegain":
            case "tăng cơ":
            case "tang co":
                return MUSCLE_GAIN;

            case "lowcarb":
            case "ít carb":
            case "it carb":
                return LOW_CARB;

            case "highprotein":
            case "nhiều protein":
            case "nhieu protein":
                return HIGH_PROTEIN;

            case "vegetarian":
            case "chay":
                return VEGETARIAN;

            case "vegan":
            case "thuần chay":
            case "thuan chay":
                return VEGAN;

            case "keto":
            case "chế độ keto":
            case "che do keto":
                return KETO;

            case "mediterranean":
            case "địa trung hải":
            case "dia trung hai":
                return MEDITERRANEAN;

            case "paleo":
                return PALEO;

            case "lowsodium":
            case "ít natri":
            case "it natri":
                return LOW_SODIUM;

            case "diabeticfriendly":
            case "thân thiện với người tiểu đường":
            case "than thien voi nguoi tieu duong":
                return DIABETIC_FRIENDLY;

            case "hearthealthy":
            case "lành mạnh cho tim mạch":
            case "lanh manh cho tim mach":
                return HEART_HEALTHY;

            case "antiinflammatory":
            case "chống viêm":
            case "chong viem":
                return ANTI_INFLAMMATORY;

            case "other":
            case "khác":
            case "khac":
                return OTHER;

            default:
                throw new IllegalArgumentException("Unknown nutrition goal: " + goalString);
        }
    }
}
