package com.example.android_exam.activities;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_exam.R;
import com.example.android_exam.adapters.CalorieAnalysisAdapter;
import com.example.android_exam.adapters.SuggestedMealsAdapter;
import com.example.android_exam.data.local.entity.*;
import com.example.android_exam.data.models.DiaryWithMeals;
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
    private LinearLayout llExpiringIngredients;
    private TextView tvSeeAllIngredients;
    private TextView tvSeeNutritionAnalysis;

    // ViewModel
    private HomeViewModel viewModel;

    // Data
    private int currentUserId;
    private SuggestedMealsAdapter mealsAdapter;
    private CalorieAnalysisAdapter calorieAnalysisAdapter;

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
        llExpiringIngredients = findViewById(R.id.ll_expiring_ingredients);
        rvCalorieAnalysis = findViewById(R.id.rvCalorieAnalysis);
        tvSeeAllIngredients = findViewById(R.id.tv_see_all_ingredients);
        tvSeeNutritionAnalysis = findViewById(R.id.tv_see_nutrition_analysis);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    private void setupRecyclerView() {
        mealsAdapter = new SuggestedMealsAdapter(new ArrayList<>(), new SuggestedMealsAdapter.OnMealClickListener() {
            @Override
            public void onMealClick(Food meal) {
                openRecipeDetail(meal);
            }
        });

        rvSuggestedMeals.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvSuggestedMeals.setAdapter(mealsAdapter);


        calorieAnalysisAdapter = new CalorieAnalysisAdapter(new ArrayList<>());
        rvCalorieAnalysis.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvCalorieAnalysis.setAdapter(calorieAnalysisAdapter);
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

        // Suggested Meals Observer
        viewModel.getSuggestedMeals().observe(this, meals -> {
            if (meals != null) {
                mealsAdapter = new SuggestedMealsAdapter(meals, this::openRecipeDetail);
                rvSuggestedMeals.setAdapter(mealsAdapter);
            }
        });

        // Expiring Ingredients Observer
        viewModel.getExpiringIngredients().observe(this, ingredients -> {
            if (ingredients != null) {
                displayExpiringIngredients(ingredients);
            }
        });

        // Calorie Analysis Observer
        viewModel.getCalorieAnalysis().observe(this, analysis -> {
            if (analysis != null) {
                calorieAnalysisAdapter = new CalorieAnalysisAdapter(analysis);
                rvCalorieAnalysis.setAdapter(calorieAnalysisAdapter);
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
            // You can show/hide loading indicator here
            if (isLoading != null && isLoading) {
                tvAiAnalysis.setText("Loading AI analysis...");
            }
        });
    }

    private void displayExpiringIngredients(List<Ingredient> ingredients) {
        llExpiringIngredients.removeAllViews();

        if (ingredients.isEmpty()) {
            TextView noItems = new TextView(this);
            noItems.setText("No expiring ingredients");
            noItems.setTextColor(getResources().getColor(android.R.color.darker_gray));
            llExpiringIngredients.addView(noItems);
            return;
        }

        // Show only first 5 items
        int itemsToShow = Math.min(ingredients.size(), 5);

        for (int i = 0; i < itemsToShow; i++) {
            Ingredient ingredient = ingredients.get(i);

            View itemView = getLayoutInflater().inflate(R.layout.item_expiring_ingredient, llExpiringIngredients, false);
            TextView tvName = itemView.findViewById(R.id.tv_ingredient_name);
            TextView tvExpiry = itemView.findViewById(R.id.tv_expiry_date);

            tvName.setText(ingredient.name);
            tvExpiry.setText("Expires: " + ingredient.expiryDate);

            // Set warning color based on days left
            int daysLeft = DateUtils.getDaysUntilExpiry(ingredient.expiryDate);
            if (daysLeft <= 1) {
                itemView.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            } else if (daysLeft <= 3) {
                itemView.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
            }

            llExpiringIngredients.addView(itemView);
        }
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