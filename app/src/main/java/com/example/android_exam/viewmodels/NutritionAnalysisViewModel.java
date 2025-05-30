package com.example.android_exam.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.android_exam.data.local.database.AppDatabase;
import com.example.android_exam.data.local.entity.NutritionGoal;
import com.example.android_exam.utils.DateUtils;

public class NutritionAnalysisViewModel extends AndroidViewModel {

    private MutableLiveData<String> nutritionSummary = new MutableLiveData<>();
    private MutableLiveData<String> recommendations = new MutableLiveData<>();

    public NutritionAnalysisViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getNutritionSummary() {
        return nutritionSummary;
    }

    public LiveData<String> getRecommendations() {
        return recommendations;
    }

    public void loadNutritionData(int userId) {
        String today = DateUtils.getTodayString();
        AppDatabase.getTotalCaloriesByDate(userId, today, new AppDatabase.DatabaseCallback<Double>() {
            @Override
            public void onSuccess(Double todayCalories) {
                AppDatabase.getNutritionGoal(userId, new AppDatabase.DatabaseCallback<NutritionGoal>() {
                    @Override
                    public void onSuccess(NutritionGoal goal) {
                        StringBuilder summary = new StringBuilder();
                        summary.append("Today's Nutrition Summary:\n");
                        summary.append("Calories Consumed: ").append(String.format("%.0f kcal", todayCalories)).append("\n");
                        if (goal != null) {
                            summary.append("Calorie Goal: ").append(String.format("%.0f kcal", goal.dailyCalorieGoal)).append("\n");
                            summary.append("Protein Goal: ").append(String.format("%.0f g", goal.dailyProteinGoal)).append("\n");
                            summary.append("Carb Goal: ").append(String.format("%.0f g", goal.dailyCarbGoal)).append("\n");
                            summary.append("Fat Goal: ").append(String.format("%.0f g", goal.dailyFatGoal)).append("\n");
                            summary.append("Goal Type: ").append(goal.goalType);
                            nutritionSummary.postValue(summary.toString());

                            StringBuilder recs = new StringBuilder();
                            recs.append("Recommendations:\n");
                            double remainingCalories = goal.dailyCalorieGoal - todayCalories;
                            if (remainingCalories > 0) {
                                recs.append("You have ").append(String.format("%.0f kcal", remainingCalories))
                                        .append(" remaining. Consider a balanced meal with ");
                                if (goal.dailyProteinGoal > 0) {
                                    recs.append("protein-rich foods (e.g., chicken, tofu). ");
                                }
                                if (goal.dailyCarbGoal > 0) {
                                    recs.append("complex carbs (e.g., brown rice, quinoa). ");
                                }
                            } else {
                                recs.append("You've met or exceeded your calorie goal. Opt for low-calorie snacks if needed.");
                            }
                            recommendations.postValue(recs.toString());
                        } else {
                            nutritionSummary.postValue("No nutrition goals set.");
                            recommendations.postValue("Set your nutrition goals for personalized recommendations.");
                        }
                    }

                    @Override
                    public void onError(String error) {
                        nutritionSummary.postValue("Error loading nutrition goals: " + error);
                    }
                });
            }

            @Override
            public void onError(String error) {
                nutritionSummary.postValue("Error loading calorie data: " + error);
            }
        });
    }
}