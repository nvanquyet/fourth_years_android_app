package com.example.android_exam.activities;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.se.omapi.Session;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.example.android_exam.R;
import com.example.android_exam.data.api.ApiManager;
import com.example.android_exam.data.api.AuthCallback;
import com.example.android_exam.data.dto.response.ApiResponse;
import com.example.android_exam.data.dto.user.ChangePasswordDto;
import com.example.android_exam.data.dto.user.UserProfileDto;
import com.example.android_exam.data.models.base.User;
import com.example.android_exam.data.models.enums.ActivityLevel;
import com.example.android_exam.data.models.enums.Gender;
import com.example.android_exam.data.models.enums.NutritionGoal;
import com.example.android_exam.utils.DateUtils;
import com.example.android_exam.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    // UI Components
    private NestedScrollView scrollView;
    private CardView personalInfoCard, healthInfoCard, preferencesCard, securityCard;
    private TextInputLayout tilFirstName, tilLastName, tilEmail, tilUsername;
    private TextInputLayout tilHeight, tilWeight, tilTargetWeight;
    private TextInputLayout tilFoodAllergies, tilFoodPreferences;
    private TextInputEditText etFirstName, etLastName, etEmail, etUsername;
    private TextInputEditText etHeight, etWeight, etTargetWeight;
    private TextInputEditText etFoodAllergies, etFoodPreferences;
    private TextView tvDateOfBirth, tvBMI, tvBMR, tvTDEE;
    private Button btnDatePicker, btnChangePassword, btnSave;
    private Spinner spinnerGender, spinnerActivityLevel, spinnerNutritionGoal;
    private CheckBox cbHasFoodAllergies, cbEnableNotifications, cbEnableMealReminders;
    private FloatingActionButton fabSave;

    private User currentUser;
    private boolean isDataChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setupToolbar();
        setupSpinners();
        setupListeners();
        loadUserData();
    }

    private void initViews() {
        // ScrollView
        scrollView = findViewById(R.id.scroll_view);

        // Cards
        personalInfoCard = findViewById(R.id.card_personal_info);
        healthInfoCard = findViewById(R.id.card_health_info);
        preferencesCard = findViewById(R.id.card_preferences);
        //securityCard = findViewById(R.id.card_security);

        // Personal Info
        tilFirstName = findViewById(R.id.til_first_name);
        tilLastName = findViewById(R.id.til_last_name);
        tilEmail = findViewById(R.id.til_email);
        tilUsername = findViewById(R.id.til_username);
        etFirstName = findViewById(R.id.et_first_name);
        etLastName = findViewById(R.id.et_last_name);
        etEmail = findViewById(R.id.et_email);
        etUsername = findViewById(R.id.et_username);
        tvDateOfBirth = findViewById(R.id.tv_date_of_birth);
        btnDatePicker = findViewById(R.id.btn_date_picker);
        spinnerGender = findViewById(R.id.spinner_gender);

        // Health Info
        tilHeight = findViewById(R.id.til_height);
        tilWeight = findViewById(R.id.til_weight);
        tilTargetWeight = findViewById(R.id.til_target_weight);
        etHeight = findViewById(R.id.et_height);
        etWeight = findViewById(R.id.et_weight);
        etTargetWeight = findViewById(R.id.et_target_weight);
        tvBMI = findViewById(R.id.tv_bmi);
        tvBMR = findViewById(R.id.tv_bmr);
        tvTDEE = findViewById(R.id.tv_tdee);
        spinnerActivityLevel = findViewById(R.id.spinner_activity_level);
        spinnerNutritionGoal = findViewById(R.id.spinner_nutrition_goal);

        // Preferences
        cbHasFoodAllergies = findViewById(R.id.cb_has_food_allergies);
        tilFoodAllergies = findViewById(R.id.til_food_allergies);
        etFoodAllergies = findViewById(R.id.et_food_allergies);
        tilFoodPreferences = findViewById(R.id.til_food_preferences);
        etFoodPreferences = findViewById(R.id.et_food_preferences);
        cbEnableNotifications = findViewById(R.id.cb_enable_notifications);
        cbEnableMealReminders = findViewById(R.id.cb_enable_meal_reminders);

        // Security
        btnChangePassword = findViewById(R.id.btn_change_password);

        // Action buttons
        btnSave = findViewById(R.id.btn_save);
        fabSave = findViewById(R.id.fab_save);
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Hồ sơ cá nhân");
        }
    }

    private void setupSpinners() {
        // Gender Spinner
        String[] genders = {"Nam", "Nữ"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);

        // Activity Level Spinner
        String[] activityLevels = {
                "Ít vận động", "Vận động nhẹ", "Vận động vừa phải",
                "Vận động nhiều", "Vận động rất nhiều"
        };
        ArrayAdapter<String> activityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, activityLevels);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerActivityLevel.setAdapter(activityAdapter);

        // Nutrition Goal Spinner
        String[] nutritionGoals = {
                "Cân bằng", "Giảm cân", "Tăng cân", "Tăng cơ", "Cắt giảm"
        };
        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, nutritionGoals);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNutritionGoal.setAdapter(goalAdapter);
    }

    private void setupListeners() {
        // Date picker
        btnDatePicker.setOnClickListener(v -> showDatePicker());

        // Food allergies checkbox
        cbHasFoodAllergies.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tilFoodAllergies.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isChecked) {
                etFoodAllergies.setText("");
            }
            markDataChanged();
        });

        // Change password button
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        // Save buttons
        btnSave.setOnClickListener(v -> saveUserData());
        fabSave.setOnClickListener(v -> saveUserData());

        // Text change listeners
        setupTextChangeListeners();

        // Weight/Height change listeners for BMI calculation
        etWeight.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) calculateAndDisplayMetrics();
        });
        etHeight.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) calculateAndDisplayMetrics();
        });
    }

    private void setupTextChangeListeners() {
        View.OnFocusChangeListener focusChangeListener = (v, hasFocus) -> {
            if (!hasFocus) markDataChanged();
        };

        etFirstName.setOnFocusChangeListener(focusChangeListener);
        etLastName.setOnFocusChangeListener(focusChangeListener);
        etEmail.setOnFocusChangeListener(focusChangeListener);
        etUsername.setOnFocusChangeListener(focusChangeListener);
        etHeight.setOnFocusChangeListener(focusChangeListener);
        etWeight.setOnFocusChangeListener(focusChangeListener);
        etTargetWeight.setOnFocusChangeListener(focusChangeListener);
        etFoodAllergies.setOnFocusChangeListener(focusChangeListener);
        etFoodPreferences.setOnFocusChangeListener(focusChangeListener);
    }

    private void loadUserData() {
        // Load from cache first
        SessionManager.getUser(new SessionManager.UserCallback() {
            @Override
            public void onUserLoaded(User user) {
                runOnUiThread(() -> {
                    currentUser = user;
                    populateFields();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.e(TAG, "Error loading user from cache: " + error);
                });
            }
        });
    }


    private void populateFields() {
        if (currentUser == null) return;

        // Personal Info
        etFirstName.setText(currentUser.getFirstName());
        etLastName.setText(currentUser.getLastName());
        etEmail.setText(currentUser.getEmail());
        etUsername.setText(currentUser.getUsername());

        if (currentUser.getDateOfBirth() != null) {
            tvDateOfBirth.setText(DateUtils.formatDate(currentUser.getDateOfBirth()));
        }

        if (currentUser.getGender() != null) {
            spinnerGender.setSelection(currentUser.getGender() == Gender.MALE ? 0 : 1);
        }

        // Health Info
        if (currentUser.getHeight() != null) {
            etHeight.setText(String.valueOf(currentUser.getHeight()));
        }
        if (currentUser.getWeight() != null) {
            etWeight.setText(String.valueOf(currentUser.getWeight()));
        }
        if (currentUser.getTargetWeight() != null) {
            etTargetWeight.setText(String.valueOf(currentUser.getTargetWeight()));
        }

        if (currentUser.getActivityLevel() != null) {
            spinnerActivityLevel.setSelection(currentUser.getActivityLevel().ordinal());
        }

        if (currentUser.getPrimaryNutritionGoal() != null) {
            spinnerNutritionGoal.setSelection(currentUser.getPrimaryNutritionGoal().ordinal());
        }

        // Preferences
        cbHasFoodAllergies.setChecked(currentUser.getHasFoodAllergies());
        tilFoodAllergies.setVisibility(currentUser.getHasFoodAllergies() ? View.VISIBLE : View.GONE);
        etFoodAllergies.setText(currentUser.getFoodAllergies());
        etFoodPreferences.setText(currentUser.getFoodPreferences());
        cbEnableNotifications.setChecked(currentUser.getEnableNotifications());
        cbEnableMealReminders.setChecked(currentUser.getEnableMealReminders());

        calculateAndDisplayMetrics();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        if (currentUser != null && currentUser.getDateOfBirth() != null) {
            Date dob = currentUser.getDateOfBirth();

            // Convert Date -> LocalDate
            Instant instant = dob.toInstant();
            ZoneId zoneId = ZoneId.systemDefault();
            LocalDate birthDate = instant.atZone(zoneId).toLocalDate();

            calendar.set(birthDate.getYear(), birthDate.getMonthValue() - 1, birthDate.getDayOfMonth());
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                    tvDateOfBirth.setText(selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    if (currentUser != null) {
                        // Convert LocalDate -> Date
                        Instant instant = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
                        Date selectedDateAsDate = Date.from(instant);
                        // Update user's date of birth
                        currentUser.setDateOfBirth(selectedDateAsDate);
                    }
                    calculateAndDisplayMetrics();
                    markDataChanged();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set max date to today
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void calculateAndDisplayMetrics() {
        if (currentUser == null) return;

        // Update user data from fields
        try {
            if (!TextUtils.isEmpty(etHeight.getText())) {
                currentUser.setHeight(Double.parseDouble(etHeight.getText().toString()));
            }
            if (!TextUtils.isEmpty(etWeight.getText())) {
                currentUser.setWeight(Double.parseDouble(etWeight.getText().toString()));
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing numeric values", e);
            return;
        }

        // Calculate and display metrics
        Double bmi = currentUser.getBMI();
        Double bmr = currentUser.getBMR();
        Double tdee = currentUser.getTDEE();

        tvBMI.setText(bmi != null ? String.format("BMI: %.1f", bmi) : "BMI: --");
        tvBMR.setText(bmr != null ? String.format("BMR: %.0f kcal", bmr) : "BMR: --");
        tvTDEE.setText(tdee != null ? String.format("TDEE: %.0f kcal", tdee) : "TDEE: --");

        // Color code BMI
        if (bmi != null) {
            int color;
            if (bmi < 18.5) {
                color = ContextCompat.getColor(this, android.R.color.holo_blue_dark);
            } else if (bmi <= 24.9) {
                color = ContextCompat.getColor(this, android.R.color.holo_green_dark);
            } else if (bmi <= 29.9) {
                color = ContextCompat.getColor(this, android.R.color.holo_orange_dark);
            } else {
                color = ContextCompat.getColor(this, android.R.color.holo_red_dark);
            }
            tvBMI.setTextColor(color);
        }
    }

    private void showChangePasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);

        TextInputLayout tilCurrentPassword = dialogView.findViewById(R.id.til_current_password);
        TextInputLayout tilNewPassword = dialogView.findViewById(R.id.til_new_password);
        TextInputLayout tilConfirmPassword = dialogView.findViewById(R.id.til_confirm_password);

        TextInputEditText etCurrentPassword = dialogView.findViewById(R.id.et_current_password);
        TextInputEditText etNewPassword = dialogView.findViewById(R.id.et_new_password);
        TextInputEditText etConfirmPassword = dialogView.findViewById(R.id.et_confirm_password);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Đổi mật khẩu")
                .setView(dialogView)
                .setPositiveButton("Đổi mật khẩu", null)
                .setNegativeButton("Hủy", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                String currentPassword = etCurrentPassword.getText().toString().trim();
                String newPassword = etNewPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();

                // Validate input
                if (TextUtils.isEmpty(currentPassword)) {
                    tilCurrentPassword.setError("Vui lòng nhập mật khẩu hiện tại");
                    return;
                }
                if (TextUtils.isEmpty(newPassword)) {
                    tilNewPassword.setError("Vui lòng nhập mật khẩu mới");
                    return;
                }
                if (newPassword.length() < 6) {
                    tilNewPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
                    return;
                }
                if (!newPassword.equals(confirmPassword)) {
                    tilConfirmPassword.setError("Mật khẩu xác nhận không khớp");
                    return;
                }

                // Clear errors
                tilCurrentPassword.setError(null);
                tilNewPassword.setError(null);
                tilConfirmPassword.setError(null);

                // TODO: Implement password change logic
                changePassword(currentPassword, newPassword, confirmPassword, dialog);
            });
        });

        dialog.show();
    }

    private void changePassword(String currentPassword, String newPassword, String confirmPassword, AlertDialog dialog) {
        // TODO: Implement actual password change with repository
        //Call api to change password
        var changePasswordDto = new ChangePasswordDto();
        changePasswordDto.setCurrentPassword(currentPassword);
        changePasswordDto.setNewPassword(newPassword);
        changePasswordDto.setConfirmNewPassword(confirmPassword);
        ApiManager.getInstance().getAuthClient().changePassword(changePasswordDto, new AuthCallback<ApiResponse<Object>>() {
            @Override
            public void onSuccess(ApiResponse<Object> result) {
                runOnUiThread(() -> {
                    Toast.makeText(ProfileActivity.this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(ProfileActivity.this, "Lỗi đổi mật khẩu: " + error, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error changing password: " + error);
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                runOnUiThread(() -> {
                    Toast.makeText(ProfileActivity.this, "Lỗi kết nối: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error changing password", throwable);
                });
            }
        });
    }

    private void markDataChanged() {
        isDataChanged = true;
        fabSave.show();
    }

    private void saveUserData() {
        if (currentUser == null) return;

        // Validate and update user data
        if (!validateInput()) return;

        updateUserFromFields();
        Log.d("ProfileActivity", "Saving user data: " + UserProfileDto.fromUser(currentUser).toJson());
        // TODO: Save to repository
        ApiManager.getInstance().getAuthClient().updateUserProfile(UserProfileDto.fromUser(currentUser), new AuthCallback<ApiResponse<UserProfileDto>>() {
            @Override
            public void onSuccess(ApiResponse<UserProfileDto> result) {
                runOnUiThread(() -> {
                    // ⭐ QUAN TRỌNG: Update cache với data mới
                    if (result.getData() != null) {
                        User updatedUser = result.getData().toUser();
                        SessionManager.saveUser(updatedUser); // Cập nhật cache
                    }

                    Toast.makeText(ProfileActivity.this,
                            "Cập nhật hồ sơ thành công!", Toast.LENGTH_SHORT).show();
                    isDataChanged = false;
                    fabSave.hide();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(ProfileActivity.this,
                            "Lỗi cập nhật hồ sơ: " + error, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error saving user profile: " + error);
                });
            }

            @Override
            public void onFailure(Throwable throwable) {
                runOnUiThread(() -> {
                    Toast.makeText(ProfileActivity.this,
                            "Lỗi kết nối: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error saving user profile", throwable);
                });
            }
        });
    }

    private boolean validateInput() {
        boolean isValid = true;

        // Clear previous errors
        tilFirstName.setError(null);
        tilLastName.setError(null);
        tilEmail.setError(null);
        tilUsername.setError(null);
        tilHeight.setError(null);
        tilWeight.setError(null);

        // Validate required fields
        if (TextUtils.isEmpty(etFirstName.getText())) {
            tilFirstName.setError("Vui lòng nhập họ");
            isValid = false;
        }

        if (TextUtils.isEmpty(etLastName.getText())) {
            tilLastName.setError("Vui lòng nhập tên");
            isValid = false;
        }

        if (TextUtils.isEmpty(etEmail.getText())) {
            tilEmail.setError("Vui lòng nhập email");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText()).matches()) {
            tilEmail.setError("Email không hợp lệ");
            isValid = false;
        }

        if (TextUtils.isEmpty(etUsername.getText())) {
            tilUsername.setError("Vui lòng nhập tên đăng nhập");
            isValid = false;
        }

        // Validate numeric fields
        try {
            if (!TextUtils.isEmpty(etHeight.getText())) {
                double height = Double.parseDouble(etHeight.getText().toString());
                if (height <= 0 || height > 300) {
                    tilHeight.setError("Chiều cao không hợp lệ (1-300 cm)");
                    isValid = false;
                }
            }

            if (!TextUtils.isEmpty(etWeight.getText())) {
                double weight = Double.parseDouble(etWeight.getText().toString());
                if (weight <= 0 || weight > 500) {
                    tilWeight.setError("Cân nặng không hợp lệ (1-500 kg)");
                    isValid = false;
                }
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }

    private void updateUserFromFields() {
        // Personal info
        currentUser.setFirstName(etFirstName.getText().toString().trim());
        currentUser.setLastName(etLastName.getText().toString().trim());
        currentUser.setEmail(etEmail.getText().toString().trim());
        currentUser.setUsername(etUsername.getText().toString().trim());

        // Gender
        currentUser.setGender(spinnerGender.getSelectedItemPosition() == 0 ? Gender.MALE : Gender.FEMALE);

        // Health info
        try {
            if (!TextUtils.isEmpty(etHeight.getText())) {
                currentUser.setHeight(Double.parseDouble(etHeight.getText().toString()));
            }
            if (!TextUtils.isEmpty(etWeight.getText())) {
                currentUser.setWeight(Double.parseDouble(etWeight.getText().toString()));
            }
            if (!TextUtils.isEmpty(etTargetWeight.getText())) {
                currentUser.setTargetWeight(Double.parseDouble(etTargetWeight.getText().toString()));
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing numeric values", e);
        }

        // Activity level and nutrition goal
        currentUser.setActivityLevel(ActivityLevel.values()[spinnerActivityLevel.getSelectedItemPosition()]);
        currentUser.setPrimaryNutritionGoal(NutritionGoal.values()[spinnerNutritionGoal.getSelectedItemPosition()]);

        // Preferences
        currentUser.setHasFoodAllergies(cbHasFoodAllergies.isChecked());
        currentUser.setFoodAllergies(cbHasFoodAllergies.isChecked() ?
                etFoodAllergies.getText().toString().trim() : "");
        currentUser.setFoodPreferences(etFoodPreferences.getText().toString().trim());
        currentUser.setEnableNotifications(cbEnableNotifications.isChecked());
        currentUser.setEnableMealReminders(cbEnableMealReminders.isChecked());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isDataChanged) {
            new AlertDialog.Builder(this)
                    .setTitle("Thay đổi chưa được lưu")
                    .setMessage("Bạn có muốn lưu các thay đổi trước khi thoát không?")
                    .setPositiveButton("Lưu", (dialog, which) -> saveUserData())
                    .setNegativeButton("Không lưu", (dialog, which) -> super.onBackPressed())
                    .setNeutralButton("Hủy", null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }
}