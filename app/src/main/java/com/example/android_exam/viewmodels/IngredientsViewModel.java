package com.example.android_exam.viewmodels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.android_exam.data.local.database.AppDatabase;
import com.example.android_exam.data.local.entity.Ingredient;
import com.example.android_exam.data.remote.LocalDataRepository;
import com.example.android_exam.data.remote.DataRepository;

import java.util.ArrayList;
import java.util.List;

public class IngredientsViewModel extends AndroidViewModel {

    private MutableLiveData<List<Ingredient>> ingredients = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public IngredientsViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Ingredient>> getIngredients() {
        return ingredients;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadIngredients(int userId) {
        LocalDataRepository.getInstance().getAllIngredients(userId, new DataRepository.AuthCallback<List<Ingredient>>() {
            @Override
            public void onSuccess(List<Ingredient> result) {
                ingredients.postValue(result != null ? result : new ArrayList<>());
            }

            @Override
            public void onError(String error) {
                errorMessage.postValue("Error loading ingredients: " + error);
                ingredients.postValue(new ArrayList<>());
            }
        });
    }

    public void addIngredient(Ingredient ingredient) {
        LocalDataRepository.getInstance().addIngredient(ingredient, new DataRepository.AuthCallback<Ingredient>() {
            @Override
            public void onSuccess(Ingredient result) {
                loadIngredients(ingredient.userId);
            }

            @Override
            public void onError(String error) {
                errorMessage.postValue("Error adding ingredient: " + error);
            }
        });
    }

    public void updateIngredient(Ingredient ingredient) {
        LocalDataRepository.getInstance().updateIngredient(ingredient, new DataRepository.AuthCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    loadIngredients(ingredient.userId);
                } else {
                    errorMessage.postValue("Failed to update ingredient");
                }
            }

            @Override
            public void onError(String error) {
                errorMessage.postValue("Error updating ingredient: " + error);
            }
        });
    }

    public void deleteIngredient(Ingredient ingredient) {
        LocalDataRepository.getInstance().deleteIngredient(ingredient.id, new DataRepository.AuthCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    loadIngredients(ingredient.userId);
                } else {
                    errorMessage.postValue("Failed to delete ingredient");
                }
            }

            @Override
            public void onError(String error) {
                errorMessage.postValue("Error deleting ingredient: " + error);
            }
        });
    }
}