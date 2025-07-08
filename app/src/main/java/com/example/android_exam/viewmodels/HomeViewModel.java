package com.example.android_exam.viewmodels;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android_exam.R;
import com.example.android_exam.activities.FoodDetailActivity;
import com.example.android_exam.activities.HomeActivity;
import com.example.android_exam.activities.IngredientManagementActivity;
import com.example.android_exam.activities.LoadingActivity;
import com.example.android_exam.activities.LoginActivity;
import com.example.android_exam.activities.NutritionAnalysisActivity;
import com.example.android_exam.data.api.ApiManager;
import com.example.android_exam.data.api.AuthCallback;
import com.example.android_exam.data.api.DataCallback;
import com.example.android_exam.data.dto.food.FoodAnalysticResponseDto;
import com.example.android_exam.data.dto.food.FoodSuggestionResponseDto;
import com.example.android_exam.data.dto.ingredient.IngredientFilterDto;
import com.example.android_exam.data.dto.ingredient.IngredientSearchResultDto;
import com.example.android_exam.data.dto.nutrition.NutritionTip;
import com.example.android_exam.data.dto.nutrition.OverviewNutritionSummaryDto;
import com.example.android_exam.data.dto.response.ApiResponse;
import com.example.android_exam.data.dto.user.UserProfileDto;
import com.example.android_exam.data.models.base.User;
import com.example.android_exam.data.models.enums.ActivityLevel;
import com.example.android_exam.data.models.enums.Gender;
import com.example.android_exam.data.models.enums.NutritionGoal;
import com.example.android_exam.data.repository.HomeRepository;
import com.example.android_exam.databinding.DialogIngredientDetailBinding;
import com.example.android_exam.module.image.ImagePickerHelper;
import com.example.android_exam.module.image.ImagePickerModule;
import com.example.android_exam.utils.FileUtils;
import com.example.android_exam.utils.SessionManager;
import com.google.gson.Gson;

import java.io.File;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private HomeRepository repository;

    // LiveData for UI
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<NutritionTip> nutritionTip = new MutableLiveData<>();
    private MutableLiveData<List<FoodSuggestionResponseDto>> foodSuggestions = new MutableLiveData<>();
    private MutableLiveData<OverviewNutritionSummaryDto> nutritionProgress = new MutableLiveData<>();
    private MutableLiveData<User> userProfile = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadingFoodSuggestions = new MutableLiveData<>(false);
    public HomeViewModel() {
        repository = HomeRepository.getInstance();
    }

    // Getters for LiveData
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<NutritionTip> getNutritionTip() { return nutritionTip; }
    public LiveData<List<FoodSuggestionResponseDto>> getFoodSuggestions() { return foodSuggestions; }
    public LiveData<OverviewNutritionSummaryDto> getNutritionProgress() { return nutritionProgress; }
    public LiveData<Boolean> getIsLoadingFoodSuggestions() {
        return isLoadingFoodSuggestions;
    }


    public LiveData<User> getUserProfile() { return userProfile; }

    public void loadHomeData() {
        loadNutritionTip();
        loadFoodSuggestions(false);
        loadNutritionProgress();
        loadUserProfile();
    }

    private void loadNutritionTip() {
        repository.getNutritionTip(new DataCallback<ApiResponse<NutritionTip>>() {
            @Override
            public void onLoading(boolean loading) {
                isLoading.setValue(loading);
            }

            @Override
            public void onSuccess(ApiResponse<NutritionTip> response) {
                if (response.isSuccess()) {
                    nutritionTip.setValue(response.getData());
                } else {
                    errorMessage.setValue(response.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                errorMessage.setValue(message);
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

    public void loadFoodSuggestions(boolean forceRefresh) {
        Boolean currentLoading = isLoadingFoodSuggestions.getValue();
        if (currentLoading != null && currentLoading) {
            Log.d("HomeViewModel", "Food suggestions are already loading");
            return;
        }
        isLoadingFoodSuggestions.postValue(true);
        repository.getFoodSuggestions(forceRefresh, new DataCallback<ApiResponse<List<FoodSuggestionResponseDto>>>() {
            @Override
            public void onLoading(boolean loading) {
                // Loading state handled by main loading indicator
            }

            @Override
            public void onSuccess(ApiResponse<List<FoodSuggestionResponseDto>> response) {
                if (response.isSuccess()) {
                    isLoadingFoodSuggestions.postValue(false);
                    foodSuggestions.postValue(response.getData());
                } else {
                    isLoadingFoodSuggestions.postValue(false);
                    errorMessage.postValue(response.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                isLoadingFoodSuggestions.postValue(false);
                errorMessage.postValue(message);
            }

            @Override
            public void onFailure(Throwable throwable) {
                isLoadingFoodSuggestions.postValue(false);
            }
        });
    }

    private void loadNutritionProgress() {
        repository.getNutritionOverview(new DataCallback<ApiResponse<OverviewNutritionSummaryDto>>() {
            @Override
            public void onLoading(boolean loading) {
                // Loading state handled by main loading indicator
            }

            @Override
            public void onSuccess(ApiResponse<OverviewNutritionSummaryDto> response) {
                if (response.isSuccess()) {
                    //log response with json for debugging
                    Log.d("HomeViewModel", "Nutrition progress loaded: " + new Gson().toJson(response));
                    nutritionProgress.postValue(response.getData());
                } else {
                    errorMessage.postValue(response.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                errorMessage.postValue(message);
            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

    private void loadUserProfile() {
        SessionManager.getUser(new SessionManager.UserCallback() {
            @Override
            public void onUserLoaded(User user) {
                userProfile.setValue(user);
                updateUserProfile(
                        String.valueOf(user.getHeight()),
                        String.valueOf(user.getWeight()),
                        user.getGender() != null ? user.getGender().toString() : "",
                        user.getActivityLevel() != null ? user.getActivityLevel().toString() : "",
                        user.getPrimaryNutritionGoal() != null ? user.getPrimaryNutritionGoal().toString() : "",
                        String.valueOf(user.getTargetWeight())
                );
            }

            @Override
            public void onError(String error) {
                Toast.makeText(null, "Error loading user profile: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Action methods
    public void onManageIngredientsClick(Context context) {
        // Call Api to get ingredients
        IngredientFilterDto filter = new IngredientFilterDto();
        filter.setSortBy("expiryDate");
        filter.setSortDirection("asc");
        LoadingActivity.getInstance().show();
        ApiManager.getInstance().getIngredientClient().getAllIngredients(filter, new DataCallback<ApiResponse<IngredientSearchResultDto>>() {
            @Override
            public void onSuccess(ApiResponse<IngredientSearchResultDto> result) {
                // Navigate to IngredientManagementActivity with the result data
                LoadingActivity.getInstance().hide();
                if (result.isSuccess()) {
                    Intent intent = new Intent(context, IngredientManagementActivity.class);
                    intent.putExtra("ingredients", result.getData());
                    context.startActivity(intent);
                } else {
                    Log.d("HomeViewModel", "Failed to load ingredients: " + result.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                // Handle error
                LoadingActivity.getInstance().hide();
                Log.d("HomeViewModel", "Error loading ingredients: " + error);
            }

            @Override
            public void onFailure(Throwable throwable) {
                // Handle failure
                LoadingActivity.getInstance().hide();
                Log.d("HomeViewModel", "Failure loading ingredients: " + throwable.getMessage());
            }
        });
    }

    public void onDetectIngredientsClick(Context context) {
        IngredientFilterDto filter = new IngredientFilterDto();
        filter.setSortBy("expiryDate");
        filter.setSortDirection("asc");
        LoadingActivity.getInstance().show();
        ApiManager.getInstance().getIngredientClient().getAllIngredients(filter, new DataCallback<ApiResponse<IngredientSearchResultDto>>() {
            @Override
            public void onSuccess(ApiResponse<IngredientSearchResultDto> result) {
                // Navigate to IngredientManagementActivity with the result data
                LoadingActivity.getInstance().hide();
                if (result.isSuccess()) {
                    // Handle navigation to detect ingredients screen
                    //Show dialog add ingredient
                    Intent intent = new Intent(context, IngredientManagementActivity.class);
                    intent.putExtra("ingredients", result.getData());
                    intent.putExtra("openDialog", true);
                    intent.putExtra("detectMode", true);
                    context.startActivity(intent);
                } else {
                    Log.d("HomeViewModel", "Failed to load ingredients: " + result.getMessage());
                }
            }

            @Override
            public void onError(String error) {
                // Handle error
                LoadingActivity.getInstance().hide();
                Log.d("HomeViewModel", "Error loading ingredients: " + error);
            }

            @Override
            public void onFailure(Throwable throwable) {
                // Handle failure
                LoadingActivity.getInstance().hide();
                Log.d("HomeViewModel", "Failure loading ingredients: " + throwable.getMessage());
            }
        });

    }

    public void onDetectFoodClick(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_detect_food, null);

        ImageView imagePreview = view.findViewById(R.id.imagePreview);
        Button btnSelectImage = view.findViewById(R.id.btnSelectImage);
        EditText foodName = view.findViewById(R.id.foodName);
        Button btnDetect = view.findViewById(R.id.btnDetect);
        Button btnDetail = view.findViewById(R.id.btnDetail);

        final FoodAnalysticResponseDto[] detectedFood = new FoodAnalysticResponseDto[1];

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view)
                .setCancelable(true)
                .create();

        final Uri[] selectedImageUri = new Uri[1]; // Dùng mảng 1 phần tử để giữ được reference

        btnSelectImage.setOnClickListener(v -> {
            if (context instanceof AppCompatActivity) {
                ImagePickerHelper.pickImage((AppCompatActivity) context, new ImagePickerModule.ImagePickerCallback() {
                    @Override
                    public void onImagePicked(Uri imageUri) {
                        selectedImageUri[0] = imageUri;
                        imagePreview.setImageURI(imageUri);
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(context, "Lỗi khi chọn ảnh: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(context, "Không thể chọn ảnh vì context không phải là Activity", Toast.LENGTH_SHORT).show();
            }

        });

        btnDetect.setOnClickListener(v -> {
            if (selectedImageUri[0] != null) {
                //Convert Uri to File
                var file = FileUtils.getFileFromUri(context, selectedImageUri[0]);
                //Show loading dialog
                Log.d("HomeViewModel", "Selected image file: " + file.getAbsolutePath());
                LoadingActivity.getInstance().show(context);
                ApiManager.getInstance().getAiClient().detectFood(file, new AuthCallback<ApiResponse<FoodAnalysticResponseDto>>() {
                    @Override
                    public void onSuccess(ApiResponse<FoodAnalysticResponseDto> result) {
                        LoadingActivity.getInstance().hide();
                        if (context instanceof Activity) {
                            ((Activity) context).runOnUiThread(() -> {
                                if (result.getData() == null) {
                                    Toast.makeText(context, "Không phát hiện được món ăn", Toast.LENGTH_SHORT).show();
                                } else {
                                    detectedFood[0] = result.getData();
                                    foodName.setText(result.getData().getName());
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(String error) {
                        LoadingActivity.getInstance().hide();
                        if (context instanceof Activity) {
                            ((Activity) context).runOnUiThread(() ->
                                    Toast.makeText(context, "Lỗi: " + error, Toast.LENGTH_SHORT).show()
                            );
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        LoadingActivity.getInstance().hide();
                        if (context instanceof Activity) {
                            ((Activity) context).runOnUiThread(() ->
                                    Toast.makeText(context, "Lỗi mạng: " + throwable.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                        }
                    }
                });
            } else {
                Toast.makeText(context, "Vui lòng chọn ảnh trước", Toast.LENGTH_SHORT).show();
            }
        });

        btnDetail.setOnClickListener(v -> {
            //Lưu cache and switch to detail screen
            var food = detectedFood[0];
            if (food == null) {
                Toast.makeText(context, "Vui lòng phát hiện món ăn trước", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d("FoodDetail", "Nội dung món ăn: " + food);
            //Create content
            Intent intent = new Intent(context, FoodDetailActivity.class);
            intent.putExtra("isSuggestionFood", true);
            intent.putExtra("foodData", food);
            context.startActivity(intent);

        });

        dialog.show();
    }


    public void onFoodSuggestionClick(FoodSuggestionResponseDto foodSuggestion) {
        // Handle navigation to food detail screen with the selected food suggestion
    }

    public void onNutritionInfoClick(Context context) {
        LoadingActivity.getInstance().show();
        // Handle navigation to nutrition info screen
        Intent intent = new Intent(context, NutritionAnalysisActivity.class);
        context.startActivity(intent);
    }

    public void refreshData() {
        loadHomeData();
    }

    public void updateUserProfile(String height, String weight, String gender,
                                  String activityLevel, String nutritionGoal, String weightGoal) {
        SessionManager.getUser(new SessionManager.UserCallback() {
            @Override
            public void onUserLoaded(User user) {
                Log.d("HomeViewModel", "User loaded: " + user.toJson());
                UserProfileDto userProfileDto = UserProfileDto.fromUser(user);
                userProfileDto.setHeight(safeParseDouble(height, 0));
                userProfileDto.setWeight(safeParseDouble(weight, 0));
                userProfileDto.setTargetWeight(safeParseDouble(weightGoal, 0));
                userProfileDto.setGender(Gender.parseGender(gender));
                userProfileDto.setActivityLevel(ActivityLevel.parseActivityLevel(activityLevel));
                userProfileDto.setPrimaryNutritionGoal(NutritionGoal.parseNutritionGoal(nutritionGoal));
            }

            @Override
            public void onError(String error) {
                Log.d("HomeViewModel", "Error loading user profile: " + error);
            }
        });
    }

    private double safeParseDouble(String value, double defaultValue) {
        if (value == null || value.isEmpty() || value.equalsIgnoreCase("null")) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public void logout(Context context) {
        // Xoá dữ liệu phiên
        SessionManager.clearUser();

        // Xoá toàn bộ SharedPreferences
        clearAllSharedPreferences(context);

        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public void clearAllSharedPreferences(Context context) {
        File sharedPrefsDir = new File(context.getApplicationInfo().dataDir, "shared_prefs");
        if (sharedPrefsDir.exists() && sharedPrefsDir.isDirectory()) {
            File[] files = sharedPrefsDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    String prefName = file.getName().replace(".xml", "");
                    SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
                    prefs.edit().clear().apply();
                }
            }
        }
    }

}