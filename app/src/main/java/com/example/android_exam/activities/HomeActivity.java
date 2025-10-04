package com.example.android_exam.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.android_exam.R;
import com.example.android_exam.adapters.FoodSuggestionsAdapter;
import com.example.android_exam.data.api.ApiManager;
import com.example.android_exam.data.api.DataCallback;
import com.example.android_exam.data.dto.food.FoodDataResponseDto;
import com.example.android_exam.data.dto.food.FoodRecipeRequestDto;
import com.example.android_exam.data.dto.food.FoodSuggestionResponseDto;
import com.example.android_exam.data.dto.nutrition.OverviewNutritionSummaryDto;
import com.example.android_exam.data.dto.response.ApiResponse;
import com.example.android_exam.data.models.base.User;
import com.example.android_exam.databinding.ActivityHomeBinding;
import com.example.android_exam.module.image.ImagePickerHelper;
import com.example.android_exam.viewmodels.HomeViewModel;
import com.google.android.material.navigation.NavigationView;

import android.appwidget.AppWidgetManager;
import android.app.PendingIntent;
import android.content.ComponentName;

import java.math.BigDecimal;

public class HomeActivity extends BaseActivity {
    private HomeViewModel viewModel;
    private ActivityHomeBinding binding;
    private FoodSuggestionsAdapter foodAdapter;

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViewModel();
        initViews();
        observeData();
        setupClickListeners();

        // Initialize DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Handle NavigationView item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    // Already on Home, no action needed
                    Toast.makeText(this, "Đang ở Trang chủ", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.nav_profile) {
                    // Navigate to Profile Activity
                    // Intent intent = new Intent(this, ProfileActivity.class);
                    // startActivity(intent);
                    Toast.makeText(this, "Quản lý thông tin cá nhân", Toast.LENGTH_SHORT).show();
                }
                else if( itemId == R.id.nav_ingredient_management) {
                    // Navigate to Food Diary Activity
                    // Intent intent = new Intent(this, FoodDiaryActivity.class);
                    // startActivity(intent);
                    Toast.makeText(this, "Nhật ký thực phẩm", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.nav_nutrition_analysis) {
                    // Navigate to Nutrition Info Activity
                    // Intent intent = new Intent(this, NutritionInfoActivity.class);
                    // startActivity(intent);
                    Toast.makeText(this, "Thông tin dinh dưỡng", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.nav_settings) {
                    // Navigate to Settings Activity
                    // Intent intent = new Intent(this, SettingsActivity.class);
                    // startActivity(intent);
                    Toast.makeText(this, "Cài đặt", Toast.LENGTH_SHORT).show();
                } else if(itemId == R.id.nav_help) {
                    Toast.makeText(this, "Trợ giúp", Toast.LENGTH_SHORT).show();
                } else if (itemId == R.id.nav_logout) {
                    // Handle logout logic
                    // viewModel.logout();
                    Toast.makeText(this, "Đăng xuất", Toast.LENGTH_SHORT).show();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
        });

        // Load initial data
        viewModel.loadHomeData();
        if (!ImagePickerHelper.isInitialized(this)) {
            ImagePickerHelper.initialize(this);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ImagePickerHelper.handlePermissionResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();

        viewModel.loadHomeData();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    private void initViews() {
        // Setup RecyclerView for food suggestions
        foodAdapter = new FoodSuggestionsAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.rvFoodSuggestions.setLayoutManager(layoutManager);
        binding.rvFoodSuggestions.setAdapter(foodAdapter);
        binding.rvFoodSuggestions.setOnTouchListener(new View.OnTouchListener() {
            private float startX;
            private boolean isRefreshing = false;
            private final int OVERSCROLL_THRESHOLD = 150; // pixels

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        isRefreshing = false;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float currentX = event.getX();
                        float diffX = currentX - startX;

                        // Nếu đã scroll về đầu và người dùng vẫn kéo sang phải (overscroll)
                        if (!binding.rvFoodSuggestions.canScrollHorizontally(-1) &&
                                diffX > OVERSCROLL_THRESHOLD && !isRefreshing) {

                            isRefreshing = true;

                            // Gọi refresh
                            viewModel.loadFoodSuggestions(true);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        isRefreshing = false;
                        break;
                }
                return false; // Cho phép RecyclerView scroll bình thường
            }
        });
        // Set up food item click listener
        foodAdapter.setOnFoodClickListener(this::onFoodSuggestionClick);
    }

    private void observeData() {
        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            // Show/hide loading indicator
            if (isLoading) {
                // Show progress bar or loading overlay
                // You can add a progress bar to your layout and show/hide it here
            } else {
                // Hide progress bar or loading overlay
            }
        });

        viewModel.getIsLoadingFoodSuggestions().observe(this, isLoading -> {
            if (isLoading) {
                binding.progressBarSuggestionFood.setVisibility(View.VISIBLE);
            } else {
                binding.progressBarSuggestionFood.setVisibility(View.GONE);
            }
        });

        // Observe error messages
        viewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe nutrition tip
        viewModel.getNutritionTip().observe(this, tip -> {
            if (tip != null) {
                binding.tvTipTitle.setText(tip.getTitle());
                binding.tvTipContent.setText(tip.getContent());
            }
        });

        // Observe food suggestions
        viewModel.getFoodSuggestions().observe(this, suggestions -> {
            if (suggestions != null) {
                foodAdapter.updateData(suggestions);
            }
        });

        // Observe nutrition progress
        viewModel.getNutritionProgress().observe(this, progress -> {
            if (progress != null) {
                updateNutritionProgress(progress);
            }
        });

        // Observe user profile
        viewModel.getUserProfile().observe(this, profile -> {
            if (profile != null) {
                updateUserProfile(profile);
            }
        });
    }

    private void updateNutritionProgress(OverviewNutritionSummaryDto progress) {
        // Calculate percentages using BigDecimal
        BigDecimal targetCalories = BigDecimal.valueOf(progress.getTargetCalories());
        BigDecimal avgCalories = BigDecimal.valueOf(progress.getAverageCalories());
        int caloriesPercentage = avgCalories.multiply(BigDecimal.valueOf(100))
                .divide(targetCalories, 2, BigDecimal.ROUND_HALF_UP)
                .intValue();
        binding.tvTotalKcal.setText(String.format(" %d / %d", avgCalories.intValue(), targetCalories.intValue()));
        binding.progressKcal.setProgress(Math.min(100, Math.max(0, caloriesPercentage)));

        BigDecimal targetProtein = BigDecimal.valueOf(progress.getTargetProtein());
        BigDecimal avgProtein = BigDecimal.valueOf(progress.getAverageProtein());
        int proteinPercent = avgProtein.multiply(BigDecimal.valueOf(100))
                .divide(targetProtein, 2, BigDecimal.ROUND_HALF_UP)
                .intValue();
        binding.tvProteinProgress.setText(String.format("%d/%dg", avgProtein.intValue(), targetProtein.intValue()));
        binding.progressProtein.setProgress(Math.min(100, Math.max(0, proteinPercent)));

        BigDecimal targetCarbs = BigDecimal.valueOf(progress.getTargetCarbs());
        BigDecimal avgCarbs = BigDecimal.valueOf(progress.getAverageCarbs());
        int carbPercentage = avgCarbs.multiply(BigDecimal.valueOf(100))
                .divide(targetCarbs, 2, BigDecimal.ROUND_HALF_UP)
                .intValue();
        binding.tvCarbsProgress.setText(String.format("%d/%dg", avgCarbs.intValue(), targetCarbs.intValue()));
        binding.progressCarbs.setProgress(Math.min(100, Math.max(0, carbPercentage)));

        BigDecimal targetFat = BigDecimal.valueOf(progress.getTargetFat());
        BigDecimal avgFat = BigDecimal.valueOf(progress.getAverageFat());
        int fatPercentage = avgFat.multiply(BigDecimal.valueOf(100))
                .divide(targetFat, 2, BigDecimal.ROUND_HALF_UP)
                .intValue();
        binding.tvFatProgress.setText(String.format("%d/%dg", avgFat.intValue(), targetFat.intValue()));
        binding.progressFat.setProgress(Math.min(100, Math.max(0, fatPercentage)));

        BigDecimal targetFiber = BigDecimal.valueOf(progress.getTargetFiber());
        BigDecimal avgFiber = BigDecimal.valueOf(progress.getAverageFiber());
        int fiberPercentage = avgFiber.multiply(BigDecimal.valueOf(100))
                .divide(targetFiber, 2, BigDecimal.ROUND_HALF_UP)
                .intValue();
        binding.tvFiberProgress.setText(String.format("%d/%dg", avgFiber.intValue(), targetFiber.intValue()));
        binding.progressFiber.setProgress(Math.min(100, Math.max(0, fiberPercentage)));
    }


    private void updateUserProfile(User user) {
        if (user != null) {
            binding.tvActivityLevel.setText(
                    user.getActivityLevel() != null ? user.getActivityLevel().toString() : "N/A");

            binding.tvNutritionGoal.setText(
                    user.getPrimaryNutritionGoal() != null ? user.getPrimaryNutritionGoal().toString() : "N/A");

            binding.tvGender.setText(
                    user.getGender() != null ? user.getGender().toString() : "N/A");

            binding.tvHeight.setText(
                    user.getHeight() != null ? String.format("%d cm", user.getHeight().intValue()) : "N/A");

            binding.tvWeight.setText(
                    user.getWeight() != null ? String.format("%d kg", user.getWeight().intValue()) : "N/A");

            binding.tvWeightGoal.setText(
                    user.getTargetWeight() != null ? String.format("%d kg", user.getTargetWeight().intValue()) : "N/A");

            binding.tvBmi.setText(
                    user.getBMI() != null ? String.format("%.2f", user.getBMI()) : "N/A");

            binding.tvBmr.setText(
                    user.getBMR() != null ? String.format("%.2f", user.getBMR()) : "N/A");

            binding.tvTdee.setText(
                    user.getTDEE() != null ? String.format("%.2f", user.getTDEE()) : "N/A");
        }
    }


    private void setupClickListeners() {
        binding.btnManageIngredients.setOnClickListener(v -> viewModel.onManageIngredientsClick(HomeActivity.this));
        binding.btnDetectIngredients.setOnClickListener(v -> viewModel.onDetectIngredientsClick(HomeActivity.this));
        binding.btnDetectFood.setOnClickListener(v -> viewModel.onDetectFoodClick(HomeActivity.this));
        binding.btnNutritionInfo.setOnClickListener(v -> viewModel.onNutritionInfoClick(HomeActivity.this));
        binding.nutritionOverviewCard.setOnClickListener(v -> viewModel.onNutritionInfoClick(HomeActivity.this));
        binding.btnLogout.setOnClickListener(v -> {
            // Handle logout logic
            viewModel.logout(HomeActivity.this);
            finish();
        });
        binding.userProfileCard.setOnClickListener(v -> {
            //Start to user profile activity
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void onFoodSuggestionClick(FoodSuggestionResponseDto food) {
        // Handle food suggestion click
        if (viewModel != null) {
            viewModel.onFoodSuggestionClick(food);
        }
        // You can also show a toast or navigate to food detail activity
        Toast.makeText(this, "Clicked: " + food.getName(), Toast.LENGTH_SHORT).show();
        //Call load at here .. fake loading screen
        var foodRecipeRequestDto = new FoodRecipeRequestDto();
        foodRecipeRequestDto.setFoodName(food.getName());
        foodRecipeRequestDto.setIngredients(food.getIngredients());

        //Fake loading action
        showLoading();

        ApiManager.getInstance().getFoodClient().getRecipeSuggestions(foodRecipeRequestDto, new DataCallback<ApiResponse<FoodDataResponseDto>>() {
            @Override
            public void onSuccess(ApiResponse<FoodDataResponseDto> result) {
                hideLoading();
                // Navigate to FoodDetailActivity with the food data
                Intent intent = new Intent(HomeActivity.this, FoodDetailActivity.class);
                FoodDataResponseDto foodData = result.getData();
                intent.putExtra("isSuggestionFood", true);
                intent.putExtra("foodData", foodData);
                startActivity(intent);
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    hideLoading();
                    Toast.makeText(HomeActivity.this, "Error loading food details: " + error, Toast.LENGTH_SHORT).show();
                    Log.e("HomeActivity", "Error loading food details: " + error);
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                runOnUiThread(() -> {
                    hideLoading();
                    Toast.makeText(HomeActivity.this, "Network error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("HomeActivity", "Network error: ", throwable);
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        ImagePickerHelper.cleanup(this);
    }
}