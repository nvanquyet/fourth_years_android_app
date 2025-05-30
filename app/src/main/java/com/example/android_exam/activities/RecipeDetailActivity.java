package com.example.android_exam.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.android_exam.R;
import com.example.android_exam.data.local.entity.Meal;
import com.example.android_exam.data.local.entity.MealType;
import com.example.android_exam.utils.DateUtils;
import com.example.android_exam.viewmodels.RecipeDetailViewModel;

public class RecipeDetailActivity extends AppCompatActivity {

    private TextView tvMealName, tvMealCalories, tvMealDescription;
    private Button btnCookMeal;
    private RecipeDetailViewModel viewModel;
    private int userId;
    private String mealName;
    private double mealCalories;
    private String mealDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        userId = getIntent().getIntExtra("USER_ID", -1);
        mealName = getIntent().getStringExtra("MEAL_NAME");
        mealCalories = getIntent().getDoubleExtra("MEAL_CALORIES", 0);
        mealDescription = getIntent().getStringExtra("MEAL_DESCRIPTION");

        if (userId == -1) {
            finish();
            return;
        }

        initViews();
        setupViewModel();
        setupObservers();
        displayMealDetails();
        setupCookButton();
    }

    private void initViews() {
        tvMealName = findViewById(R.id.tv_meal_name);
        tvMealCalories = findViewById(R.id.tv_meal_calories);
        tvMealDescription = findViewById(R.id.tv_meal_description);
        btnCookMeal = findViewById(R.id.btn_cook_meal);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(RecipeDetailViewModel.class);
    }

    private void setupObservers() {
        viewModel.getSuccessMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayMealDetails() {
        tvMealName.setText(mealName);
        tvMealCalories.setText(String.format("%.0f kcal", mealCalories));
        tvMealDescription.setText(mealDescription != null ? mealDescription : "No description available");
    }

    private void setupCookButton() {
        btnCookMeal.setOnClickListener(v -> {
            Meal meal = new Meal();
            meal.userId = userId;
            meal.date = DateUtils.getTodayString();
            meal.mealType = MealType.LUNCH; // Default, can be customized
            meal.totalCalories = mealCalories;
            viewModel.cookMeal(meal);
        });
    }
}