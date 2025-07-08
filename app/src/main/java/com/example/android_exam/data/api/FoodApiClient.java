package com.example.android_exam.data.api;

import com.example.android_exam.data.dto.food.CreateFoodRequestDto;
import com.example.android_exam.data.dto.food.DeleteFoodRequestDto;
import com.example.android_exam.data.dto.food.FoodDataResponseDto;
import com.example.android_exam.data.dto.food.FoodRecipeRequestDto;
import com.example.android_exam.data.dto.food.FoodSuggestionRequestDto;
import com.example.android_exam.data.dto.food.FoodSuggestionResponseDto;
import com.example.android_exam.data.dto.food.UpdateFoodRequestDto;
import com.example.android_exam.data.dto.response.ApiResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodApiClient extends BaseApiClient {

    // Create food
    public void createFood(CreateFoodRequestDto dto, DataCallback<ApiResponse<FoodDataResponseDto>> callback) {
        createFood(dto, null, callback);
    }

    // Update food
    public void updateFood(UpdateFoodRequestDto dto, DataCallback<ApiResponse<FoodDataResponseDto>> callback) {
        updateFood(dto, null, callback);
    }

    // Delete food
    public void deleteFood(DeleteFoodRequestDto dto, DataCallback<ApiResponse<Boolean>> callback) {
        delete("food", dto, new TypeToken<ApiResponse<Boolean>>() {}, callback);
    }

    // Get food by ID
    public void getFoodById(int id, DataCallback<ApiResponse<FoodDataResponseDto>> callback) {
        get("food/" + id, new TypeToken<ApiResponse<FoodDataResponseDto>>(){}, callback);
    }

    // Get all foods (if endpoint supports it)
    // Get food suggestions
    public void getFoodSuggestions(FoodSuggestionRequestDto requestDto, DataCallback<ApiResponse<List<FoodSuggestionResponseDto>>> callback) {
        post("food/suggestions", requestDto, new TypeToken<ApiResponse<List<FoodSuggestionResponseDto>>>(){}, callback);
    }

    // Get recipe suggestions
    public void getRecipeSuggestions(FoodRecipeRequestDto recipeRequest, DataCallback<ApiResponse<FoodDataResponseDto>> callback) {
        post("food/recipes", recipeRequest, new TypeToken<ApiResponse<FoodDataResponseDto>>(){}, callback);
    }


    public void createFood(CreateFoodRequestDto dto, File imageFile, DataCallback<ApiResponse<FoodDataResponseDto>> callback) {
        Map<String, String> formFields = createFormFieldsFromDto(dto);

        postMultipart(
                "food",
                formFields,
                imageFile,
                "Image",
                new TypeToken<ApiResponse<FoodDataResponseDto>>() {},
                callback
        );
    }

    // Update food with image file
    public void updateFood(UpdateFoodRequestDto dto, File imageFile, DataCallback<ApiResponse<FoodDataResponseDto>> callback) {
        Map<String, String> formFields = createFormFieldsFromDto(dto);

        putMultipart(
                "food",
                formFields,
                imageFile,
                "Image",
                new TypeToken<ApiResponse<FoodDataResponseDto>>() {},
                callback
        );
    }


    // Utility method để tạo form fields từ CreateFoodRequestDto
    private Map<String, String> createFormFieldsFromDto(CreateFoodRequestDto dto) {
        Map<String, String> formFields = new HashMap<>();

        if (dto.getName() != null) {
            formFields.put("Name", dto.getName());
        }
        if (dto.getDescription() != null) {
            formFields.put("Description", dto.getDescription());
        }
        if (dto.getPreparationTimeMinutes() != null) {
            formFields.put("PreparationTimeMinutes", dto.getPreparationTimeMinutes().toString());
        }
        if (dto.getCookingTimeMinutes() != null) {
            formFields.put("CookingTimeMinutes", dto.getCookingTimeMinutes().toString());
        }
        if (dto.getCalories() != null) {
            formFields.put("Calories", dto.getCalories().toString());
        }
        if (dto.getProtein() != null) {
            formFields.put("Protein", dto.getProtein().toString());
        }
        if (dto.getCarbohydrates() != null) {
            formFields.put("Carbohydrates", dto.getCarbohydrates().toString());
        }
        if (dto.getFat() != null) {
            formFields.put("Fat", dto.getFat().toString());
        }
        if (dto.getFiber() != null) {
            formFields.put("Fiber", dto.getFiber().toString());
        }
        if (dto.getInstructions() != null && !dto.getInstructions().isEmpty()) {
            // Convert list to JSON string
            formFields.put("Instructions", new Gson().toJson(dto.getInstructions()));
        }
        if (dto.getTips() != null && !dto.getTips().isEmpty()) {
            // Convert list to JSON string
            formFields.put("Tips", new Gson().toJson(dto.getTips()));
        }
        formFields.put("DifficultyLevel", String.valueOf(dto.getDifficultyLevel()));

        if (dto.getMealDate() != null) {
            formFields.put("MealDate", dto.getMealDate().toString());
        }
        if (dto.getMealType() != null) {
            formFields.put("MealType", dto.getMealType().name());
        }
        if (dto.getIngredients() != null && !dto.getIngredients().isEmpty()) {
            // Convert list to JSON string
            formFields.put("Ingredients", new Gson().toJson(dto.getIngredients()));
        }
        if (dto.getConsumedAt() != null) {
            formFields.put("ConsumedAt", dto.getConsumedAt().toString());
        }

        return formFields;
    }

    // Utility method để tạo form fields từ UpdateFoodRequestDto
    private Map<String, String> createFormFieldsFromDto(UpdateFoodRequestDto dto) {
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
        if (dto.getPreparationTimeMinutes() != null) {
            formFields.put("PreparationTimeMinutes", dto.getPreparationTimeMinutes().toString());
        }
        if (dto.getCookingTimeMinutes() != null) {
            formFields.put("CookingTimeMinutes", dto.getCookingTimeMinutes().toString());
        }
        if (dto.getCalories() != null) {
            formFields.put("Calories", dto.getCalories().toString());
        }
        if (dto.getProtein() != null) {
            formFields.put("Protein", dto.getProtein().toString());
        }
        if (dto.getCarbohydrates() != null) {
            formFields.put("Carbohydrates", dto.getCarbohydrates().toString());
        }
        if (dto.getFat() != null) {
            formFields.put("Fat", dto.getFat().toString());
        }
        if (dto.getFiber() != null) {
            formFields.put("Fiber", dto.getFiber().toString());
        }
        if (dto.getInstructions() != null && !dto.getInstructions().isEmpty()) {
            // Convert list to JSON string
            formFields.put("Instructions", new Gson().toJson(dto.getInstructions()));
        }
        if (dto.getTips() != null && !dto.getTips().isEmpty()) {
            // Convert list to JSON string
            formFields.put("Tips", new Gson().toJson(dto.getTips()));
        }
        formFields.put("DifficultyLevel", String.valueOf(dto.getDifficultyLevel()));

        if (dto.getMealDate() != null) {
            formFields.put("MealDate", dto.getMealDate().toString());
        }
        if (dto.getMealType() != null) {
            formFields.put("MealType", dto.getMealType().name());
        }

        if (dto.getConsumedAt() != null) {
            formFields.put("ConsumedAt", dto.getConsumedAt().toString());
        }

        if (dto.getIngredients() != null && !dto.getIngredients().isEmpty()) {
            // Convert list to JSON string
            formFields.put("Ingredients", new Gson().toJson(dto.getIngredients()));
        }

        return formFields;
    }

}