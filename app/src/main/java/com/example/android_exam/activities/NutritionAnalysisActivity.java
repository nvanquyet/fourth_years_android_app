package com.example.android_exam.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.android_exam.R;
import com.example.android_exam.adapters.EnhancedMealAdapter;
import com.example.android_exam.adapters.EnhancedWeeklyNutritionAdapter;
import com.example.android_exam.data.api.ApiManager;
import com.example.android_exam.data.api.DataCallback;
import com.example.android_exam.data.dto.food.FoodDataResponseDto;
import com.example.android_exam.data.dto.nutrition.DailyNutritionSummaryDto;
import com.example.android_exam.data.dto.nutrition.FoodNutritionDto;
import com.example.android_exam.data.dto.nutrition.WeeklyNutritionSummaryDto;
import com.example.android_exam.data.dto.response.ApiResponse;
import com.example.android_exam.databinding.ActivityNutritionAnalysisBinding;
import com.example.android_exam.viewmodels.NutritionAnalysisViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NutritionAnalysisActivity extends AppCompatActivity {
    private static final String TAG = "NutritionAnalysisActivity";
    private ActivityNutritionAnalysisBinding binding;
    private NutritionAnalysisViewModel viewModel;
    private EnhancedWeeklyNutritionAdapter weeklyAdapter;
    private EnhancedMealAdapter mealAdapter;
    private boolean isWeekMode = false;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'Tháng' MM, yyyy", new Locale("vi", "VN"));
    private SimpleDateFormat weekFormat = new SimpleDateFormat("dd/MM", new Locale("vi", "VN"));
    private SimpleDateFormat logFormat = new SimpleDateFormat("yyyy-MM-dd", new Locale("vi", "VN"));
    private Calendar selectedCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNutritionAnalysisBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initViewModel();
        setupUI();
        setupObservers();
        loadInitialData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Clear cache from view model and load again
        viewModel.clearCache();
        loadInitialData();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(NutritionAnalysisViewModel.class);
    }

    private void setupUI() {
        setupTabs();
        setupNavigationButtons();
        setupDatePicker();
        setupRecyclerViews();
    }

    private void setupTabs() {
        binding.tabDay.setSelected(true);
        binding.tabWeek.setSelected(false);

        binding.tabDay.setOnClickListener(v -> {
            if (isWeekMode) {
                switchToDateMode();
            }
        });

        binding.tabWeek.setOnClickListener(v -> {
            if (!isWeekMode) {
                switchToWeekMode();
            }
        });
    }

    private void setupNavigationButtons() {
        binding.btnPrevious.setOnClickListener(v -> {
            if (isWeekMode) {
                viewModel.navigateToPreviousWeek();
            } else {
                viewModel.navigateToPreviousDay();
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            if (isWeekMode) {
                viewModel.navigateToNextWeek();
            } else {
                viewModel.navigateToNextDay();
            }
        });
    }

    private void setupDatePicker() {
        binding.tvSelectedDate.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedCalendar.set(Calendar.YEAR, year);
                    selectedCalendar.set(Calendar.MONTH, month);
                    selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    Date selectedDate = selectedCalendar.getTime();

                    // Log selected date
                    Log.d(TAG, "Selected date: " + logFormat.format(selectedDate));

                    if (isWeekMode) {
                        viewModel.setSelectedWeek(selectedDate);
                    } else {
                        viewModel.setSelectedDate(selectedDate);
                    }
                },
                selectedCalendar.get(Calendar.YEAR),
                selectedCalendar.get(Calendar.MONTH),
                selectedCalendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void setupRecyclerViews() {
        weeklyAdapter = new EnhancedWeeklyNutritionAdapter(this::onDaySelected);

        // Create meal adapter with food item click listener
        mealAdapter = new EnhancedMealAdapter(
                date -> {
                    // Log date selected from meal adapter
                    Log.d(TAG, "Selected date from meal: " + logFormat.format(date.getTime()));
                    viewModel.setSelectedDate(date.getTime());
                    switchToDateMode();
                },
                this::onFoodItemClick  // Add food item click listener
        );

        binding.rvMeals.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMeals.setAdapter(mealAdapter);
    }

    // Handle food item click
    private void onFoodItemClick(FoodNutritionDto foodItem) {
        // You can also show a toast message to confirm the click
        Toast.makeText(this, "Clicked: " + foodItem.getFoodName(), Toast.LENGTH_SHORT).show();
        LoadingActivity.getInstance().show();
        ApiManager.getInstance().getFoodClient().getFoodById(foodItem.getFoodId(), new DataCallback<ApiResponse<FoodDataResponseDto>>() {
            @Override
            public void onSuccess(ApiResponse<FoodDataResponseDto> result) {
                //Get the food data and show it in a dialog or new activity
                FoodDataResponseDto foodData = result.getData();
                //put to intent and start a new activity to show food details
                LoadingActivity.getInstance().hide();
                Log.d(TAG, "Food details: " + foodData.toString());
                Intent intent = new Intent(NutritionAnalysisActivity.this, FoodDetailActivity.class);
                intent.putExtra("isSuggestionFood", false);
                intent.putExtra("foodData", foodData);
                startActivity(intent);
            }

            @Override
            public void onError(String error) {
                LoadingActivity.getInstance().hide();
            }

            @Override
            public void onFailure(Throwable throwable) {
                LoadingActivity.getInstance().hide();
            }
        });
        //Call to api
    }

    private void setupObservers() {
        viewModel.getCurrentDate().observe(this, this::updateDateDisplay);
        viewModel.getDailyNutrition().observe(this, this::updateDailyView);
        viewModel.getWeeklyNutrition().observe(this, this::updateWeeklyView);
        viewModel.getIsLoading().observe(this, this::updateLoadingState);
        viewModel.getError().observe(this, this::handleError);
    }

    private void loadInitialData() {
        viewModel.loadTodayNutrition();
    }

    private void switchToDateMode() {
        isWeekMode = false;
        updateTabSelection();
        binding.rvMeals.setAdapter(mealAdapter);
        Date currentDate = viewModel.getCurrentDate().getValue();
        if (currentDate != null) {
            viewModel.loadDailyNutrition(currentDate);
        } else {
            viewModel.loadTodayNutrition();
        }
    }

    private void switchToWeekMode() {
        isWeekMode = true;
        updateTabSelection();
        binding.rvMeals.setAdapter(weeklyAdapter);
        Date currentDate = viewModel.getCurrentDate().getValue();
        if (currentDate != null) {
            viewModel.loadWeeklyNutrition(currentDate);
        } else {
            viewModel.loadCurrentWeekNutrition();
        }
    }

    private void updateTabSelection() {
        binding.tabDay.setSelected(!isWeekMode);
        binding.tabWeek.setSelected(isWeekMode);
    }

    private void updateDateDisplay(Date date) {
        selectedCalendar.setTime(date);

        if (isWeekMode) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            int daysFromMonday = (dayOfWeek + 5) % 7;
            cal.add(Calendar.DAY_OF_MONTH, -daysFromMonday);
            Date weekStart = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, 6);
            Date weekEnd = cal.getTime();

            binding.tvSelectedDate.setText(String.format("Tuần %s - %s",
                    weekFormat.format(weekStart), weekFormat.format(weekEnd)));
        } else {
            Calendar today = Calendar.getInstance();
            if (isSameDay(date, today.getTime())) {
                binding.tvSelectedDate.setText("Hôm nay, " + new SimpleDateFormat("dd 'Tháng' MM", new Locale("vi", "VN")).format(date));
            } else {
                binding.tvSelectedDate.setText(dateFormat.format(date));
            }
        }
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private void updateDailyView(DailyNutritionSummaryDto dailyData) {
        if (dailyData == null) return;

        updateNutritionSummary(
                dailyData.getTotalCalories(),
                dailyData.getTotalProtein(),
                dailyData.getTotalCarbs(),
                dailyData.getTotalFat(),
                dailyData.getTotalFiber(),
                dailyData.getTargetCalories(),
                dailyData.getTargetProtein(),
                dailyData.getTargetCarbs(),
                dailyData.getTargetFat(),
                dailyData.getTargetFiber()
        );

        if (!isWeekMode) {
            mealAdapter.updateMeals(dailyData.getMealBreakdown());
        }
    }

    private void updateWeeklyView(WeeklyNutritionSummaryDto weeklyData) {
        if (weeklyData == null) return;

        updateNutritionSummary(
                weeklyData.getAverageCalories(),
                weeklyData.getAverageProtein(),
                weeklyData.getAverageCarbs(),
                weeklyData.getAverageFat(),
                weeklyData.getAverageFiber(),
                weeklyData.getTargetCalories(),
                weeklyData.getTargetProtein(),
                weeklyData.getTargetCarbs(),
                weeklyData.getTargetFat(),
                weeklyData.getTargetFiber()
        );

        if (isWeekMode) {
            weeklyAdapter.updateWeeklyData(weeklyData.getDailyBreakdown());
        }
    }

    private void updateNutritionSummary(double calories, double protein, double carbs,
                                        double fat, double fiber, Double targetCalories,
                                        Double targetProtein, Double targetCarbs,
                                        Double targetFat, Double targetFiber) {
        binding.tvCaloriesCurrent.setText(String.format("%.0f", calories));
        if (targetCalories != null) {
            binding.tvCaloriesTarget.setText(String.format("%.0f kcal", targetCalories));
            int caloriesProgress = (int) ((calories / targetCalories) * 100);
            binding.progressCalories.setProgress(Math.min(caloriesProgress, 100));
        }

        binding.tvProteinProgress.setText(String.format("%.0f/%.0fg", protein, targetProtein != null ? targetProtein : 0));
        if (targetProtein != null) {
            int proteinProgress = (int) ((protein / targetProtein) * 100);
            binding.progressProtein.setProgress(Math.min(proteinProgress, 100));
        }

        binding.tvCarbsProgress.setText(String.format("%.0f/%.0fg", carbs, targetCarbs != null ? targetCarbs : 0));
        if (targetCarbs != null) {
            int carbsProgress = (int) ((carbs / targetCarbs) * 100);
            binding.progressCarbs.setProgress(Math.min(carbsProgress, 100));
        }

        binding.tvFatProgress.setText(String.format("%.0f/%.0fg", fat, targetFat != null ? targetFat : 0));
        if (targetFat != null) {
            int fatProgress = (int) ((fat / targetFat) * 100);
            binding.progressFat.setProgress(Math.min(fatProgress, 100));
        }

        binding.tvFiberProgress.setText(String.format("%.0f/%.0fg", fiber, targetFiber != null ? targetFiber : 0));
        if (targetFiber != null) {
            int fiberProgress = (int) ((fiber / targetFiber) * 100);
            binding.progressFiber.setProgress(Math.min(fiberProgress, 100));
        }
    }

    private void updateLoadingState(boolean isLoading) {
        binding.rvMeals.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void handleError(String error) {
        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        }
    }

    private void onDaySelected(DailyNutritionSummaryDto selectedDay) {
        isWeekMode = false;
        updateTabSelection();
        binding.rvMeals.setAdapter(mealAdapter);
        viewModel.setSelectedDate(selectedDay.getDate());
        updateDailyView(selectedDay);
        Log.d(TAG, "Selected day from weekly view: " + logFormat.format(selectedDay.getDate()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.getCurrentDate().removeObservers(this);
        viewModel.getDailyNutrition().removeObservers(this);
        viewModel.getWeeklyNutrition().removeObservers(this);
        viewModel.getIsLoading().removeObservers(this);
        viewModel.getError().removeObservers(this);
        binding = null;
    }
}