package com.example.android_exam.data.api;

import android.util.Log;

import com.example.android_exam.data.dto.ingredient.CreateIngredientRequestDto;
import com.example.android_exam.data.dto.ingredient.DeleteIngredientRequestDto;
import com.example.android_exam.data.dto.ingredient.IngredientDataResponseDto;
import com.example.android_exam.data.dto.ingredient.IngredientFilterDto;
import com.example.android_exam.data.dto.ingredient.IngredientSearchResultDto;
import com.example.android_exam.data.dto.ingredient.UpdateIngredientRequestDto;
import com.example.android_exam.data.dto.response.ApiResponse;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Request;

public class IngredientApiClient extends BaseApiClient {

    // Tạo ingredient chỉ với JSON (không có file)
    public void createIngredient(CreateIngredientRequestDto dto, DataCallback<ApiResponse<IngredientDataResponseDto>> callback) {
        createIngredient(dto, null, callback);
    }

    // Tạo ingredient với file
    public void createIngredient(CreateIngredientRequestDto dto, File imageFile, DataCallback<ApiResponse<IngredientDataResponseDto>> callback) {
        Map<String, String> formFields = createFormFieldsFromDto(dto);

        postMultipart(
                "ingredient",
                formFields,
                imageFile,
                "Image",
                new TypeToken<ApiResponse<IngredientDataResponseDto>>() {},
                callback
        );
    }

    // Update ingredient chỉ với JSON
    public void updateIngredient(UpdateIngredientRequestDto dto, DataCallback<ApiResponse<IngredientDataResponseDto>> callback) {
        updateIngredient(dto, null, callback);
    }

    // Update ingredient với file
    public void updateIngredient(UpdateIngredientRequestDto dto, File imageFile, DataCallback<ApiResponse<IngredientDataResponseDto>> callback) {
        Map<String, String> formFields = createFormFieldsFromDto(dto);
        String endpoint = "ingredient/" + dto.getId();
        putMultipart(
                endpoint,
                formFields,
                imageFile,
                "Image",
                new TypeToken<ApiResponse<IngredientDataResponseDto>>() {},
                callback
        );
    }

    // Delete ingredient
    public void deleteIngredient(DeleteIngredientRequestDto dto, DataCallback<ApiResponse<Boolean>> callback) {
        delete("ingredient/" + dto.getId(), dto, new TypeToken<ApiResponse<Boolean>>(){}, callback);
    }

    // Get ingredient by ID
    public void getIngredientById(int id, DataCallback<ApiResponse<IngredientDataResponseDto>> callback) {
        get("ingredient/" + id, new TypeToken<ApiResponse<IngredientDataResponseDto>>(){}, callback);
    }

    // Get all ingredients với filter
    public void getAllIngredients(IngredientFilterDto filter, DataCallback<ApiResponse<IngredientSearchResultDto>> callback) {
        // Build query parameters
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "ingredient").newBuilder();

        if (filter.getCategory() != null) {
            urlBuilder.addQueryParameter("category", filter.getCategory().name());
        }
        if (filter.getIsExpired() != null) {
            urlBuilder.addQueryParameter("isExpired", filter.getIsExpired().toString());
        }
        if (filter.getSearchTerm() != null && !filter.getSearchTerm().trim().isEmpty()) {
            urlBuilder.addQueryParameter("searchTerm", filter.getSearchTerm().trim());
        }

        HttpUrl finalUrl = urlBuilder.build();
        Log.d("IngredientApiClient", "Query URL: " + finalUrl.toString());

        Request request = createRequestBuilder("")
                .url(finalUrl)
                .get()
                .build();

        executeRequest(request, new TypeToken<ApiResponse<IngredientSearchResultDto>>(){}, callback);
    }

    // Utility method để tạo form fields từ CreateIngredientRequestDto
    private Map<String, String> createFormFieldsFromDto(CreateIngredientRequestDto dto) {
        Map<String, String> formFields = new HashMap<>();

        if (dto.getName() != null) {
            formFields.put("Name", dto.getName());
        }
        if (dto.getDescription() != null) {
            formFields.put("Description", dto.getDescription());
        }
        if (dto.getQuantity() != null) {
            formFields.put("Quantity", dto.getQuantity().toString());
        }
        if (dto.getUnit() != null) {
            formFields.put("Unit", String.valueOf(dto.getUnit().toInt()));
        }
        if (dto.getCategory() != null) {
            formFields.put("Category",  String.valueOf(dto.getCategory().toInt()));
        }
        if (dto.getExpiryDate() != null) {
            formFields.put("ExpiryDate", dto.getExpiryDate());
        }

        return formFields;
    }

    // Utility method để tạo form fields từ UpdateIngredientRequestDto
    private Map<String, String> createFormFieldsFromDto(UpdateIngredientRequestDto dto) {
        Map<String, String> formFields = new HashMap<>();

        if (dto.getId() != null) {
            formFields.put("Id", dto.getId().toString());
        }
        if (dto.getName() != null) {
            formFields.put("Name", dto.getName());
        }
        if (dto.getDescription() != null) {
            formFields.put("Description", dto.getDescription());
        }
        if (dto.getQuantity() != null) {
            formFields.put("Quantity", dto.getQuantity().toString());
        }
        if (dto.getUnit() != null) {
            formFields.put("Unit", String.valueOf(dto.getUnit().toInt()));
        }
        if (dto.getCategory() != null) {
            formFields.put("Category",  String.valueOf(dto.getCategory().toInt()));
        }
        if (dto.getExpiryDate() != null) {
            formFields.put("ExpiryDate", dto.getExpiryDate());
        }

        return formFields;
    }
}