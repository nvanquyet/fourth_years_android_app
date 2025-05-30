package com.example.android_exam.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android_exam.R;
import com.example.android_exam.adapters.IngredientsAdapter;
import com.example.android_exam.data.local.entity.Ingredient;
import com.example.android_exam.utils.SessionManager;
import com.example.android_exam.viewmodels.IngredientsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class IngredientsActivity extends AppCompatActivity {

    private RecyclerView rvIngredients;
    private FloatingActionButton fabAddIngredient;
    private IngredientsViewModel viewModel;
    private IngredientsAdapter adapter;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        currentUserId = SessionManager.getCurrentUserId(this);
        if (currentUserId == -1) {
            finish();
            return;
        }

        initViews();
        setupViewModel();
        setupRecyclerView();
        setupObservers();
        viewModel.loadIngredients(currentUserId);
    }

    private void initViews() {
        rvIngredients = findViewById(R.id.rv_ingredients);
        fabAddIngredient = findViewById(R.id.fab_add_ingredient);
        fabAddIngredient.setOnClickListener(v -> openAddIngredientScreen());
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(IngredientsViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new IngredientsAdapter(new ArrayList<>(), new IngredientsAdapter.OnIngredientActionListener() {
            @Override
            public void onEdit(Ingredient ingredient) {
                openEditIngredientScreen(ingredient);
            }

            @Override
            public void onDelete(Ingredient ingredient) {
                viewModel.deleteIngredient(ingredient);
            }
        });
        rvIngredients.setLayoutManager(new LinearLayoutManager(this));
        rvIngredients.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getIngredients().observe(this, ingredients -> {
            if (ingredients != null) {
                adapter.updateIngredients(ingredients);
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openAddIngredientScreen() {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra("USER_ID", currentUserId);
        startActivity(intent);
    }

    private void openEditIngredientScreen(Ingredient ingredient) {
        Intent intent = new Intent(this, AddEditIngredientActivity.class);
        intent.putExtra("USER_ID", currentUserId);
        intent.putExtra("INGREDIENT_ID", ingredient.id);
        intent.putExtra("INGREDIENT_NAME", ingredient.name);
        intent.putExtra("INGREDIENT_QUANTITY", ingredient.quantity);
        intent.putExtra("INGREDIENT_UNIT", ingredient.unit);
        intent.putExtra("INGREDIENT_EXPIRY", ingredient.expiryDate);
        intent.putExtra("INGREDIENT_CALORIES", ingredient.caloriesPerUnit);
        intent.putExtra("INGREDIENT_CATEGORY", ingredient.category);
        startActivity(intent);
    }

    @Override


    protected void onResume() {
        super.onResume();
        viewModel.loadIngredients(currentUserId);
    }
}