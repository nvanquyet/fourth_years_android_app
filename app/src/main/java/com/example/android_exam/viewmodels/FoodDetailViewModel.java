package com.example.android_exam.viewmodels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android_exam.data.api.ApiManager;
import com.example.android_exam.data.api.DataCallback;
import com.example.android_exam.data.dto.food.CreateFoodRequestDto;
import com.example.android_exam.data.dto.food.DeleteFoodRequestDto;
import com.example.android_exam.data.dto.food.FoodDataResponseDto;
import com.example.android_exam.data.dto.food.FoodIngredientDto;
import com.example.android_exam.data.dto.food.UpdateFoodRequestDto;
import com.example.android_exam.data.dto.response.ApiResponse;
import com.example.android_exam.utils.DateUtils;
import com.example.android_exam.utils.FoodUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class FoodDetailViewModel extends ViewModel {
    // LiveData
    private MutableLiveData<FoodDataResponseDto> foodLiveData = new MutableLiveData<>();
    private MutableLiveData<List<FoodIngredientDto>> ingredientsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<String>> instructionsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<String>> tipsLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isSuggestionLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> hasChangesLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> confirmButtonEnabledLiveData = new MutableLiveData<>();
    private MutableLiveData<String> successMessageLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> showDateTimePickerLiveData = new MutableLiveData<>();
    private MutableLiveData<String> consumeTimeLiveData = new MutableLiveData<>();

    // Data
    private FoodDataResponseDto originalFood;
    private FoodDataResponseDto currentFood;
    private boolean isSuggestionFood = false;
    private String selectedConsumeTime;

    // Local time formatter (for display)
    private static final DateTimeFormatter LOCAL_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public FoodDetailViewModel() {
        initializeState();
    }

    private void initializeState() {
        isLoadingLiveData.setValue(false);
        hasChangesLiveData.setValue(false);
        confirmButtonEnabledLiveData.setValue(false);

        LocalDateTime currentDateTime = LocalDateTime.now();
        selectedConsumeTime = currentDateTime.format(LOCAL_TIME_FORMATTER);
        consumeTimeLiveData.setValue(selectedConsumeTime);

        Log.d("FoodDetailViewModel", "Default consume time set (local): " + selectedConsumeTime);
        Log.d("FoodDetailViewModel", "Current timezone: " + TimeZone.getDefault().getID());
    }

    public void loadFoodDetail(FoodDataResponseDto foodData, boolean isSuggestionFood) {
        //Log id food response
        Log.d("FoodDetailViewModel", "Loading food detail for ID: " + (foodData != null ? foodData.getId() : "null"));
        this.isSuggestionFood = isSuggestionFood;
        isSuggestionLiveData.setValue(isSuggestionFood);
        originalFood = foodData;
        currentFood = copyFood(originalFood);

        // Set consume time from food data or current LOCAL time
        if (currentFood.getConsumedAt() != null && !currentFood.getConsumedAt().isEmpty()) {
            // If consume time from server has Z suffix, parse it and convert to local time for display
            LocalDateTime parsedDateTime = DateUtils.parseIsoDateTimeWithoutTimezoneConvert(currentFood.getConsumedAt());
            if (parsedDateTime != null) {
                selectedConsumeTime = parsedDateTime.format(LOCAL_TIME_FORMATTER);
                Log.d("FoodDetailViewModel", "Parsed consume time: " + parsedDateTime);
            } else {
                selectedConsumeTime = currentFood.getConsumedAt();
            }
        } else {
            // Use current LOCAL time
            LocalDateTime currentDateTime = LocalDateTime.now();
            selectedConsumeTime = currentDateTime.format(LOCAL_TIME_FORMATTER);
            currentFood.setConsumedAt(selectedConsumeTime);
        }
        consumeTimeLiveData.setValue(selectedConsumeTime);

        foodLiveData.postValue(currentFood);
        ingredientsLiveData.postValue(new ArrayList<>(currentFood.getIngredients()));
        tipsLiveData.postValue(new ArrayList<>(currentFood.getTips()));
        instructionsLiveData.postValue(new ArrayList<>(currentFood.getInstructions()));

        updateConfirmButtonState();
    }

    public void updateIngredientQuantity(int ingredientId, BigDecimal newQuantity) {
        if (currentFood == null || currentFood.getIngredients() == null) return;

        List<FoodIngredientDto> ingredients = currentFood.getIngredients();
        for (FoodIngredientDto ingredient : ingredients) {
            if (ingredient.getIngredientId() == ingredientId) {
                ingredient.setQuantity(newQuantity);
                break;
            }
        }

        ingredientsLiveData.setValue(new ArrayList<>(ingredients));
        checkForChanges();
    }

    public void onTimePress() {
        // Trigger showing date time picker
        showDateTimePickerLiveData.setValue(true);
    }

    public void onDateTimeSelected(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
        // Create LocalDateTime directly (this is local time)
        LocalDateTime selectedDateTime = LocalDateTime.of(year, month + 1, dayOfMonth, hourOfDay, minute);
        selectedConsumeTime = selectedDateTime.format(LOCAL_TIME_FORMATTER);

        if (currentFood != null) {
            currentFood.setConsumedAt(selectedConsumeTime);
        }

        consumeTimeLiveData.setValue(selectedConsumeTime);
        checkForChanges();

        Log.d("FoodDetailViewModel", "Selected consume time (local): " + selectedConsumeTime);
    }

    /**
     * Convert local time string to UTC time string for API calls
     */
    private String convertLocalTimeToUTC(String localTimeString) {
        try {
            // Parse the local time string
            LocalDateTime localDateTime = LocalDateTime.parse(localTimeString, LOCAL_TIME_FORMATTER);

            // Convert to UTC
            ZonedDateTime localZoned = localDateTime.atZone(ZoneId.systemDefault());
            ZonedDateTime utcZoned = localZoned.withZoneSameInstant(ZoneId.of("UTC"));

            // Format with Z suffix
            return utcZoned.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        } catch (Exception e) {
            Log.e("FoodDetailViewModel", "Error converting local time to UTC: " + localTimeString, e);
            // Fallback to using DateUtils method
            Calendar calendar = Calendar.getInstance();
            return DateUtils.formatDateTimeToIso(calendar.getTime());
        }
    }

    public void confirmAction() {
        if (currentFood == null) return;

        if (isSuggestionFood) {
            addFoodToMenu();
        } else {
            updateFoodInMenu();
        }
    }

    public void updateFoodInMenu() {
        if (currentFood == null) {
            errorLiveData.postValue("Dữ liệu món ăn không hợp lệ");
            return;
        }


        // Set meal date and consume time
        currentFood.setMealDate(selectedConsumeTime);
        currentFood.setConsumedAt(selectedConsumeTime);
        currentFood.setMealType(FoodUtils.getMealTypeWithTime(selectedConsumeTime));

        UpdateFoodRequestDto updateRequest = UpdateFoodRequestDto.fromFoodData(currentFood);
        Log.d("FoodDetailViewModel", "Update json: " + updateRequest.toString());
        ApiManager apiManager = ApiManager.getInstance();
        if (apiManager == null || apiManager.getFoodClient() == null) {
            errorLiveData.postValue("Lỗi hệ thống: API client không khả dụng");
            return;
        }

        apiManager.getFoodClient().updateFood(updateRequest, new DataCallback<ApiResponse<FoodDataResponseDto>>() {
            @Override
            public void onLoading(boolean isLoading) {
                isLoadingLiveData.postValue(isLoading);
            }

            @Override
            public void onSuccess(ApiResponse<FoodDataResponseDto> result) {
                if (result.isSuccess()) {
                    originalFood = copyFood(currentFood);
                    hasChangesLiveData.postValue(false);
                    successMessageLiveData.postValue("Cập nhật món ăn thành công");
                    updateConfirmButtonState();
                } else {
                    errorLiveData.postValue(result.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
            }

            @Override
            public void onFailure(Throwable throwable) {
                errorLiveData.postValue("Lỗi khi cập nhật món ăn: " + throwable.getMessage());
            }
        });
    }

    private void addFoodToMenu() {
        if (currentFood == null) {
            errorLiveData.postValue("Dữ liệu món ăn không hợp lệ");
            return;
        }

        // Set meal date and consume time
        currentFood.setMealDate(selectedConsumeTime);
        currentFood.setConsumedAt(selectedConsumeTime);
        currentFood.setMealType(FoodUtils.getMealTypeWithTime(selectedConsumeTime));

        Log.d("FoodDetailViewModel", "Original local time: " + selectedConsumeTime);

        CreateFoodRequestDto createRequest = CreateFoodRequestDto.fromFoodData(currentFood);
        ApiManager apiManager = ApiManager.getInstance();
        if (apiManager == null || apiManager.getFoodClient() == null) {
            errorLiveData.postValue("Lỗi hệ thống: API client không khả dụng");
            return;
        }

        apiManager.getFoodClient().createFood(createRequest, new DataCallback<ApiResponse<FoodDataResponseDto>>() {
            @Override
            public void onLoading(boolean isLoading) {
                isLoadingLiveData.postValue(isLoading);
            }

            @Override
            public void onSuccess(ApiResponse<FoodDataResponseDto> result) {
                if (result.isSuccess()) {
                    originalFood = copyFood(currentFood);
                    hasChangesLiveData.postValue(false);
                    successMessageLiveData.postValue("Thêm món ăn thành công");
                    updateConfirmButtonState();
                } else {
                    errorLiveData.postValue(result.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
            }

            @Override
            public void onFailure(Throwable throwable) {
                errorLiveData.postValue("Lỗi khi thêm món ăn: " + throwable.getMessage());
            }
        });
    }

    public void deleteFood() {
        if (currentFood == null || isSuggestionFood) {
            errorLiveData.postValue("Không thể xóa món ăn");
            return;
        }

        DeleteFoodRequestDto dto = new DeleteFoodRequestDto();
        dto.setId(currentFood.getId());
        Log.d("FoodDetailViewModel", "Delete json: " + dto.toString());
        ApiManager apiManager = ApiManager.getInstance();
        if (apiManager == null || apiManager.getFoodClient() == null) {
            errorLiveData.postValue("Lỗi hệ thống: API client không khả dụng");
            return;
        }

        apiManager.getFoodClient().deleteFood(dto, new DataCallback<ApiResponse<Boolean>>() {
            @Override
            public void onLoading(boolean isLoading) {
                isLoadingLiveData.postValue(isLoading);
            }

            @Override
            public void onSuccess(ApiResponse<Boolean> result) {
                if (result.isSuccess()) {
                    successMessageLiveData.postValue("Đã xóa món ăn khỏi thực đơn");
                } else {
                    errorLiveData.postValue(result.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                errorLiveData.postValue(error);
            }

            @Override
            public void onFailure(Throwable throwable) {
                errorLiveData.postValue("Lỗi khi xóa món ăn: " + throwable.getMessage());
            }
        });
    }

    private void checkForChanges() {
        if (originalFood == null || currentFood == null) return;

        boolean hasChanges = !areIngredientsEqual(originalFood.getIngredients(), currentFood.getIngredients()) ||
                !areConsumeTimesEqual(originalFood.getConsumedAt(), currentFood.getConsumedAt());
        hasChangesLiveData.setValue(hasChanges);
        updateConfirmButtonState();
    }

    private void updateConfirmButtonState() {
        boolean enabled = (isSuggestionFood && currentFood != null) ||
                (!isSuggestionFood && hasChangesLiveData.getValue() != null && hasChangesLiveData.getValue());
        confirmButtonEnabledLiveData.postValue(enabled);
    }

    private boolean areIngredientsEqual(List<FoodIngredientDto> list1, List<FoodIngredientDto> list2) {
        if (list1 == null || list2 == null || list1.size() != list2.size()) return false;

        for (int i = 0; i < list1.size(); i++) {
            FoodIngredientDto ing1 = list1.get(i);
            FoodIngredientDto ing2 = list2.get(i);

            if (!(ing1.getIngredientId() == ing2.getIngredientId()) ||
                    ing1.getQuantity().compareTo(ing2.getQuantity()) != 0) {
                return false;
            }
        }
        return true;
    }

    private boolean areConsumeTimesEqual(String time1, String time2) {
        if (time1 == null && time2 == null) return true;
        if (time1 == null || time2 == null) return false;
        return time1.equals(time2);
    }

    private FoodDataResponseDto copyFood(FoodDataResponseDto original) {
        FoodDataResponseDto copy = new FoodDataResponseDto();
        copy.setId(original.getId());
        copy.setName(original.getName());
        copy.setDescription(original.getDescription());
        copy.setPreparationTimeMinutes(original.getPreparationTimeMinutes());
        copy.setCookingTimeMinutes(original.getCookingTimeMinutes());
        copy.setCalories(original.getCalories());
        copy.setProtein(original.getProtein());
        copy.setCarbohydrates(original.getCarbohydrates());
        copy.setFat(original.getFat());
        copy.setFiber(original.getFiber());
        copy.setMealType(original.getMealType());
        copy.setMealDate(original.getMealDate());
        copy.setConsumedAt(original.getConsumedAt());
        copy.setDifficultyLevel(original.getDifficultyLevel());
        copy.setImageUrl(original.getImageUrl());

        // Deep copy ingredients
        List<FoodIngredientDto> copiedIngredients = new ArrayList<>();
        if (original.getIngredients() != null) {
            for (FoodIngredientDto ingredient : original.getIngredients()) {
                FoodIngredientDto copiedIngredient = new FoodIngredientDto();
                copiedIngredient.setIngredientId(ingredient.getIngredientId());
                copiedIngredient.setIngredientName(ingredient.getIngredientName());
                copiedIngredient.setQuantity(ingredient.getQuantity());
                copiedIngredient.setUnit(ingredient.getUnit());
                copiedIngredients.add(copiedIngredient);
            }
        }
        copy.setIngredients(copiedIngredients);

        // Deep copy instructions
        if (original.getInstructions() != null) {
            copy.setInstructions(new ArrayList<>(original.getInstructions()));
        }

        // Deep copy tips
        if (original.getTips() != null) {
            copy.setTips(new ArrayList<>(original.getTips()));
        }

        return copy;
    }

    // Getters for LiveData
    public MutableLiveData<FoodDataResponseDto> getFoodLiveData() { return foodLiveData; }
    public MutableLiveData<List<FoodIngredientDto>> getIngredientsLiveData() { return ingredientsLiveData; }
    public MutableLiveData<List<String>> getInstructionsLiveData() { return instructionsLiveData; }
    public MutableLiveData<List<String>> getTipsLiveData() { return tipsLiveData; }
    public MutableLiveData<Boolean> getIsLoadingLiveData() { return isLoadingLiveData; }
    public MutableLiveData<String> getErrorLiveData() { return errorLiveData; }
    public MutableLiveData<Boolean> getIsSuggestionLiveData() { return isSuggestionLiveData; }
    public MutableLiveData<Boolean> getHasChangesLiveData() { return hasChangesLiveData; }
    public MutableLiveData<Boolean> getConfirmButtonEnabledLiveData() { return confirmButtonEnabledLiveData; }
    public MutableLiveData<String> getSuccessMessageLiveData() { return successMessageLiveData; }
    public MutableLiveData<Boolean> getShowDateTimePickerLiveData() { return showDateTimePickerLiveData; }
    public MutableLiveData<String> getConsumeTimeLiveData() { return consumeTimeLiveData; }
}