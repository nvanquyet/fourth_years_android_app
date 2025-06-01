package com.example.android_exam.activities;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_exam.R;
import com.example.android_exam.adapters.CalorieAnalysisAdapter;
import com.example.android_exam.adapters.ExpiringIngredientsAdapter;
import com.example.android_exam.adapters.SuggestedMealsAdapter;
import com.example.android_exam.data.local.entity.*;
import com.example.android_exam.data.models.DiaryWithMeals;
import com.example.android_exam.utils.GridSpacingItemDecoration;
import com.example.android_exam.utils.SessionManager;
import com.example.android_exam.utils.DateUtils;
import com.example.android_exam.viewmodels.HomeViewModel;

import java.util.List;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    // UI Components
    private TextView tvAiAnalysis;
    private RecyclerView rvSuggestedMeals;
    private RecyclerView rvCalorieAnalysis;
    private RecyclerView rvExpiringIngredients;
    private TextView tvSeeAllIngredients;
    private TextView tvSeeNutritionAnalysis;

    // ViewModel
    private HomeViewModel viewModel;

    // Data
    private int currentUserId;
    private SuggestedMealsAdapter mealsAdapter;
    private CalorieAnalysisAdapter calorieAnalysisAdapter;
    private ExpiringIngredientsAdapter expiringIngredientsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize
        currentUserId = SessionManager.getCurrentUserId(this);
//        if (currentUserId == -1) {
//            // Redirect to login if not logged in
//            finish();
//            return;
//        }

        initViews();
        setupViewModel();
        setupRecyclerView();
        setupClickListeners();
        setupObservers();

        // Load data
        viewModel.setCurrentUserId(currentUserId);
        viewModel.loadAllData();
    }

    private void initViews() {
        tvAiAnalysis = findViewById(R.id.tv_ai_analysis);
        rvSuggestedMeals = findViewById(R.id.rv_suggested_meals);
        rvExpiringIngredients = findViewById(R.id.rvExpiringIngredients);
        rvCalorieAnalysis = findViewById(R.id.rvCalorieAnalysis);
        tvSeeAllIngredients = findViewById(R.id.tv_see_all_ingredients);
        tvSeeNutritionAnalysis = findViewById(R.id.tv_see_nutrition_analysis);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    private void setupRecyclerView() {
        // Suggested Meals RecyclerView
        mealsAdapter = new SuggestedMealsAdapter(new ArrayList<>(), new SuggestedMealsAdapter.OnMealClickListener() {
            @Override
            public void onMealClick(Food meal) {
                openRecipeDetail(meal);
            }
        });
        rvSuggestedMeals.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvSuggestedMeals.setAdapter(mealsAdapter);

        // Calorie Analysis RecyclerView
        calorieAnalysisAdapter = new CalorieAnalysisAdapter(new ArrayList<>());
        rvCalorieAnalysis.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvCalorieAnalysis.setAdapter(calorieAnalysisAdapter);

        // Expiring Ingredients RecyclerView
        expiringIngredientsAdapter = new ExpiringIngredientsAdapter(this, new ArrayList<>());
        rvExpiringIngredients.setLayoutManager(new GridLayoutManager(this, 2));
        rvExpiringIngredients.addItemDecoration(new GridSpacingItemDecoration(2, 16, true));
        rvExpiringIngredients.setAdapter(expiringIngredientsAdapter);

        expiringIngredientsAdapter.setOnIngredientClickListener(ingredient -> {
            Toast.makeText(this, "Đã chọn: " + ingredient.getName(), Toast.LENGTH_SHORT).show();
            // Có thể thêm Intent để mở Activity chi tiết nếu cần
        });
    }

    private void setupClickListeners() {
        tvSeeAllIngredients.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, IngredientsActivity.class);
            startActivity(intent);
        });

        tvSeeNutritionAnalysis.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, NutritionAnalysisActivity.class);
            startActivity(intent);
        });
    }

    private void setupObservers() {
        // AI Analysis Observer
        viewModel.getAiAnalysis().observe(this, analysis -> {
            if (analysis != null) {
                tvAiAnalysis.setText(analysis);
            }
        });

        // Suggested Meals Observer - SỬA: Sử dụng updateMeals() thay vì tạo adapter mới
        viewModel.getSuggestedMeals().observe(this, meals -> {
            if (meals != null) {
                mealsAdapter.updateMeals(meals); // Thay đổi ở đây
            } else {
                mealsAdapter.updateMeals(new ArrayList<>()); // Handle null case
            }
        });

        // Expiring Ingredients Observer - Giữ nguyên vì đã có updateIngredients()
        viewModel.getExpiringIngredients().observe(this, ingredients -> {
            if (ingredients != null) {
                displayExpiringIngredients(ingredients);
            } else {
                displayExpiringIngredients(new ArrayList<>());
            }
        });

        // Calorie Analysis Observer - SỬA: Sử dụng updateData() thay vì tạo adapter mới
        viewModel.getCalorieAnalysis().observe(this, analysis -> {
            if (analysis != null) {
                calorieAnalysisAdapter.updateData(analysis); // Thay đổi ở đây
            } else {
                calorieAnalysisAdapter.updateData(new ArrayList<>()); // Handle null case
            }
        });

        // Error Message Observer
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        // Loading Observer
        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null && isLoading) {
                tvAiAnalysis.setText("Loading AI analysis...");
                // Có thể thêm loading indicator ở đây
            }
        });
    }


    private void displayExpiringIngredients(List<Ingredient> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            expiringIngredientsAdapter.updateIngredients(new ArrayList<>());
            if (!Boolean.TRUE.equals(viewModel.getIsLoading().getValue())) {
                Toast.makeText(this, "Không có nguyên liệu sắp hết hạn", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        expiringIngredientsAdapter.updateIngredients(ingredients);
    }

    private void openRecipeDetail(Food meal) {
        Intent intent = new Intent(HomeActivity.this, RecipeDetailActivity.class);
        intent.putExtra("MEAL_NAME", meal.name);
        intent.putExtra("MEAL_CALORIES", meal.totalCalories);
        intent.putExtra("MEAL_DESCRIPTION", meal.description);
        intent.putExtra("USER_ID", currentUserId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        if (viewModel != null) {
            viewModel.loadAllData();
        }
    }
}