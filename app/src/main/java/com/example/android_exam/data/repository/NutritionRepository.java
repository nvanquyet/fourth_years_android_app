package com.example.android_exam.data.repository;

import android.util.Log;

import com.example.android_exam.data.api.ApiManager;
import com.example.android_exam.data.api.DataCallback;
import com.example.android_exam.data.dto.nutrition.DailyNutritionSummaryDto;
import com.example.android_exam.data.dto.nutrition.FoodNutritionDto;
import com.example.android_exam.data.dto.nutrition.NutritionDto;
import com.example.android_exam.data.dto.nutrition.UserNutritionRequestDto;
import com.example.android_exam.data.dto.nutrition.WeeklyNutritionSummaryDto;
import com.example.android_exam.data.dto.response.ApiResponse;
import com.example.android_exam.data.models.base.User;
import com.example.android_exam.data.models.enums.MealType;
import com.example.android_exam.utils.SessionManager;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class NutritionRepository {
    private Random random = new Random();
    private Map<String, DailyNutritionSummaryDto> dailyCache = new HashMap<>();
    private Map<String, WeeklyNutritionSummaryDto> weeklyCache = new HashMap<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat weekFormat = new SimpleDateFormat("yyyy-'W'ww");

    public interface Callback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    public void clearCache() {
        dailyCache.clear();
        weeklyCache.clear();
    }

    public void getDailyNutrition(Date date, Callback<DailyNutritionSummaryDto> callback) {
        Log.d("NutritionRepository", "getDailyNutrition called for date: " + date);
        String dateKey = dateFormat.format(date);
        if (dailyCache.containsKey(dateKey)) {
            callback.onSuccess(dailyCache.get(dateKey));
            return;
        }

        SessionManager.getUser(new SessionManager.UserCallback() {
            @Override
            public void onUserLoaded(User user) {
                var userNutritionRequest = new UserNutritionRequestDto();
                userNutritionRequest.setCurrentDate(date);
                userNutritionRequest.setUserInformationDto(user.toUserInformationDto());
                ApiManager.getInstance().getNutritionClient().getDailyNutritionSummary(userNutritionRequest, new DataCallback<ApiResponse<DailyNutritionSummaryDto>>() {
                    @Override
                    public void onSuccess(ApiResponse<DailyNutritionSummaryDto> result) {
                        //Add to cache
                        dailyCache.put(dateKey, result.getData());
                        callback.onSuccess(result.getData());
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError("Lỗi khi tải dữ liệu: " + error);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        callback.onError("Lỗi kết nối: " + throwable.getMessage());
                    }
                });
            }

            @Override
            public void onError(String error) {
                callback.onError("Lỗi khi tải dữ liệu: " + error);
            }
        });


    }

    public void getWeeklyNutrition(Date weekStart, Callback<WeeklyNutritionSummaryDto> callback) {
        String weekKey = weekFormat.format(weekStart);
        if (weeklyCache.containsKey(weekKey)) {
            callback.onSuccess(weeklyCache.get(weekKey));
            return;
        }

        //Get user information
        SessionManager.getUser(new SessionManager.UserCallback() {
            @Override
            public void onUserLoaded(User user) {
                var userNutritionRequest = new UserNutritionRequestDto();
                userNutritionRequest.setStartDate(weekStart);
                userNutritionRequest.setUserInformationDto(user.toUserInformationDto());
                ApiManager.getInstance().getNutritionClient().getWeeklyNutritionSummary(userNutritionRequest, new DataCallback<ApiResponse<WeeklyNutritionSummaryDto>>() {
                    @Override
                    public void onSuccess(ApiResponse<WeeklyNutritionSummaryDto> result) {
                        //Add to cache
                        weeklyCache.put(weekKey, result.getData());
                        callback.onSuccess(result.getData());
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError("Lỗi khi tải dữ liệu: " + error);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        callback.onError("Lỗi kết nối: " + throwable.getMessage());
                    }
                });
            }

            @Override
            public void onError(String error) {
                callback.onError("Lỗi khi tải dữ liệu: " + error);
            }
        });


    }

}