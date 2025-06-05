package com.example.android_exam.data.local.entity;

/**
 * Enum for ingredient expiry status
 */
public enum ExpiryStatus {
    FRESH("Fresh", android.R.color.holo_green_light),
    EXPIRING_SOON("Expiring Soon", android.R.color.holo_orange_light),
    EXPIRED("Expired", android.R.color.holo_red_light);

    private final String displayName;
    private final int colorResource;

    ExpiryStatus(String displayName, int colorResource) {
        this.displayName = displayName;
        this.colorResource = colorResource;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getColorResource() {
        return colorResource;
    }
}