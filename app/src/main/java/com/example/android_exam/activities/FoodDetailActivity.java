package com.example.android_exam.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.android_exam.R;
import com.example.android_exam.adapters.IngredientsFoodAdapter;
import com.example.android_exam.adapters.InstructionsAdapter;
import com.example.android_exam.adapters.TipsAdapter;
import com.example.android_exam.data.dto.food.FoodDataResponseDto;
import com.example.android_exam.databinding.ActivityFoodDetailBinding;
import com.example.android_exam.utils.DateUtils;
import com.example.android_exam.viewmodels.FoodDetailViewModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class FoodDetailActivity extends AppCompatActivity {
    private FoodDetailViewModel viewModel;
    private ActivityFoodDetailBinding binding;
    private IngredientsFoodAdapter ingredientsAdapter;
    private InstructionsAdapter instructionsAdapter;
    private TipsAdapter tipsAdapter;
    private boolean isSuggestionFood;
    private FoodDataResponseDto foodData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFoodDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get data from intent
        getIntentData();

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(FoodDetailViewModel.class);

        // Setup UI
        setupUI();
        setupRecyclerViews();
        setupObservers();
        setupClickListeners();

        // Load food detail
        viewModel.loadFoodDetail(foodData, isSuggestionFood);
    }


    private void getIntentData() {
        Intent intent = getIntent();
        isSuggestionFood = intent.getBooleanExtra("isSuggestionFood", false);
        foodData = (FoodDataResponseDto) intent.getSerializableExtra("foodData");
        if (foodData == null) {
            Log.e("FoodDetailActivity", "Food data is null");
            showError("Không tìm thấy dữ liệu món ăn");
            finish();
            return;
        }
        Log.d("FoodDetailActivity", "Received food data: " + foodData);
    }

    private void setupUI() {
        // Setup status bar
        getWindow().setStatusBarColor(ContextCompat.getColor(this, android.R.color.transparent));
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    private void setupRecyclerViews() {
        // Setup ingredients adapter
        ingredientsAdapter = new IngredientsFoodAdapter((ingredientId, newQuantity) ->
                viewModel.updateIngredientQuantity(ingredientId, newQuantity));

        // Setup steps adapter
        instructionsAdapter = new InstructionsAdapter();

        tipsAdapter = new TipsAdapter();

        // Set adapters to RecyclerViews
        LinearLayoutManager ingredientsLayoutManager = new LinearLayoutManager(this);
        binding.rvIngredients.setLayoutManager(ingredientsLayoutManager);
        binding.rvIngredients.setAdapter(ingredientsAdapter);

        LinearLayoutManager instructionsLayoutManager = new LinearLayoutManager(this);
        binding.rvInstructions.setLayoutManager(instructionsLayoutManager);
        binding.rvInstructions.setAdapter(instructionsAdapter);

        LinearLayoutManager tipsLayoutManager = new LinearLayoutManager(this);
        binding.rvTips.setLayoutManager(tipsLayoutManager);
        binding.rvTips.setAdapter(tipsAdapter);

        // Disable nested scrolling
        binding.rvIngredients.setNestedScrollingEnabled(false);
        binding.rvInstructions.setNestedScrollingEnabled(false);
        binding.rvTips.setNestedScrollingEnabled(false);
    }

    private void setupObservers() {
        // Observe food data
        viewModel.getFoodLiveData().observe(this, food -> {
            if (food != null) {
                updateFoodUI(food);
            }
        });

        // Observe ingredients
        viewModel.getIngredientsLiveData().observe(this, ingredients -> {
            if (ingredients != null) {
                ingredientsAdapter.setIngredients(ingredients);
            }
        });

        // Observe instructions
        viewModel.getInstructionsLiveData().observe(this, instructions -> {
            if (instructions != null) {
                instructionsAdapter.setInstructions(instructions);
            }
        });

        // Observe tips
        viewModel.getTipsLiveData().observe(this, tips -> {
            if (tips != null) {
                tipsAdapter.setTips(tips);
            }
        });

        // Observe loading state
        viewModel.getIsLoadingLiveData().observe(this, isLoading -> {
            // TODO: Add ProgressBar in layout and update visibility here
        });

        // Observe error
        viewModel.getErrorLiveData().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                showError(error);
            }
        });

        // Observe suggestion state
        viewModel.getIsSuggestionLiveData().observe(this, isSuggestion -> {
            if (isSuggestion != null) {
                updateButtonVisibility(isSuggestion);
            }
        });

        // Observe confirm button state
        viewModel.getConfirmButtonEnabledLiveData().observe(this, enabled -> {
            if (enabled != null) {
                binding.btnConfirm.setEnabled(enabled);
                binding.btnConfirm.setAlpha(enabled ? 1.0f : 0.6f);
            }
        });

        // Observe update button state
        viewModel.getHasChangesLiveData().observe(this, hasChanges -> {
            if (hasChanges != null && !isSuggestionFood) {
                binding.btnUpdateFood.setVisibility(hasChanges ? View.VISIBLE : View.GONE);
            }
        });

        // Observe success message
        viewModel.getSuccessMessageLiveData().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                showSuccess(message);
            }
        });

        // Observe date time picker trigger
        viewModel.getShowDateTimePickerLiveData().observe(this, show -> {
            if (show != null && show) {
                showDateTimePicker();
            }
        });

        // Observe consume time changes
        viewModel.getConsumeTimeLiveData().observe(this, consumeTime -> {
            if (consumeTime != null) {
                updateConsumeTimeUI(consumeTime);
            }
        });
    }

    private void updateButtonVisibility(boolean isSuggestion) {
        if (isSuggestion) {
            binding.btnConfirm.setVisibility(View.VISIBLE);
            binding.btnDeleteFood.setVisibility(View.GONE);
            binding.btnUpdateFood.setVisibility(View.GONE);
            binding.btnConfirm.setText("Thêm vào thực đơn");
        } else {
            binding.btnConfirm.setVisibility(View.GONE);
            binding.btnDeleteFood.setVisibility(View.VISIBLE);
            binding.btnUpdateFood.setVisibility(View.GONE); // Will be shown when there are changes
        }
    }

    private void setupClickListeners() {
        // Time consume food - Always clickable
        binding.cvConsumeFood.setOnClickListener(v -> viewModel.onTimePress());

        // Back button
        binding.btnBack.setOnClickListener(v -> onBackPressed());

        // Confirm button
        binding.btnConfirm.setOnClickListener(v -> viewModel.confirmAction());

        // Delete button
        binding.btnDeleteFood.setOnClickListener(v -> showDeleteConfirmDialog());

        // Update button
        binding.btnUpdateFood.setOnClickListener(v -> viewModel.updateFoodInMenu());
    }

    private void showDateTimePicker() {
        Calendar calendar = Calendar.getInstance();

        // Parse current consume time if available
        String currentConsumeTime = viewModel.getConsumeTimeLiveData().getValue();
        if (currentConsumeTime != null && !currentConsumeTime.isEmpty()) {
            LocalDateTime dateTime = DateUtils.parseIsoDateTime(currentConsumeTime);
            if (dateTime != null) {
                calendar.set(Calendar.YEAR, dateTime.getYear());
                calendar.set(Calendar.MONTH, dateTime.getMonthValue() - 1);
                calendar.set(Calendar.DAY_OF_MONTH, dateTime.getDayOfMonth());
                calendar.set(Calendar.HOUR_OF_DAY, dateTime.getHour());
                calendar.set(Calendar.MINUTE, dateTime.getMinute());
            }
        }

        // Show date picker first
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (datePicker, year, month, dayOfMonth) -> {
                    // After date is selected, show time picker
                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            this,
                            (timePicker, hourOfDay, minute) -> {
                                // Both date and time selected
                                viewModel.onDateTimeSelected(year, month, dayOfMonth, hourOfDay, minute);
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                    );
                    timePickerDialog.setTitle("Chọn giờ tiêu thụ");
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.setTitle("Chọn ngày tiêu thụ");
        datePickerDialog.show();
    }

    private void updateConsumeTimeUI(String consumeTime) {
        if (consumeTime != null && !consumeTime.isEmpty()) {
            LocalDateTime dateTime = DateUtils.parseIsoDateTimeWithoutTimezoneConvert(consumeTime);
            if (dateTime != null) {
                // Format ngày
                String dateStr = dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                // Format giờ
                String timeStr = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));

                // Gán vào TextView
                binding.tvDateConsumeFood.setText(dateStr);
                binding.tvTimeConsumeFood.setText(timeStr);
            } else {
                binding.tvDateConsumeFood.setText("N/A");
                binding.tvTimeConsumeFood.setText("N/A");
            }
        } else {
            binding.tvDateConsumeFood.setText("N/A");
            binding.tvTimeConsumeFood.setText("N/A");
        }
    }

    @SuppressLint("SetTextI18n")
    private void updateFoodUI(FoodDataResponseDto food) {
        // Update basic info
        binding.tvRecipeTitle.setText(food.getName());
        binding.tvRecipeCategory.setText(food.getDescription());

        // Update time info
        int totalTime = food.getPreparationTimeMinutes() + food.getCookingTimeMinutes();
        binding.tvCookTime.setText(totalTime + " phút");

        // Update calories
        binding.tvCalories.setText(food.getCalories().intValue() + " kcal");

        // Update difficulty
        int difficulty = (food.getDifficultyLevel() > 0 && food.getDifficultyLevel() <= 5) ? food.getDifficultyLevel() : 1;
        String stars = "★★★★★".substring(0, Math.min(Math.max(difficulty, 1), 5)) +
                "☆☆☆☆☆".substring(0, Math.max(5 - difficulty, 0));
        binding.tvDifficulty.setText(stars);

        // Update nutrition info
        binding.tvProtein.setText(food.getProtein().intValue() + "g");
        binding.tvCarbs.setText(food.getCarbohydrates().intValue() + "g");
        binding.tvFat.setText(food.getFat().intValue() + "g");
        binding.tvFiber.setText(food.getFiber().intValue() + "g");

        // Update consume time UI
        updateConsumeTimeUI(food.getConsumedAt());

        // Load recipe image
        loadRecipeImage(food.getImageUrl());
    }

    private void loadRecipeImage(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.sample_healthy_dish)
                .error(R.drawable.sample_healthy_dish)
                .into(binding.ivRecipeImage);
    }

    private void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    private void showSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        // Return result to previous activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("success", true);
        resultIntent.putExtra("message", message);
        setResult(RESULT_OK, resultIntent);

        // Finish after delay
        new Handler(Looper.getMainLooper()).postDelayed(this::finish, 1500);
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa món ăn")
                .setMessage("Bạn có chắc chắn muốn xóa món ăn này khỏi thực đơn?")
                .setPositiveButton("Xóa", (dialog, which) -> viewModel.deleteFood())
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        Boolean hasChanges = viewModel.getHasChangesLiveData().getValue();
        if (hasChanges != null && hasChanges && !isSuggestionFood) {
            showUnsavedChangesDialog();
        } else {
            super.onBackPressed();
        }
    }

    private void showUnsavedChangesDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Thay đổi chưa lưu")
                .setMessage("Bạn có thay đổi chưa được lưu. Bạn có muốn lưu trước khi thoát?")
                .setPositiveButton("Lưu", (dialog, which) -> viewModel.updateFoodInMenu())
                .setNegativeButton("Không lưu", (dialog, which) -> finish())
                .setNeutralButton("Hủy", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewModel != null) {
            viewModel.getFoodLiveData().removeObservers(this);
            viewModel.getIngredientsLiveData().removeObservers(this);
            viewModel.getInstructionsLiveData().removeObservers(this);
            viewModel.getTipsLiveData().removeObservers(this);
            viewModel.getIsLoadingLiveData().removeObservers(this);
            viewModel.getErrorLiveData().removeObservers(this);
            viewModel.getIsSuggestionLiveData().removeObservers(this);
            viewModel.getConfirmButtonEnabledLiveData().removeObservers(this);
            viewModel.getSuccessMessageLiveData().removeObservers(this);
            viewModel.getHasChangesLiveData().removeObservers(this);
            viewModel.getShowDateTimePickerLiveData().removeObservers(this);
            viewModel.getConsumeTimeLiveData().removeObservers(this);
        }
        binding = null;
    }
}