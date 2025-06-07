package com.example.android_exam.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.android_exam.data.local.database.AppDatabase;
import com.example.android_exam.data.local.entity.Meal;
import com.example.android_exam.data.remote.DataRepository;
import com.example.android_exam.data.remote.LocalDataRepository;

public class RecipeDetailViewModel extends AndroidViewModel {

    private MutableLiveData<String> successMessage = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public RecipeDetailViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<String> getSuccessMessage() {
        return successMessage;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void cookMeal(Meal meal) {
        LocalDataRepository.getInstance().addMeal(meal, new DataRepository.AuthCallback<Meal>() {
            @Override
            public void onSuccess(Meal result) {
                successMessage.postValue("Meal recorded successfully!");
            }

            @Override
            public void onError(String error) {
                errorMessage.postValue("Error recording meal: " + error);
            }
        });
    }
}