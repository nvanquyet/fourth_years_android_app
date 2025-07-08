package com.example.android_exam.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.android_exam.activities.dialog.IngredientDialog;
import com.example.android_exam.adapters.IngredientAdapter;
import com.example.android_exam.data.dto.ingredient.IngredientDataResponseDto;
import com.example.android_exam.data.dto.ingredient.IngredientSearchResultDto;
import com.example.android_exam.data.models.base.Ingredient;
import com.example.android_exam.data.models.enums.IngredientCategory;
import com.example.android_exam.data.models.enums.IngredientUnit;
import com.example.android_exam.databinding.ActivityIngredientManagementBinding;
import com.example.android_exam.databinding.DialogIngredientDetailBinding;
import com.example.android_exam.module.image.ImagePickerHelper;
import com.example.android_exam.utils.IngredientUtils;
import com.example.android_exam.viewmodels.IngredientViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import android.util.Log;

public class IngredientManagementActivity extends AppCompatActivity implements IngredientAdapter.OnIngredientActionListener, IngredientDialog.OnIngredientDialogListener {

    private ActivityIngredientManagementBinding binding;
    private IngredientAdapter adapter;
    private IngredientViewModel viewModel;
    private IngredientDialog ingredientDialog;
    private AlertDialog currentDialog;
    private final Map<Integer, IngredientCategory> chipIdToCategoryMap = new HashMap<>();

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private String currentSortBy = "expiryDate";
    private String currentSortDirection = "asc";
    private String  currentPhotoPath = "asc";
    private int chipAllCategoriesId = -1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ImagePickerHelper.handlePermissionResult(this, requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIngredientManagementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (!ImagePickerHelper.isInitialized(this)) {
            ImagePickerHelper.initialize(this);
        }

        initializeComponents();
        initializeDialog();



        Intent intent = getIntent();
        IngredientSearchResultDto ingredients = (IngredientSearchResultDto) intent.getSerializableExtra("ingredients");
        loadInitialData(ingredients);
        var openDialog = intent.getBooleanExtra("openDialog", false);
        var detectMode = intent.getBooleanExtra("detectMode", false);
        if (openDialog && detectMode) {
            showIngredientDialog(null);
        }
    }

    private void initializeDialog() {
        ingredientDialog = new IngredientDialog(this, this);
    }

    private void initializeComponents() {
        initAdapter();
        setupViewModel();
        setupListeners();
        setupChips();
        setupSortButton();
    }

    private void initAdapter() {
        adapter = new IngredientAdapter(this);
        adapter.setOnIngredientActionListener(this);

        binding.recyclerViewIngredients.setHasFixedSize(true);
        binding.recyclerViewIngredients.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewIngredients.setAdapter(adapter);
        binding.recyclerViewIngredients.addItemDecoration(
                new DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        );
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(IngredientViewModel.class);
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getIngredientsLiveData().observe(this, this::updateIngredientsDisplay);
        viewModel.getLoadingLiveData().observe(this, this::updateLoadingState);
        viewModel.getErrorLiveData().observe(this, this::handleError);
        viewModel.getSuccessMessageLiveData().observe(this, this::handleSuccessMessage);
        viewModel.getValidationErrorLiveData().observe(this, this::handleValidationErrors);
    }

    private void updateIngredientsDisplay(List<IngredientDataResponseDto> ingredients) {
        if (ingredients == null) return;

        adapter.submitList(null);
        adapter.submitList(new ArrayList<>(ingredients));

        binding.layoutEmptyState.setVisibility(ingredients.isEmpty() ? View.VISIBLE : View.GONE);
        updateExpiringBanner(ingredients);
        updateChipCounts();
    }

    private void updateLoadingState(Boolean isLoading) {
        if (isLoading == null) return;

        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.swipeRefreshLayout.setRefreshing(isLoading);
    }

    private void handleError(String error) {
        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            viewModel.clearError();
        }
    }

    private void handleSuccessMessage(String message) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            viewModel.clearSuccessMessage();
        }
    }

    private void handleValidationErrors(Map<String, String> errors) {
        if (errors != null && ingredientDialog != null) {
            ingredientDialog.showValidationErrors(errors);
            viewModel.clearValidationErrors();
        }
    }

    private void updateExpiringBanner(List<IngredientDataResponseDto> ingredients) {
        long expiringCount = ingredients.stream()
                .filter(IngredientDataResponseDto::isExpiringSoon)
                .count();

        binding.expiringBanner.setVisibility(expiringCount > 0 ? View.VISIBLE : View.GONE);
        if (expiringCount > 0) {
            TextView bannerText = binding.expiringBanner.findViewById(android.R.id.text1);
            if (bannerText != null) {
                bannerText.setText(String.format("Có %d nguyên liệu sắp hết hạn", expiringCount));
            }
        }
    }

    private void setupListeners() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.searchIngredients(s.toString());
            }
        });

        binding.swipeRefreshLayout.setOnRefreshListener(() -> viewModel.refresh());
        binding.fabAddIngredient.setOnClickListener(v -> showIngredientDialog(null));
        binding.btnAddFromEmptyState.setOnClickListener(v -> showIngredientDialog(null));
    }

    private void setupChips() {
        binding.chipGroupCategories.removeAllViews();
        chipIdToCategoryMap.clear();

        Chip chipAll = new Chip(this);
        chipAllCategoriesId = View.generateViewId();
        chipAll.setId(chipAllCategoriesId);
        chipAll.setText("Tất cả");
        chipAll.setCheckable(true);
        chipAll.setChecked(true);
        binding.chipGroupCategories.addView(chipAll);

        for (IngredientCategory category : IngredientCategory.values()) {
            if (category != IngredientCategory.OTHER) {
                Chip chip = new Chip(this);
                int chipId = View.generateViewId();
                chip.setId(chipId);
                chip.setText(IngredientUtils.getCategoryDisplayName(category));
                chip.setCheckable(true);
                binding.chipGroupCategories.addView(chip);
                chipIdToCategoryMap.put(chipId, category);
            }
        }

        binding.chipGroupCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                chipAll.setChecked(true);
                viewModel.clearCategoryFilter();
                Log.d("IngredientManagement", "Category filter: All");
                return;
            }

            if (checkedIds.contains(chipAll.getId())) {
                chipAll.setChecked(false);
            }

            int checkedId = checkedIds.get(0);
            IngredientCategory category = chipIdToCategoryMap.get(checkedId);
            if (category != null) {
                viewModel.filterByCategory(category);
                Log.d("IngredientManagement", "Category filter: " + IngredientUtils.getCategoryDisplayName(category));
            } else {
                viewModel.clearCategoryFilter();
                Log.d("IngredientManagement", "Category filter: Cleared");
            }
        });

        binding.chipGroupExpiry.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                binding.chipAllExpiry.setChecked(true);
                viewModel.clearExpiryFilter();
                Log.d("IngredientManagement", "Expiry filter: All");
                return;
            }

            if (checkedIds.contains(binding.chipAllExpiry.getId())) {
                binding.chipAllExpiry.setChecked(false);
            }

            int checkedId = checkedIds.get(0);
            if (checkedId == binding.chipExpired.getId()) {
                viewModel.filterByExpiry(true);
                Log.d("IngredientManagement", "Expiry filter: Expired");
            } else if (checkedId == binding.chipNotExpired.getId()) {
                viewModel.filterByExpiry(false);
                Log.d("IngredientManagement", "Expiry filter: Not Expired");
            } else {
                viewModel.clearExpiryFilter();
                Log.d("IngredientManagement", "Expiry filter: Cleared");
            }
        });
    }

    private void setupSortButton() {
        binding.btnSort.setOnClickListener(v -> showSortDialog());
    }

    private void showSortDialog() {
        String[][] sortOptions = {
                {"Tên (Name)", "name"},
                {"Số lượng (Quantity)", "quantity"},
                {"Ngày hết hạn (Expiry Date)", "expiryDate"},
                {"Danh mục (Category)", "category"},
                {"Ngày tạo (Created At)", "createdAt"}
        };

        String[][] directionOptions = {
                {"Tăng dần (Ascending)", "asc"},
                {"Giảm dần (Descending)", "desc"}
        };

        View dialogView = createSortDialogView();
        Spinner sortSpinner = dialogView.findViewById(android.R.id.text1);
        Spinner directionSpinner = dialogView.findViewById(android.R.id.text2);

        setupSortSpinners(sortSpinner, directionSpinner, sortOptions, directionOptions);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Sắp xếp")
                .setView(dialogView)
                .setPositiveButton("Áp dụng", (dialog, which) ->
                        applySortSettings(sortSpinner, directionSpinner, sortOptions, directionOptions))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private View createSortDialogView() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        TextView sortLabel = new TextView(this);
        sortLabel.setText("Tiêu chí sắp xếp:");
        sortLabel.setPadding(0, 0, 0, 16);
        layout.addView(sortLabel);

        Spinner sortSpinner = new Spinner(this);
        sortSpinner.setId(android.R.id.text1);
        LinearLayout.LayoutParams sortParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        sortParams.setMargins(0, 0, 0, 32);
        sortSpinner.setLayoutParams(sortParams);
        layout.addView(sortSpinner);

        TextView directionLabel = new TextView(this);
        directionLabel.setText("Hướng sắp xếp:");
        directionLabel.setPadding(0, 0, 0, 16);
        layout.addView(directionLabel);

        Spinner directionSpinner = new Spinner(this);
        directionSpinner.setId(android.R.id.text2);
        LinearLayout.LayoutParams directionParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        directionSpinner.setLayoutParams(directionParams);
        layout.addView(directionSpinner);

        return layout;
    }

    private void setupSortSpinners(Spinner sortSpinner, Spinner directionSpinner,
                                   String[][] sortOptions, String[][] directionOptions) {
        String[] sortDisplay = new String[sortOptions.length];
        String[] directionDisplay = new String[directionOptions.length];

        for (int i = 0; i < sortOptions.length; i++) {
            sortDisplay[i] = sortOptions[i][0];
        }
        for (int i = 0; i < directionOptions.length; i++) {
            directionDisplay[i] = directionOptions[i][0];
        }

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, sortDisplay);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);

        ArrayAdapter<String> directionAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, directionDisplay);
        directionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directionSpinner.setAdapter(directionAdapter);

        setCurrentSortValues(sortSpinner, directionSpinner, sortOptions, directionOptions);
    }

    private void setCurrentSortValues(Spinner sortSpinner, Spinner directionSpinner,
                                      String[][] sortOptions, String[][] directionOptions) {
        for (int i = 0; i < sortOptions.length; i++) {
            if (sortOptions[i][1].equals(currentSortBy)) {
                sortSpinner.setSelection(i);
                break;
            }
        }

        for (int i = 0; i < directionOptions.length; i++) {
            if (directionOptions[i][1].equals(currentSortDirection)) {
                directionSpinner.setSelection(i);
                break;
            }
        }
    }

    private void applySortSettings(Spinner sortSpinner, Spinner directionSpinner,
                                   String[][] sortOptions, String[][] directionOptions) {
        int sortPosition = sortSpinner.getSelectedItemPosition();
        int directionPosition = directionSpinner.getSelectedItemPosition();

        if (sortPosition >= 0 && sortPosition < sortOptions.length &&
                directionPosition >= 0 && directionPosition < directionOptions.length) {
            currentSortBy = sortOptions[sortPosition][1];
            currentSortDirection = directionOptions[directionPosition][1];
            viewModel.sortIngredients(currentSortBy, currentSortDirection);
        }
    }

    private void updateChipCounts() {
        updateCategoryChipCounts();
        updateExpiryChipCounts();
    }

    @SuppressLint("DefaultLocale")
    private void updateCategoryChipCounts() {
        binding.getRoot().post(() -> {
            for (int i = 0; i < binding.chipGroupCategories.getChildCount(); i++) {
                View view = binding.chipGroupCategories.getChildAt(i);
                if (view instanceof Chip) {
                    Chip chip = (Chip) view; // Ép kiểu thủ công
                    int chipId = chip.getId();
                    if (chipId == chipAllCategoriesId) {
                        continue;
                    }
                    IngredientCategory category = chipIdToCategoryMap.get(chipId);
                    if (category != null) {
                        chip.setText(String.format("%s (%d)",
                                IngredientUtils.getCategoryDisplayName(category),
                                viewModel.getCountByCategory(category)));
                    }
                }
            }
        });
    }

    private void updateExpiryChipCounts() {
        binding.getRoot().post(() -> {
            binding.chipExpired.setText(String.format("Hết hạn (%d)", viewModel.getExpiredIngredients().size()));
            binding.chipNotExpired.setText(String.format("Còn hạn (%d)", viewModel.getNotExpiredIngredients().size()));
        });
    }

    @Nullable
    private IngredientCategory getIngredientCategoryFromChipId(int chipId) {
        return chipIdToCategoryMap.get(chipId);
    }

    private void showIngredientDialog(@Nullable IngredientDataResponseDto ingredient) {
        if (ingredientDialog != null) {
            ingredientDialog.show(ingredient);
        }
    }

    private void showDeleteConfirmationDialog(IngredientDataResponseDto ingredient) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Xác nhận xóa")
                .setMessage(String.format("Bạn có chắc chắn muốn xóa %s?", ingredient.getName()))
                .setPositiveButton("Xóa", (dialog, which) ->
                        viewModel.deleteIngredient(ingredient.getId()))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void loadInitialData(IngredientSearchResultDto ingredients) {
        viewModel.loadIngredients(ingredients);
    }

    @Override
    public void onIngredientClick(IngredientDataResponseDto ingredient) {
        showIngredientDialog(ingredient);
    }

    @Override
    public void onEditIngredient(IngredientDataResponseDto ingredient) {
        showIngredientDialog(ingredient);
    }

    @Override
    public void onDeleteIngredient(IngredientDataResponseDto ingredient) {
        showDeleteConfirmationDialog(ingredient);
    }

    @Override
    public void onViewDetails(IngredientDataResponseDto ingredient) {
        showIngredientDialog(ingredient);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImagePickerHelper.cleanup(this);
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.dismiss();
        }
        binding = null;
        if (ingredientDialog != null) {
            ingredientDialog.dismiss();
            ingredientDialog = null;
        }
    }


    @Override
    public void onIngredientSaved(Ingredient ingredient, boolean isEditing, int existingId, File imageFile) {
        if (isEditing) {
            viewModel.updateIngredient(existingId, ingredient, imageFile);
        } else {
            viewModel.addIngredient(ingredient, imageFile); // Assuming no image for simplicity
        }
    }

    @Override
    public void onIngredientDeleted(int ingredientId) {
        viewModel.deleteIngredient(ingredientId);
    }

    @Override
    public void onImagePickerRequested(int requestCode) {
        handleImagePickerRequest(requestCode);
    }

    private void handleImagePickerRequest(int requestCode) {
        // Implement logic để xử lý camera/gallery request
        switch (requestCode) {
            case 1001: // CAMERA_REQUEST
                // Xử lý camera
                openCamera();
                break;
            case 1002: // PICK_IMAGE_REQUEST
                openGallery();
                // Xử lý gallery
                break;
            default:
                break;
        }
    }

    // Thêm method để mở camera
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Tạo file để lưu ảnh
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("Camera", "Error creating image file", ex);
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android_exam.fileprovider",
                        photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, 1001);
            }
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    // Thêm method để mở gallery
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 1002);
    }

    // Tạo file để lưu ảnh từ camera
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // Tạo file temporary
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        currentPhotoPath = image.getAbsolutePath();

        return image;
    }

}