package com.example.android_exam.data.api;

import com.example.android_exam.data.dto.nutrition.DailyNutritionSummaryDto;
import com.example.android_exam.data.dto.nutrition.OverviewNutritionSummaryDto;
import com.example.android_exam.data.dto.nutrition.WeeklyNutritionSummaryDto;
import com.example.android_exam.data.dto.nutrition.UserNutritionRequestDto;
import com.example.android_exam.data.dto.response.ApiResponse;
import com.example.android_exam.data.dto.user.UserInformationDto;
import com.google.gson.reflect.TypeToken;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class NutritionApiClient extends BaseApiClient {

    /**
     * Get daily nutrition summary
     * @param userNutritionRequest Request containing date and user information
     * @param callback Callback to handle the response
     */
    public void getDailyNutritionSummary(UserNutritionRequestDto userNutritionRequest,
                                         DataCallback<ApiResponse<DailyNutritionSummaryDto>> callback) {
        String json = gson.toJson(userNutritionRequest);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

        Request request = createRequestBuilder("nutrition/daily")
                .post(body)
                .build();

        executeRequest(request, new TypeToken<ApiResponse<DailyNutritionSummaryDto>>(){}, callback);
    }

    /**
     * Get weekly nutrition summary
     * @param userNutritionRequest Request containing start date, end date and user information
     * @param callback Callback to handle the response
     */
    public void getWeeklyNutritionSummary(UserNutritionRequestDto userNutritionRequest,
                                          DataCallback<ApiResponse<WeeklyNutritionSummaryDto>> callback) {
        String json = gson.toJson(userNutritionRequest);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

        Request request = createRequestBuilder("nutrition/weekly")
                .post(body)
                .build();

        executeRequest(request, new TypeToken<ApiResponse<WeeklyNutritionSummaryDto>>(){}, callback);
    }

    /**
     * Get overview nutrition summary
     * @param userInformationDto User information for calculating nutrition targets
     * @param callback Callback to handle the response
     */
    public void getOverviewNutritionSummary(UserInformationDto userInformationDto,
                                            DataCallback<ApiResponse<OverviewNutritionSummaryDto>> callback) {
        String json = gson.toJson(userInformationDto);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

        Request request = createRequestBuilder("nutrition/overview")
                .post(body)
                .build();

        executeRequest(request, new TypeToken<ApiResponse<OverviewNutritionSummaryDto>>(){}, callback);
    }
}