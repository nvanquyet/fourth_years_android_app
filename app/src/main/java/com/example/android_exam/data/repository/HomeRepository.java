package com.example.android_exam.data.repository;

import static com.example.android_exam.App.getContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import com.example.android_exam.data.api.ApiManager;
import com.example.android_exam.data.api.DataCallback;
import com.example.android_exam.data.dto.food.FoodIngredientDto;
import com.example.android_exam.data.dto.food.FoodSuggestionRequestDto;
import com.example.android_exam.data.dto.food.FoodSuggestionResponseDto;
import com.example.android_exam.data.dto.nutrition.NutritionTip;
import com.example.android_exam.data.dto.nutrition.OverviewNutritionSummaryDto;
import com.example.android_exam.data.dto.response.ApiResponse;
import com.example.android_exam.data.dto.user.UserInformationDto;
import com.example.android_exam.data.models.base.User;
import com.example.android_exam.data.models.enums.ActivityLevel;
import com.example.android_exam.data.models.enums.Gender;
import com.example.android_exam.data.models.enums.NutritionGoal;
import com.example.android_exam.utils.SessionManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

public class HomeRepository {
    private static HomeRepository instance;
    private Handler handler = new Handler(Looper.getMainLooper());

    public static HomeRepository getInstance() {
        if (instance == null) {
            instance = new HomeRepository();
        }
        return instance;
    }

    private static final String CACHE_PREF_NAME = "FoodCache";
    private static final String CACHE_KEY = "Suggestions";

    private List<FoodSuggestionResponseDto> cachedSuggestions = null;

    public HomeRepository() {
        cachedSuggestions = loadCacheFromLocal();
    }

    // Simulate API call for nutrition tip
    public void getNutritionTip(DataCallback<ApiResponse<NutritionTip>> callback) {
        callback.onLoading(true);

        // Simulate network delay
        handler.postDelayed(() -> {
            try {
                // Mock data with various nutrition tips
                String[] tips = {
                        "Uống đủ nước là chìa khóa cho sức khỏe tốt. Mục tiêu 8-10 ly nước mỗi ngày.",
                        "Protein trong bữa sáng giúp bạn cảm thấy no lâu hơn và duy trì năng lượng.",
                        "Ăn nhiều rau xanh giúp cung cấp vitamin và khoáng chất thiết yếu.",
                        "Hạn chế đường và thực phẩm chế biến sẵn để cải thiện sức khỏe tổng thể.",
                        "Ăn chậm và nhai kỹ giúp tiêu hóa tốt hơn và kiểm soát cân nặng."
                };

                String[] titles = {
                        "💧 Mẹo hydration hôm nay",
                        "🥚 Protein cho bữa sáng",
                        "🥬 Rau xanh trong bữa ăn",
                        "🍎 Ăn uống lành mạnh",
                        "🍽️ Thói quen ăn uống"
                };

                int randomIndex = (int) (Math.random() * tips.length);

                NutritionTip tip = new NutritionTip();
                tip.setTitle(titles[randomIndex]);
                tip.setContent(tips[randomIndex]);
                tip.setType("nutrition");

                ApiResponse<NutritionTip> response = new ApiResponse<>();
                response.setSuccess(true);
                response.setData(tip);
                response.setMessage("Success");

                callback.onLoading(false);
                callback.onSuccess(response);
            } catch (Exception e) {
                callback.onLoading(false);
                callback.onError("Failed to load nutrition tip: " + e.getMessage());
            }
        }, 1000);
    }
    public void getFoodSuggestions(boolean forceFetch, DataCallback<ApiResponse<List<FoodSuggestionResponseDto>>> callback) {
        if (!forceFetch && cachedSuggestions != null && !cachedSuggestions.isEmpty()) {
            // Trả về cache nếu không force fetch
            var response = new ApiResponse<List<FoodSuggestionResponseDto>>();
            response.setSuccess(true);
            response.setData(cachedSuggestions);
            response.setMessage("Loaded from cache");
            callback.onLoading(false);
            // Gọi callback onSuccess với dữ liệu cache
            callback.onSuccess(response);
            return;
        }

        // Load user info từ local
        SessionManager.getUser(new SessionManager.UserCallback() {
            @Override
            public void onUserLoaded(User user) {
                var foodSuggestionRequest = new FoodSuggestionRequestDto();
                foodSuggestionRequest.setUserInformation(user.toUserInformationDto());

                // Call API
                ApiManager.getInstance().getFoodClient().getFoodSuggestions(foodSuggestionRequest, new DataCallback<ApiResponse<List<FoodSuggestionResponseDto>>>() {
                    @Override
                    public void onLoading(boolean loading) {
                        callback.onLoading(loading);
                    }

                    @Override
                    public void onSuccess(ApiResponse<List<FoodSuggestionResponseDto>> response) {
                        if (response.isSuccess() && response.getData() != null) {
                            cachedSuggestions = response.getData(); // Lưu cache RAM
                            saveCacheToLocal(cachedSuggestions);    // Lưu cache local
                            callback.onSuccess(response);
                        } else {
                            callback.onError("Failed to load food suggestions: " + response.getMessage());
                        }
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError("Failed to load food suggestions: " + error);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        callback.onError("Failed to load food suggestions: " + throwable);
                    }
                });
            }

            @Override
            public void onError(String error) {
                callback.onError("Failed to load user information: " + error);
            }
        });
    }

    private void saveCacheToLocal(List<FoodSuggestionResponseDto> suggestions) {
        String json = new Gson().toJson(suggestions);
        SharedPreferences preferences = getContext().getSharedPreferences(CACHE_PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(CACHE_KEY, json).apply();
    }

    private List<FoodSuggestionResponseDto> loadCacheFromLocal() {
        SharedPreferences preferences = getContext().getSharedPreferences(CACHE_PREF_NAME, Context.MODE_PRIVATE);
        String json = preferences.getString(CACHE_KEY, null);
        if (json != null) {
            Type type = new TypeToken<List<FoodSuggestionResponseDto>>() {}.getType();
            return new Gson().fromJson(json, type);
        }
        return null;
    }

    // Helper method to create FoodIngredientDto
    private FoodIngredientDto createIngredient(String name, BigDecimal quantity) {
        FoodIngredientDto ingredient = new FoodIngredientDto();
        ingredient.setIngredientName(name);
        ingredient.setQuantity(quantity);
        return ingredient;
    }

    // Simulate API call for nutrition progress
    public void getNutritionOverview(DataCallback<ApiResponse<OverviewNutritionSummaryDto>> callback) {
        callback.onLoading(true);

        SessionManager.getUser(new SessionManager.UserCallback() {
            @Override
            public void onUserLoaded(User user) {
                UserInformationDto userInfo = new UserInformationDto();
                userInfo.setGender(user.getGender());
                userInfo.setDateOfBirth(user.getDateOfBirth());
                userInfo.setHeight(user.getHeight());
                userInfo.setWeight(user.getWeight());
                userInfo.setTargetWeight(user.getTargetWeight());
                userInfo.setPrimaryNutritionGoal(user.getPrimaryNutritionGoal());
                userInfo.setActivityLevel(user.getActivityLevel());
                ApiManager.getInstance().getNutritionClient().getOverviewNutritionSummary(userInfo, new DataCallback<ApiResponse<OverviewNutritionSummaryDto>>() {
                    @Override
                    public void onSuccess(ApiResponse<OverviewNutritionSummaryDto> result) {
                        if (result.isSuccess() && result.getData() != null) {
                            callback.onSuccess(result);
                        } else {
                            callback.onError(result.getMessage());
                        }
                        callback.onLoading(false);
                    }

                    @Override
                    public void onError(String error) {
                        callback.onLoading(false);
                        callback.onError(error);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        callback.onLoading(false);
                        callback.onFailure(throwable);
                    }
                });
            }

            @Override
            public void onError(String error) {
                // Handle error loading user information
                callback.onLoading(false);
                callback.onError("Failed to load user information: " + error);
                //Clear shared preferences if user info is not available
                //SharedPreferences preferences = getContext().getSharedPreferences(CACHE_PREF_NAME, Context.MODE_PRIVATE);
                //preferences.edit().clear().apply();
                //Switch to login screen or show error message
                return;
            }
        });



    }

    // Simulate API call for user profile
    public void getUserProfile(DataCallback<ApiResponse<UserInformationDto>> callback) {
        callback.onLoading(true);

        handler.postDelayed(() -> {
            try {
                UserInformationDto profile = new UserInformationDto();

                // Set user information
                profile.setGender(Gender.MALE);

                // Create date of birth (25 years old)
                Calendar calendar = Calendar.getInstance();
                calendar.set(1999, Calendar.MARCH, 15); // March 15, 1999
                profile.setDateOfBirth(calendar.getTime());

                profile.setHeight(170.5);
                profile.setWeight(68.5);
                profile.setTargetWeight(65.0);
                profile.setPrimaryNutritionGoal(NutritionGoal.WEIGHT_LOSS);
                profile.setActivityLevel(ActivityLevel.ACTIVE);

                ApiResponse<UserInformationDto> response = new ApiResponse<>();
                response.setSuccess(true);
                response.setData(profile);
                response.setMessage("Success");

                callback.onLoading(false);
                callback.onSuccess(response);
            } catch (Exception e) {
                callback.onLoading(false);
                callback.onError("Failed to load user profile: " + e.getMessage());
            }
        }, 600);
    }
}