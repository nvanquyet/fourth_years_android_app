package com.example.android_exam.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.android_exam.data.dto.nutrition.DailyNutritionSummaryDto;
import com.example.android_exam.data.dto.nutrition.WeeklyNutritionSummaryDto;
import com.example.android_exam.data.repository.NutritionRepository;

import java.util.Calendar;
import java.util.Date;

public class NutritionAnalysisViewModel extends ViewModel {
    private NutritionRepository repository;
    private MutableLiveData<Date> currentDate = new MutableLiveData<>();
    private MutableLiveData<DailyNutritionSummaryDto> dailyNutrition = new MutableLiveData<>();
    private MutableLiveData<WeeklyNutritionSummaryDto> weeklyNutrition = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();

    public NutritionAnalysisViewModel() {
        repository = new NutritionRepository();
        currentDate.setValue(new Date());
    }

    public LiveData<Date> getCurrentDate() { return currentDate; }
    public LiveData<DailyNutritionSummaryDto> getDailyNutrition() { return dailyNutrition; }
    public LiveData<WeeklyNutritionSummaryDto> getWeeklyNutrition() { return weeklyNutrition; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getError() { return error; }

    public void loadTodayNutrition() {
        loadDailyNutrition(new Date());
    }

    public void loadCurrentWeekNutrition() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        loadWeeklyNutrition(cal.getTime());
    }

    public void loadDailyNutrition(Date date) {
        isLoading.setValue(true);
        currentDate.setValue(date);
        repository.getDailyNutrition(date, new NutritionRepository.Callback<DailyNutritionSummaryDto>() {
            @Override
            public void onSuccess(DailyNutritionSummaryDto result) {
                dailyNutrition.postValue(result);
                isLoading.postValue(false);
            }

            @Override
            public void onError(String errorMessage) {
                error.postValue(errorMessage);
                isLoading.postValue(false);
            }
        });
    }

    public void loadWeeklyNutrition(Date date) {
        isLoading.setValue(true);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Date weekStart = cal.getTime();
        currentDate.setValue(weekStart);
        repository.getWeeklyNutrition(weekStart, new NutritionRepository.Callback<WeeklyNutritionSummaryDto>() {
            @Override
            public void onSuccess(WeeklyNutritionSummaryDto result) {
                weeklyNutrition.postValue(result);
                isLoading.postValue(false);
            }

            @Override
            public void onError(String errorMessage) {
                error.postValue(errorMessage);
                isLoading.postValue(false);
            }
        });
    }

    public void navigateToPreviousDay() {
        Date current = currentDate.getValue();
        if (current != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(current);
            cal.add(Calendar.DAY_OF_MONTH, -1);
            Date newDate = cal.getTime();
            currentDate.setValue(newDate);
            loadDailyNutrition(newDate);
        }
    }

    public void navigateToNextDay() {
        Date current = currentDate.getValue();
        if (current != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(current);
            cal.add(Calendar.DAY_OF_MONTH, 1);
            Date newDate = cal.getTime();
            currentDate.setValue(newDate);
            loadDailyNutrition(newDate);
        }
    }

    public void navigateToPreviousWeek() {
        Date current = currentDate.getValue();
        if (current != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(current);
            cal.add(Calendar.WEEK_OF_YEAR, -1);
            Date newDate = cal.getTime();
            currentDate.setValue(newDate);
            loadWeeklyNutrition(newDate);
        }
    }

    public void navigateToNextWeek() {
        Date current = currentDate.getValue();
        if (current != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(current);
            cal.add(Calendar.WEEK_OF_YEAR, 1);
            Date newDate = cal.getTime();

            // Gán giá trị mới cho LiveData
            currentDate.setValue(newDate);

            // Load dữ liệu mới
            loadWeeklyNutrition(newDate);
        }
    }

    public void setSelectedDate(Date date) {
        currentDate.setValue(date);
        loadDailyNutrition(date);
    }

    public void setSelectedWeek(Date date) {
        loadWeeklyNutrition(date);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.clearCache();
    }

    public void clearCache() {
        //Clear the cache in the repository
        if (repository != null) {
            repository.clearCache();
        }
    }
}