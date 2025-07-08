package com.example.android_exam.activities.dialog;


import static android.app.Activity.RESULT_OK;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ParseException;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.android_exam.R;
import com.example.android_exam.activities.IngredientManagementActivity;
import com.example.android_exam.activities.LoadingActivity;
import com.example.android_exam.data.api.ApiManager;
import com.example.android_exam.data.api.AuthCallback;
import com.example.android_exam.data.dto.ingredient.IngredientAnalysticResponseDto;
import com.example.android_exam.data.dto.ingredient.IngredientDataResponseDto;
import com.example.android_exam.data.dto.response.ApiResponse;
import com.example.android_exam.data.models.base.Ingredient;
import com.example.android_exam.data.models.enums.IngredientCategory;
import com.example.android_exam.data.models.enums.IngredientUnit;
import com.example.android_exam.databinding.DialogIngredientDetailBinding;
import com.example.android_exam.module.image.ImagePickerHelper;
import com.example.android_exam.module.image.ImagePickerModule;
import com.example.android_exam.utils.DateUtils;
import com.example.android_exam.utils.FileUtils;
import com.example.android_exam.utils.IngredientUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class IngredientDialog {

    public interface OnIngredientDialogListener {
        void onIngredientSaved(Ingredient ingredient, boolean isEditing, int existingId, File imageFile);
        void onIngredientDeleted(int ingredientId);
        void onImagePickerRequested(int requestCode);
    }

    private static final int CAMERA_REQUEST = 1001;
    private static final int PICK_IMAGE_REQUEST = 1002;
    private static final int PERMISSION_REQUEST_CODE = 1003;

    private final Context context;
    private final OnIngredientDialogListener listener;
    private DialogIngredientDetailBinding binding;
    private AlertDialog dialog;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Uri selectedImageUri;
    private IngredientDataResponseDto currentIngredient;


    private boolean isEditing;

    public IngredientDialog(Context context, OnIngredientDialogListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void show(@Nullable IngredientDataResponseDto ingredient) {
        this.currentIngredient = ingredient;
        this.isEditing = ingredient != null;

        binding = DialogIngredientDetailBinding.inflate(android.view.LayoutInflater.from(context));

        setupDialogComponents();

        if (isEditing && ingredient != null && ingredient.getId() > 0) {
            populateDialogForEditing(ingredient);
        } else {
            binding.btnDelete.setVisibility(View.GONE);
        }

        dialog = new MaterialAlertDialogBuilder(context)
                .setView(binding.getRoot())
                .setCancelable(false)
                .setTitle(isEditing ? "Chi tiết nguyên liệu" : "Thêm nguyên liệu mới")
                .create();

        setupDialogButtons();
        setupImageClickListeners();
        dialog.show();
    }

    public void dismiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void updateSelectedImage(Uri imageUri) {
        this.selectedImageUri = imageUri;

        if (binding != null) {
            if (imageUri != null) {
                // Hiển thị hình ảnh từ URI
                Glide.with(context)
                        .load(imageUri)
                        .placeholder(R.drawable.ic_ingredient_placeholder)
                        .error(R.drawable.ic_ingredient_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .circleCrop()
                        .into(binding.imgIngredientPreview);
            }
        }
    }

    public void showValidationErrors(Map<String, String> errors) {
        if (binding == null) return;

        clearValidationErrors();

        if (errors.containsKey("name")) {
            binding.etIngredientName.setError(errors.get("name"));
        }
        if (errors.containsKey("quantity")) {
            binding.etQuantity.setError(errors.get("quantity"));
        }
        if (errors.containsKey("expiryDate")) {
            binding.etExpiryDate.setError(errors.get("expiryDate"));
        }
        if (errors.containsKey("unit")) {
            binding.spinnerUnit.setError(errors.get("unit"));
        }
        if (errors.containsKey("category")) {
            binding.spinnerCategory.setError(errors.get("category"));
        }
    }

    private void clearValidationErrors() {
        if (binding == null) return;

        binding.etIngredientName.setError(null);
        binding.etQuantity.setError(null);
        binding.etExpiryDate.setError(null);
        binding.spinnerUnit.setError(null);
        binding.spinnerCategory.setError(null);
    }

    private void setupDialogComponents() {
        setupUnitSpinner(binding.spinnerUnit);
        setupCategorySpinner(binding.spinnerCategory);
        setupDatePicker(binding.etExpiryDate);
        clearValidationErrors();
    }

    private void populateDialogForEditing(@NonNull IngredientDataResponseDto ingredient) {
        binding.etIngredientName.setText(ingredient.getName());
        binding.etDescription.setText(ingredient.getDescription());
        binding.etQuantity.setText(String.valueOf(ingredient.getQuantity()));

        if (ingredient.getExpiryDate() != null) {
            setExpiryDateFromIngredient(ingredient);
        }

        String unitDisplay = IngredientUtils.getUnitDisplayName(ingredient.getUnit());
        binding.spinnerUnit.setText(unitDisplay, false);
        binding.spinnerCategory.setText(IngredientUtils.getCategoryDisplayName(ingredient.getCategory()), false);
        binding.btnDelete.setVisibility(ingredient.getId() > 0 ? View.VISIBLE : View.GONE);

        // Load image if available
        if (ingredient.getImageUrl() != null && !ingredient.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(ingredient.getImageUrl())
                    .placeholder(R.drawable.ic_ingredient_placeholder)
                    .error(R.drawable.ic_ingredient_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .circleCrop()
                    .into(binding.imgIngredientPreview);
        } else {
            binding.imgIngredientPreview.setImageResource(IngredientUtils.getDefaultImageForCategory(ingredient.getCategory()));
        }
    }

    private void setExpiryDateFromIngredient(IngredientDataResponseDto ingredient) {
        try {
            LocalDateTime expiryDate = DateUtils.parseLocalDateTime(ingredient.getExpiryDate());
            Calendar cal = Calendar.getInstance();
            if (expiryDate != null) {
                Date date = Date.from(expiryDate.atZone(ZoneId.systemDefault()).toInstant());
                cal.setTime(date);
            }
            LocalDate localDate = LocalDate.of(
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH)
            );
            binding.etExpiryDate.setText(localDate.format(dateFormatter));
        } catch (Exception e) {
            Log.e("IngredientDialog", "Error setting expiry date", e);
        }
    }

    private void setupDialogButtons() {
        binding.btnCancel.setOnClickListener(v -> dismiss());

        binding.btnSave.setOnClickListener(v -> {
            if (saveIngredient()) {
                dismiss();
            }
        });

        if(!isEditing){
            binding.btnDetectIngredients.setOnClickListener(v-> {
                if(selectedImageUri == null) {
                    Toast.makeText(context, "Vui lòng chọn hình ảnh trước", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Todo: Call Api to detect ingredients
                var file = FileUtils.getFileFromUri(context, selectedImageUri);
                if (file == null) {
                    Toast.makeText(context, "Lỗi khi xử lý ảnh đã chọn", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoadingActivity.getInstance().show(context);
                ApiManager.getInstance().getAiClient().detectIngredient(file, new AuthCallback<ApiResponse<IngredientAnalysticResponseDto>>() {
                    @Override
                    public void onSuccess(ApiResponse<IngredientAnalysticResponseDto> result) {
                        LoadingActivity.getInstance().hide();
                        //Set the detected ingredient data to the dialog fields
                        IngredientAnalysticResponseDto data = result.getData();
                        if (data == null) {
                            new Handler(Looper.getMainLooper()).post(() ->
                                    Toast.makeText(context, "Không tìm thấy thông tin nguyên liệu", Toast.LENGTH_SHORT).show()
                            );
                            Log.d("IngredientDialog", "No ingredient data found in response");
                            return;

                        }
                        new Handler(Looper.getMainLooper()).post(() -> populateDialogForEditing(data));
                    }

                    @Override
                    public void onError(String error) {
                        LoadingActivity.getInstance().hide();
                        Log.e("IngredientDialog", "Error detecting ingredient: " + error);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        LoadingActivity.getInstance().hide();
                        Log.e("IngredientDialog", "Error detecting ingredient: ", throwable);
                    }
                });

            });
        }else{
            binding.btnDetectIngredients.setVisibility(View.GONE);
        }

        if (isEditing && currentIngredient != null) {
            binding.btnDelete.setOnClickListener(v -> {
                showDeleteConfirmationDialog();
                dismiss();
            });
        }
    }

    private void setupImageClickListeners() {
        binding.imgIngredientPreview.setOnClickListener(v -> showImagePickerDialog());
        binding.imgCameraOverlay.setOnClickListener(v -> showImagePickerDialog());
    }

    private void showImagePickerDialog() {
        //call here
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;

            ImagePickerHelper.pickImage(activity, new ImagePickerModule.ImagePickerCallback(){
                @Override
                public void onImagePicked(Uri imageUri) {
                    // Log the selected image URI
                    Log.d("IngredientDialog", "Selected image URI: " + imageUri.toString());
                    selectedImageUri = imageUri;
                    updateSelectedImage(imageUri);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(context, "Lỗi khi chọn ảnh: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(context, "Không thể mở trình chọn ảnh", Toast.LENGTH_SHORT).show();
        }
    }


    private void setupUnitSpinner(AutoCompleteTextView spinner) {
        IngredientUnit[] unitValues = IngredientUnit.values();
        List<String> displayUnits = new ArrayList<>();

        for (IngredientUnit unit : unitValues) {
            if (unit != IngredientUnit.OTHER) {
                displayUnits.add(IngredientUtils.getUnitDisplayName(unit));
            }
        }

        String[] displayUnitsArray = displayUnits.toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_dropdown_item_1line, displayUnitsArray);
        spinner.setAdapter(adapter);

        spinner.setOnItemClickListener((parent, view, position, id) -> {
            String selectedUnit = displayUnitsArray[position];
            spinner.setText(selectedUnit, false);
            Log.d("IngredientDialog", "Selected unit: " + selectedUnit);
        });
    }

    private void setupCategorySpinner(AutoCompleteTextView spinner) {
        IngredientCategory[] categoryValues = IngredientCategory.values();
        List<String> displayCategories = new ArrayList<>();

        for (IngredientCategory category : categoryValues) {
            if (category != IngredientCategory.OTHER) {
                displayCategories.add(IngredientUtils.getCategoryDisplayName(category));
            }
        }

        String[] displayCategoriesArray = displayCategories.toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_dropdown_item_1line, displayCategoriesArray);
        spinner.setAdapter(adapter);

        spinner.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCategory = displayCategoriesArray[position];
            spinner.setText(selectedCategory, false);
            Log.d("IngredientDialog", "Selected category: " + selectedCategory);
        });
    }

    private void setupDatePicker(TextView etDate) {
        etDate.setOnClickListener(v -> showDatePickerDialog(etDate));
    }

    private void showDatePickerDialog(TextView etDate) {
        Calendar calendar = Calendar.getInstance();
        String currentDate = etDate.getText().toString();

        if (!currentDate.isEmpty()) {
            try {
                LocalDate localDate = LocalDate.parse(currentDate, dateFormatter);
                calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
            } catch (Exception e) {
                Log.e("IngredientDialog", "Error parsing date", e);
            }
        }

        new DatePickerDialog(
                context,
                (view, year, month, dayOfMonth) -> {
                    LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
                    etDate.setText(selectedDate.format(dateFormatter));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private boolean saveIngredient() {
        try {
            Ingredient ingredient = createIngredientFromDialog();
            int existingId = isEditing && currentIngredient != null ? currentIngredient.getId() : 0;
            File imageFile = null;

            if (selectedImageUri != null) {
                imageFile = FileUtils.getFileFromUri(context, selectedImageUri);
                if (imageFile == null) {
                    Toast.makeText(context, "Lỗi khi xử lý ảnh đã chọn", Toast.LENGTH_SHORT).show();
                    return false;
                }
                Log.d("IngredientDialog", "Image file created: " + imageFile.getName() +
                        ", size: " + imageFile.length() + " bytes");
            }

            if (listener != null) {
                listener.onIngredientSaved(ingredient, isEditing, existingId,  imageFile);
            }
            return true;
        } catch (Exception e) {
            Toast.makeText(context, "Lỗi khi lưu 1 thực phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private Ingredient createIngredientFromDialog() throws Exception {
        String name = getTextFromEditText(binding.etIngredientName);
        String description = getTextFromEditText(binding.etDescription);
        String quantityStr = getTextFromEditText(binding.etQuantity);
        String unitStr = binding.spinnerUnit.getText().toString().trim();
        String categoryStr = binding.spinnerCategory.getText().toString().trim();
        String dateStr = getTextFromEditText(binding.etExpiryDate);

        validateRequiredFields(name, quantityStr, dateStr, unitStr, categoryStr);
        double quantity = parseAndValidateQuantity(quantityStr);
        IngredientUnit unit = IngredientUtils.parseUnit(unitStr);
        IngredientCategory category = IngredientUtils.parseCategory(categoryStr);
        Date expiryDate = DateUtils.parseIsoDateToDate(dateStr);

        return createIngredientObject(name, description, quantity, unit, category, expiryDate);
    }

    private String getTextFromEditText(TextView editText) {
        return Objects.requireNonNull(editText.getText()).toString().trim();
    }

    private void validateRequiredFields(String name, String quantityStr, String dateStr,
                                        String unitStr, String categoryStr) throws Exception {
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Tên thực phẩm không được để trống");
        }
        if (quantityStr.isEmpty()) {
            throw new IllegalArgumentException("Số lượng không được để trống");
        }
        if (dateStr.isEmpty()) {
            throw new IllegalArgumentException("Ngày hết hạn không được để trống");
        }
        if (unitStr.isEmpty()) {
            throw new IllegalArgumentException("Đơn vị không được để trống");
        }
        if (categoryStr.isEmpty()) {
            throw new IllegalArgumentException("Danh mục không được để trống");
        }
    }

    private double parseAndValidateQuantity(String quantityStr) throws Exception {
        double quantity;
        try {
            quantity = Double.parseDouble(quantityStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Số lượng không hợp lệ");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }
        return quantity;
    }

    private Date convertStringToDate(String dateStr) throws ParseException, java.text.ParseException {
        Log.d("IngredientDialog", "Converting date string: " + dateStr);
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        // Format phải khớp với input người dùng
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date = inputFormat.parse(dateStr);

        // Không cần format lại ở đây, Gson sẽ tự convert thành ISO format khi post lên server
        return date;
    }


    private Ingredient createIngredientObject(String name, String description, double quantity,
                                              IngredientUnit unit, IngredientCategory category, Date expiryDate) {
        Ingredient ingredient = new Ingredient();
        ingredient.setName(name);
        ingredient.setDescription(description);
        ingredient.setQuantity(quantity);
        ingredient.setUnit(unit);
        ingredient.setCategory(category);
        ingredient.setExpiryDate(expiryDate);
        return ingredient;
    }

    private void showDeleteConfirmationDialog() {
        if (currentIngredient == null) return;

        new MaterialAlertDialogBuilder(context)
                .setTitle("Xác nhận xóa")
                .setMessage(String.format("Bạn có chắc chắn muốn xóa %s?", currentIngredient.getName()))
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (listener != null) {
                        listener.onIngredientDeleted(currentIngredient.getId());
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Getter for accessing selected image URI
    public Uri getSelectedImageUri() {
        return selectedImageUri;
    }

    public void setSelectedImageUri(Uri uri) {
        this.selectedImageUri = uri;
    }

}