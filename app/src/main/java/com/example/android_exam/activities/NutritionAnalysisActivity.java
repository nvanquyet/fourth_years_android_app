package com.example.android_exam.activities;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.android_exam.R;
import com.example.android_exam.utils.SessionManager;
import com.example.android_exam.viewmodels.NutritionAnalysisViewModel;

public class NutritionAnalysisActivity extends AppCompatActivity {

    private TextView tvNutritionSummary, tvRecommendations;
    private NutritionAnalysisViewModel viewModel;
    private int currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_analysis);

        currentUserId = SessionManager.getCurrentUserId(this);
        if (currentUserId == -1) {
            finish();
            return;
        }

        initViews();
        setupViewModel();
        setupObservers();
        viewModel.loadNutritionData(currentUserId);
    }

    private void initViews() {
        tvNutritionSummary = findViewById(R.id.tv_nutrition_summary);
        tvRecommendations = findViewById(R.id.tv_nutrition_recommendations);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(NutritionAnalysisViewModel.class);
    }

    private void setupObservers() {
        viewModel.getNutritionSummary().observe(this, summary -> {
            if (summary != null) {
                tvNutritionSummary.setText(summary);
            }
        });

        viewModel.getRecommendations().observe(this, recommendations -> {
            if (recommendations != null) {
                tvRecommendations.setText(recommendations);
            }
        });
    }
}