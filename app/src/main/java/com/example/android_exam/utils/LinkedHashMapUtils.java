package com.example.android_exam.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class LinkedHashMapUtils {
    public static final Map<String, String> genderMap = new LinkedHashMap<String, String>() {{
        put("MALE", "Nam");
        put("FEMALE", "Nữ");
        put("OTHER", "Khác");
        put("PREFER_NOT_TO_SAY", "Không muốn tiết lộ");
    }};

    public static final Map<String, String> activityLevelMap = new LinkedHashMap<String, String>() {{
        put("SEDENTARY", "Ít vận động");
        put("LIGHT", "Hoạt động nhẹ");
        put("MODERATE", "Hoạt động vừa");
        put("ACTIVE", "Hoạt động nhiều");
        put("VERY_ACTIVE", "Rất nhiều");
    }};

    public static final Map<String, String> nutritionGoalMap = new LinkedHashMap<String, String>() {{
        put("BALANCED", "Cân bằng");
        put("WEIGHT_LOSS", "Giảm cân");
        put("WEIGHT_GAIN", "Tăng cân");
        put("MUSCLE_GAIN", "Tăng cơ");
        put("LOW_CARB", "Ít tinh bột");
        put("HIGH_PROTEIN", "Cao đạm");
        put("VEGETARIAN", "Ăn chay");
        put("VEGAN", "Thuần chay");
        put("KETO", "Keto");
        put("MEDITERRANEAN", "Địa Trung Hải");
        put("PALEO", "Paleo");
        put("LOW_SODIUM", "Ít muối");
        put("DIABETIC_FRIENDLY", "Thân thiện tiểu đường");
        put("HEART_HEALTHY", "Tốt cho tim mạch");
        put("ANTI_INFLAMMATORY", "Chống viêm");
        put("OTHER", "Khác");
    }};

    public static String getKeyByValue(Map<String, String> map, String value) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
