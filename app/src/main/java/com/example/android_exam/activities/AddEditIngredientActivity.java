package com.example.android_exam.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.android_exam.R;
import com.example.android_exam.data.local.entity.Ingredient;
import com.example.android_exam.viewmodels.IngredientsViewModel;
import androidx.lifecycle.ViewModelProvider;

public class AddEditIngredientActivity extends AppCompatActivity {

    private EditText etName, etQuantity, etExpiryDate, etCalories;
    private Spinner spUnit, spCategory;
    private Button btnSave;
    private IngredientsViewModel viewModel;
    private int userId, ingredientId = -1;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        initViews();
        setupViewModel();
        setupSpinners();
        checkEditMode();
        setupObservers();
        setupSaveButton();
    }

    private void initViews() {
        etName = findViewById(R.id.et_ingredient_name);
        etQuantity = findViewById(R.id.et_ingredient_quantity);
        etExpiryDate = findViewById(R.id.et_expiry_date);
        etCalories = findViewById(R.id.et_calories_per_unit);
        spUnit = findViewById(R.id.sp_unit);
        spCategory = findViewById(R.id.sp_category);
        btnSave = findViewById(R.id.btn_save_ingredient);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(IngredientsViewModel.class);
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(
                this, R.array.measurement_units, android.R.layout.simple_spinner_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUnit.setAdapter(unitAdapter);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this, R.array.ingredient_categories, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);
    }

    private void checkEditMode() {
        userId = getIntent().getIntExtra("USER_ID", -1);
        ingredientId = getIntent().getIntExtra("INGREDIENT_ID", -1);
        isEditMode = ingredientId != -1;

        if (isEditMode) {
            setTitle("Edit Ingredient");
            etName.setText(getIntent().getStringExtra("INGREDIENT_NAME"));
            etQuantity.setText(String.valueOf(getIntent().getDoubleExtra("INGREDIENT_QUANTITY", 0)));
            etExpiryDate.setText(getIntent().getStringExtra("INGREDIENT_EXPIRY"));
            etCalories.setText(String.valueOf(getIntent().getDoubleExtra("INGREDIENT_CALORIES", 0)));

            String unit = getIntent().getStringExtra("INGREDIENT_UNIT");
            String category = getIntent().getStringExtra("INGREDIENT_CATEGORY");
            ArrayAdapter<CharSequence> unitAdapter = (ArrayAdapter<CharSequence>) spUnit.getAdapter();
            ArrayAdapter<CharSequence> categoryAdapter = (ArrayAdapter<CharSequence>) spCategory.getAdapter();
            spUnit.setSelection(unitAdapter.getPosition(unit));
            spCategory.setSelection(categoryAdapter.getPosition(category));
        } else {
            setTitle("Add Ingredient");
        }
    }

    private void setupObservers() {
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> {
            if (validateInputs()) {
                Ingredient ingredient = new Ingredient();
                ingredient.userId = userId;
                ingredient.name = etName.getText().toString().trim();
                ingredient.quantity = Double.parseDouble(etQuantity.getText().toString().trim());
                ingredient.unit = spUnit.getSelectedItem().toString();
                ingredient.setExpiryDateTimestamp(etExpiryDate.getText().toString().trim());
                ingredient.category = spCategory.getSelectedItem().toString();
                if (isEditMode) {
                    ingredient.id = ingredientId;
                    viewModel.updateIngredient(ingredient);
                } else {
                    viewModel.addIngredient(ingredient);
                }
                finish();
            }
        });
    }

    private boolean validateInputs() {
        if (etName.getText().toString().trim().isEmpty()) {
            etName.setError("Name is required");
            return false;
        }
        if (etQuantity.getText().toString().trim().isEmpty()) {
            etQuantity.setError("Quantity is required");
            return false;
        }
        try {
            Double.parseDouble(etQuantity.getText().toString().trim());
        } catch (NumberFormatException e) {
            etQuantity.setError("Invalid quantity");
            return false;
        }
        if (etCalories.getText().toString().trim().isEmpty()) {
            etCalories.setError("Calories is required");
            return false;
        }
        try {
            Double.parseDouble(etCalories.getText().toString().trim());
        } catch (NumberFormatException e) {
            etCalories.setError("Invalid calories");
            return false;
        }
        return true;
    }
}