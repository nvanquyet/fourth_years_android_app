package com.example.android_exam.data.models.enums;

public enum NutritionGoal {
    WEIGHT_LOSS("Weight Loss"),
    WEIGHT_GAIN("Weight Gain"),
    MAINTENANCE("Maintenance");

    private final String displayName;

    NutritionGoal(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
