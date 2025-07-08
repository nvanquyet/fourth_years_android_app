package com.example.android_exam.data.api;

import android.util.Log;

import com.example.android_exam.data.dto.food.FoodAnalysticResponseDto;
import com.example.android_exam.data.dto.ingredient.IngredientAnalysticResponseDto;
import com.example.android_exam.data.dto.response.ApiResponse;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AiClient extends BaseApiClient {

    private static final String TAG = "AiClient";

    public AiClient() {
        super();
    }

    /**
     * Gọi API để phát hiện món ăn từ ảnh
     * Endpoint: POST api/ai/detect_food (multipart)
     */
    public void detectFood(File imageFile, ResponseCallback<ApiResponse<FoodAnalysticResponseDto>> callback) {
        String endpoint = "ai/detect_food";

        // Gửi file không cần thêm fields khác
        Map<String, String> formFields = Collections.emptyMap();

        Type type = new TypeToken<ApiResponse<List<FoodAnalysticResponseDto>>>() {}.getType();

        postMultipart(endpoint, formFields, imageFile, "Image", new TypeToken<ApiResponse<FoodAnalysticResponseDto>>() {}, callback);
    }

    /**
     * Gọi API để phân tích nguyên liệu từ ảnh
     * Endpoint: POST api/ai/detect_ingredient (application/json)
     */
    public void detectIngredient(File imageFile, ResponseCallback<ApiResponse<IngredientAnalysticResponseDto>> callback) {
        String endpoint = "ai/detect_ingredient";

        Map<String, String> formFields = Collections.emptyMap();
        Type type = new TypeToken<ApiResponse<List<IngredientAnalysticResponseDto>>>() {}.getType();

        postMultipart(endpoint, formFields, imageFile, "Image", new TypeToken<ApiResponse<IngredientAnalysticResponseDto>>() {}, callback);
    }


}
