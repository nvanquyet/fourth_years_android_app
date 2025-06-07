package com.example.android_exam.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_exam.R;
import com.example.android_exam.adapters.Ingredient.CategoryAdapter;
import com.example.android_exam.adapters.Ingredient.ExpiryFilterAdapter;
import com.example.android_exam.adapters.Ingredient.IngredientAdapter;
import com.example.android_exam.data.local.entity.Ingredient;
import com.example.android_exam.viewmodels.IngredientViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class IngredientsActivity extends AppCompatActivity implements IngredientAdapter.OnIngredientClickListener {

    private RecyclerView rvIngredients, rvCategories, rvExpiryFilters;
    private TextInputEditText etSearch;
    private FloatingActionButton fabMain, fabCamera, fabManual;
    private LinearLayout llEmptyState;
    private ProgressBar progressLoading;
    private TextView tvTotalItems, tvExpiringSoon, tvExpired;
    private IngredientAdapter ingredientAdapter;
    private CategoryAdapter categoryAdapter;
    private ExpiryFilterAdapter expiryFilterAdapter;
    private IngredientViewModel viewModel;
    private static final int CAMERA_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        initViews();
        initViewModel();
        setupRecyclerViews();
        setupListeners();
        observeData();
    }

    private void initViews() {
        rvIngredients = findViewById(R.id.rv_ingredients);
        rvCategories = findViewById(R.id.rv_categories);
        rvExpiryFilters = findViewById(R.id.rv_expiry_filters);
        etSearch = findViewById(R.id.et_search);
        fabMain = findViewById(R.id.fab_add_ingredient);
        fabCamera = findViewById(R.id.fab_camera);
        fabManual = findViewById(R.id.fab_manual);
        llEmptyState = findViewById(R.id.ll_empty_state);
        progressLoading = findViewById(R.id.progress_loading);
        tvTotalItems = findViewById(R.id.tv_total_items);
        tvExpiringSoon = findViewById(R.id.tv_expiring_soon);
        tvExpired = findViewById(R.id.tv_expired);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(IngredientViewModel.class);
    }

    private void setupRecyclerViews() {
        // Ingredients RecyclerView
        ingredientAdapter = new IngredientAdapter(new ArrayList<>(), this);
        rvIngredients.setLayoutManager(new LinearLayoutManager(this));
        rvIngredients.setAdapter(ingredientAdapter);

        // Categories RecyclerView
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), category -> viewModel.setSelectedCategory(category));
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);

        // Expiry Filters RecyclerView
        expiryFilterAdapter = new ExpiryFilterAdapter(new ArrayList<>(), filter -> viewModel.setSelectedExpiryFilter(filter));
        rvExpiryFilters.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvExpiryFilters.setAdapter(expiryFilterAdapter);
    }

    private void setupListeners() {
        // Search functionality
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSearchQuery(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // FAB animations
        fabMain.setOnClickListener(v -> toggleFab());
        fabCamera.setOnClickListener(v -> openCamera());
        fabManual.setOnClickListener(v -> showAddIngredientDialog(null, false));

        // Sort chips
        findViewById(R.id.chip_sort_name).setOnClickListener(v -> viewModel.setSortMode("name"));
        findViewById(R.id.chip_sort_expiry).setOnClickListener(v -> viewModel.setSortMode("expiry"));
        findViewById(R.id.chip_sort_quantity).setOnClickListener(v -> viewModel.setSortMode("quantity"));
        findViewById(R.id.chip_sort_category).setOnClickListener(v -> viewModel.setSortMode("category"));
    }

    private void observeData() {
        // Observe filtered ingredients
        viewModel.getFilteredIngredients().observe(this, ingredients -> {
            ingredientAdapter = new IngredientAdapter(ingredients, this);
            rvIngredients.setAdapter(ingredientAdapter);
            if (ingredients != null) {
                updateEmptyState(ingredients.isEmpty());
            }

        });

        // Observe categories
        viewModel.getAllCategories().observe(this, categories -> categoryAdapter = new CategoryAdapter(categories, category -> viewModel.setSelectedCategory(category)));

        // Observe expiry filters (static, can be hardcoded or from resources)
        viewModel.getExpiryFilters().observe(this, filters -> expiryFilterAdapter = new ExpiryFilterAdapter(filters, filter -> viewModel.setSelectedExpiryFilter(filter)));

        // Observe statistics
        viewModel.getStatistics().observe(this, stats -> {
            tvTotalItems.setText(String.valueOf(stats.totalItems));
            tvExpiringSoon.setText(String.valueOf(stats.expiringSoon));
            tvExpired.setText(String.valueOf(stats.expired));
        });

        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> progressLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        // Observe error messages
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe FAB menu state
        viewModel.getFabMenuOpen().observe(this, isOpen -> {
            if (isOpen) openFabMenu();
            else closeFabMenu();
        });
    }

    private void toggleFab() {
        viewModel.toggleFabMenu();
    }

    private void openFabMenu() {
        fabCamera.setVisibility(View.VISIBLE);
        fabManual.setVisibility(View.VISIBLE);
        Animation slideInCamera = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        Animation slideInManual = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        slideInManual.setStartOffset(50);
        fabCamera.startAnimation(slideInCamera);
        fabManual.startAnimation(slideInManual);
        fabMain.animate().rotation(45f).setDuration(300);
    }

    private void closeFabMenu() {
        Animation slideOutCamera = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
        Animation slideOutManual = AnimationUtils.loadAnimation(this, R.anim.slide_out_right);
        slideOutCamera.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) { fabCamera.setVisibility(View.GONE); }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        slideOutManual.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) { fabManual.setVisibility(View.GONE); }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        fabCamera.startAnimation(slideOutCamera);
        fabManual.startAnimation(slideOutManual);
        fabMain.animate().rotation(0f).setDuration(300);
    }

    private void openCamera() {
        viewModel.closeFabMenu();
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        } else {
            Toast.makeText(this, "Camera không khả dụng", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddIngredientDialog(Ingredient ingredient, boolean isEdit) {
        viewModel.closeFabMenu();
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_ingredient);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView tvTitle = dialog.findViewById(R.id.tv_dialog_title);
        EditText etName = dialog.findViewById(R.id.et_ingredient_name);
        EditText etQuantity = dialog.findViewById(R.id.et_ingredient_quantity);
        Spinner spinnerUnit = dialog.findViewById(R.id.spinner_unit);
        EditText etExpiryDate = dialog.findViewById(R.id.et_expiry_date);
        Spinner spinnerCategory = dialog.findViewById(R.id.spinner_category);
        MaterialButton btnSave = dialog.findViewById(R.id.btn_save);
        MaterialButton btnCancel = dialog.findViewById(R.id.btn_cancel);

        // Setup spinners
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.measurement_units));
        spinnerUnit.setAdapter(unitAdapter);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.ingredient_categories));
        spinnerCategory.setAdapter(categoryAdapter);

        // Pre-fill for edit mode
        if (isEdit && ingredient != null) {
            tvTitle.setText("Chỉnh sửa nguyên liệu");
            etName.setText(ingredient.getName());
            etQuantity.setText(String.valueOf(ingredient.getQuantity()));
            etExpiryDate.setText(ingredient.getExpiryDate());
            int unitPosition = unitAdapter.getPosition(ingredient.getUnit());
            if (unitPosition >= 0) spinnerUnit.setSelection(unitPosition);
            int categoryPosition = categoryAdapter.getPosition(ingredient.getCategory());
            if (categoryPosition >= 0) spinnerCategory.setSelection(categoryPosition);
        } else {
            tvTitle.setText("Thêm nguyên liệu mới");
        }

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String quantityStr = etQuantity.getText().toString().trim();
            String unit = spinnerUnit.getSelectedItem().toString();
            String expiryDate = etExpiryDate.getText().toString().trim();
            String category = spinnerCategory.getSelectedItem().toString();

            Ingredient newIngredient = viewModel.createIngredient(name, quantityStr, unit, expiryDate, category);
            if (newIngredient != null) {
                if (isEdit && ingredient != null) {
                    newIngredient.id = ingredient.id; // Keep same ID for update
                    viewModel.updateIngredient(newIngredient);
                } else {
                    viewModel.addIngredient(newIngredient);
                }
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void updateEmptyState(boolean isEmpty) {
        llEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        rvIngredients.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onIngredientClick(Ingredient ingredient) {
        showAddIngredientDialog(ingredient, true);
    }

    @Override
    public void onIngredientLongClick(Ingredient ingredient) {
        Toast.makeText(this, "Xóa " + ingredient.getName() + "?", Toast.LENGTH_SHORT).show();
        viewModel.deleteIngredient(ingredient.getId());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Toast.makeText(this, "Ảnh đã được chụp! Đang xử lý...", Toast.LENGTH_SHORT).show();
            // TODO: Xử lý ảnh để trích xuất thông tin nguyên liệu (có thể dùng ML Kit hoặc API OCR)
        }
    }

    @Override
    public void onBackPressed() {
        if (viewModel.getFabMenuOpen().getValue() != null && viewModel.getFabMenuOpen().getValue()) {
            viewModel.closeFabMenu();
        } else {
            super.onBackPressed();
        }
    }
}