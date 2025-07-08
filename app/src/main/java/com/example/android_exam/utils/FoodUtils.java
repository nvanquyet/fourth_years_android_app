package com.example.android_exam.utils;

import android.net.ParseException;

import com.example.android_exam.data.models.enums.MealType;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FoodUtils {
    public static MealType getMealTypeWithTime(String dateTimeStr) {
        // Nếu không truyền tham số, dùng thời gian hiện tại
        Calendar calendar = Calendar.getInstance();

        if (dateTimeStr != null && !dateTimeStr.isEmpty()) {
            try {
                // Parse chuỗi ISO 8601
                SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                Date parsedDate = isoFormat.parse(dateTimeStr);

                // Gán thời gian đã parse vào calendar
                calendar.setTime(parsedDate);
            } catch (ParseException e) {
                // Nếu chuỗi thời gian không hợp lệ, giữ thời gian hiện tại
                e.printStackTrace();
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }
        }

        // Lấy giờ trong ngày
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        // Xác định loại bữa ăn
        if (hour >= 5 && hour < 10) {
            return MealType.BREAKFAST;
        } else if (hour >= 10 && hour < 15) {
            return MealType.LUNCH;
        } else if (hour >= 15 && hour < 18) {
            return MealType.SNACK;
        } else if (hour >= 18 && hour < 22) {
            return MealType.DINNER;
        } else {
            return MealType.OTHER;
        }
    }
}
