package com.example.android_exam.viewmodels;
import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.android_exam.data.local.database.AppDatabase;
import com.example.android_exam.data.local.entity.*;
import com.example.android_exam.data.models.DiaryWithMeals;
import com.example.android_exam.data.models.MealWithFoods;
import com.example.android_exam.data.remote.DataRepository;
import com.example.android_exam.utils.DateUtils;
import java.util.List;
import java.util.ArrayList;

public class HomeViewModel extends AndroidViewModel {

    private MutableLiveData<String> aiAnalysis = new MutableLiveData<>();
    private MutableLiveData<List<Food>> suggestedMeals = new MutableLiveData<>();
    private MutableLiveData<List<Ingredient>> expiringIngredients = new MutableLiveData<>();
    private LiveData<List<Ingredient>> expiringIngredientsSource;
    private MutableLiveData<List<DiaryWithMeals>> calorieAnalysis = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private int currentUserId;

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }

    // LiveData Getters
    public LiveData<String> getAiAnalysis() { return aiAnalysis; }
    public LiveData<List<Food>> getSuggestedMeals() { return suggestedMeals; }
    public LiveData<List<Ingredient>> getExpiringIngredients() { return expiringIngredients; }
    public LiveData<List<DiaryWithMeals>> getCalorieAnalysis() { return calorieAnalysis; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadAllData() {
        loadAiAnalysis();
        loadSuggestedMeals();
        loadExpiringIngredients();
        loadCalorieAnalysis();
    }

    private void loadAiAnalysis() {
        isLoading.setValue(true);
        String today = DateUtils.getTodayString();

        LocalDataRepository.getInstance().getTotalCaloriesByDate(today, new DataRepository.AuthCallback<Double>() {
            @Override
            public void onSuccess(Double todayCalories) {
                LocalDataRepository.getInstance().getNutritionGoal(new DataRepository.AuthCallback<NutritionGoal>() {
                    @Override
                    public void onSuccess(NutritionGoal goal) {
                        String analysis = generateAiAnalysisText(todayCalories, goal);
                        aiAnalysis.postValue(analysis);
                        isLoading.postValue(false);
                    }

                    @Override
                    public void onError(String error) {
                        aiAnalysis.postValue("Unable to load nutrition analysis. Please set your goals first.");
                        isLoading.postValue(false);
                    }
                });
            }

            @Override
            public void onError(String error) {
                errorMessage.postValue("Error loading calorie data: " + error);
                isLoading.postValue(false);
            }
        });
    }

    private String generateAiAnalysisText(double todayCalories, NutritionGoal goal) {
        StringBuilder analysis = new StringBuilder();
        analysis.append("🤖 AI Analysis:\n");

        if (goal != null) {
            double remainingCalories = goal.dailyCalorieGoal - todayCalories;

            if (remainingCalories > 500) {
                analysis.append("You're behind your calorie goal! Consider having a nutritious meal. ");
                analysis.append("Remaining: ").append(String.format("%.0f", remainingCalories)).append(" kcal");
            } else if (remainingCalories > 0) {
                analysis.append("Great progress! You have ").append(String.format("%.0f", remainingCalories))
                        .append(" kcal left for today. Perfect for a light meal.");
            } else if (remainingCalories > -200) {
                analysis.append("You've reached your daily goal! Well done! Consider light activities if you want to eat more.");
            } else {
                analysis.append("You've exceeded your daily goal by ").append(String.format("%.0f", Math.abs(remainingCalories)))
                        .append(" kcal. Try some light exercise!");
            }

            // Add goal-specific advice
            if (goal.goalType.equals("weight_loss")) {
                analysis.append("\n💡 Tip: Focus on protein-rich meals to maintain muscle while losing weight.");
            } else if (goal.goalType.equals("muscle_gain")) {
                analysis.append("\n💡 Tip: Don't forget to include protein after workouts for muscle recovery.");
            } else {
                analysis.append("\n💡 Tip: Maintain a balanced diet with variety in nutrients.");
            }
        } else {
            analysis.append("Set your nutrition goals to get personalized AI insights!");
        }

        return analysis.toString();
    }

    private void loadSuggestedMeals() {
        LocalDataRepository.getInstance().getSuggestedMeals(new DataRepository.AuthCallback<List<Food>>() {
            @Override
            public void onSuccess(List<Food> meals) {
                if (meals != null && !meals.isEmpty()) {
                    suggestedMeals.postValue(meals);
                } else {
                    // Generate mock suggestions if AI service is not available
                    suggestedMeals.postValue(generateMockSuggestions());
                }
            }

            @Override
            public void onError(String error) {
                errorMessage.postValue("Error loading meal suggestions: " + error);
                suggestedMeals.postValue(generateMockSuggestions());
            }
        });
    }

    private List<Food> generateMockSuggestions() {
        List<Food> mockMeals = new ArrayList<>();

        Food meal1 = new Food();
        meal1.name = "Potato Soup";
        meal1.totalCalories = 200;
        meal1.description = "Creamy soup made from potatoes";

        Food meal2 = new Food();
        meal2.name = "Tomato Salad";
        meal2.totalCalories = 120;
        meal2.description = "Fresh salad with tomatoes";

        Food meal3 = new Food();
        meal3.name = "Carrot Stir-fry";
        meal3.totalCalories = 150;
        meal3.description = "Quick stir-fry with carrots";

        mockMeals.add(meal1);
        mockMeals.add(meal2);
        mockMeals.add(meal3);

        return mockMeals;
    }

    private void loadExpiringIngredients() {
        isLoading.setValue(true);

        if (currentUserId <= 0) {
            errorMessage.postValue("ID người dùng chưa được đặt. Vui lòng đăng nhập trước.");
            expiringIngredients.postValue(new ArrayList<>());
            isLoading.postValue(false);
            return;
        }

        // Gọi API lấy dữ liệu bất đồng bộ
        LocalDataRepository.getInstance().getExpiringIngredients(10, new DataRepository.AuthCallback<List<Ingredient>>() {
            @Override
            public void onSuccess(List<Ingredient> result) {
                isLoading.postValue(false);
                if (result != null && !result.isEmpty()) {
                    Log.d("HomeViewModel", "Tải được " + result.size() + " nguyên liệu sắp hết hạn");
                    expiringIngredients.postValue(result);
                } else {
                    Log.d("HomeViewModel", "Danh sách nguyên liệu rỗng");
                    expiringIngredients.postValue(new ArrayList<>());
                }
            }

            @Override
            public void onError(String error) {
                errorMessage.postValue("Error loading expiring ingredients: " + error);
                expiringIngredients.postValue(new ArrayList<>());
                isLoading.postValue(false);
            }
        });
    }


    // Alternative: Sử dụng MediatorLiveData (Recommended)
    private void loadExpiringIngredientsWithMediator() {
        isLoading.setValue(true);

        if (currentUserId <= 0) {
            errorMessage.postValue("ID người dùng chưa được đặt. Vui lòng đăng nhập trước.");
            expiringIngredients.postValue(new ArrayList<>());
            isLoading.postValue(false);
            return;
        }

        // Remove previous source nếu có
        if (expiringIngredientsSource != null && expiringIngredients instanceof MediatorLiveData) {
            ((MediatorLiveData<List<Ingredient>>) expiringIngredients).removeSource(expiringIngredientsSource);
        }

        // Get new source
        LocalDataRepository.getInstance().getExpiringIngredients(10, new DataRepository.AuthCallback<List<Ingredient>>() {
            @Override
            public void onSuccess(List<Ingredient> result) {
                expiringIngredientsSource = new MutableLiveData<>(result);
            }

            @Override
            public void onError(String error) {
                errorMessage.postValue("Error loading expiring ingredients: " + error);
                expiringIngredientsSource = new MutableLiveData<>(new ArrayList<>());
            }
        });

        // Add source to MediatorLiveData
        if (!(expiringIngredients instanceof MediatorLiveData)) {
            // Convert to MediatorLiveData nếu chưa phải
            MediatorLiveData<List<Ingredient>> mediator = new MediatorLiveData<>();
            mediator.setValue(expiringIngredients.getValue());
            expiringIngredients = mediator;
        }

        ((MediatorLiveData<List<Ingredient>>) expiringIngredients).addSource(expiringIngredientsSource, ingredients -> {
            isLoading.postValue(false);

            if (ingredients != null) {
                Log.d("HomeViewModel", "Tải được " + ingredients.size() + " nguyên liệu sắp hết hạn");
                expiringIngredients.postValue(ingredients);
            } else {
                Log.d("HomeViewModel", "Danh sách nguyên liệu rỗng");
                expiringIngredients.postValue(new ArrayList<>());
            }
        });
    }

    // Method để refresh data
    public void refreshExpiringIngredients() {
        LocalDataRepository.getInstance().refreshExpiringIngredients(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    private void loadCalorieAnalysis() {
        List<String> last5Days = DateUtils.getLast2Days();
        List<DiaryWithMeals> diaryList = new ArrayList<>();
        List<String> processedDays = new ArrayList<>();

        for (String day : last5Days) {
            LocalDataRepository.getInstance().getMealsByDate(day, new DataRepository.AuthCallback<List<Meal>>() {
                @Override
                public void onSuccess(List<Meal> resultMeals) {
                    List<Meal> meals = resultMeals != null ? resultMeals : new ArrayList<>();
                    List<MealWithFoods> mealsWithFoods = new ArrayList<>();

                    if (meals.isEmpty()) {
                        diaryList.add(new DiaryWithMeals(DateUtils.formatDateForDisplay(day), mealsWithFoods));
                        checkIfFinished();
                        return;
                    }

                    int[] mealsProcessed = {0};
                    for (Meal meal : meals) {
                        LocalDataRepository.getInstance().getFoodsByMealId(meal.id, new DataRepository.AuthCallback<List<Food>>() {
                            @Override
                            public void onSuccess(List<Food> foods) {
                                mealsWithFoods.add(new MealWithFoods(meal, foods != null ? foods : new ArrayList<>()));
                                mealsProcessed[0]++;
                                if (mealsProcessed[0] == meals.size()) {
                                    diaryList.add(new DiaryWithMeals(DateUtils.formatDateForDisplay(day), mealsWithFoods));
                                    checkIfFinished();
                                }
                            }

                            @Override
                            public void onError(String error) {
                                mealsWithFoods.add(new MealWithFoods(meal, new ArrayList<>()));
                                mealsProcessed[0]++;
                                if (mealsProcessed[0] == meals.size()) {
                                    diaryList.add(new DiaryWithMeals(DateUtils.formatDateForDisplay(day), mealsWithFoods));
                                    checkIfFinished();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onError(String error) {
                    diaryList.add(new DiaryWithMeals(DateUtils.formatDateForDisplay(day), new ArrayList<>()));
                    checkIfFinished();
                }

                private void checkIfFinished() {
                    synchronized (processedDays) {
                        processedDays.add(day);
                        if (processedDays.size() == last5Days.size()) {
                            calorieAnalysis.postValue(diaryList);
                        }
                    }
                }
            });
        }
    }

}
