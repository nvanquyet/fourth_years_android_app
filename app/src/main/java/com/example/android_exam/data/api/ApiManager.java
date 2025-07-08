package com.example.android_exam.data.api;


import com.example.android_exam.data.models.enums.IngredientCategory;
import com.example.android_exam.data.models.enums.IngredientUnit;
import com.example.android_exam.utils.IngredientCategoryDeserializer;
import com.example.android_exam.utils.IngredientUnitDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ApiManager {
    private static ApiManager instance;

    private AuthApiClient authClient;
    private FoodApiClient foodClient;
    private AiClient aiClient;
    private IngredientApiClient ingredientClient;
    private NutritionApiClient nutritionClient;
    private ApiManager() {
        authClient = new AuthApiClient();
        foodClient = new FoodApiClient();
        ingredientClient = new IngredientApiClient();
        nutritionClient = new NutritionApiClient();
        aiClient = new AiClient();

    }
    public static synchronized ApiManager getInstance() {
        if (instance == null) {
            instance = new ApiManager();
        }
        return instance;
    }

    public void setAuthToken(String token) {
        authClient.setAuthToken(token);
        foodClient.setAuthToken(token);
        ingredientClient.setAuthToken(token);
        nutritionClient.setAuthToken(token);
        aiClient.setAuthToken(token);
    }

    public AuthApiClient getAuthClient() {
        return authClient;
    }

    public FoodApiClient getFoodClient() {
        return foodClient;
    }

    public IngredientApiClient getIngredientClient() {
        return ingredientClient;
    }

    public NutritionApiClient getNutritionClient() {
        return nutritionClient;
    }
    public AiClient getAiClient() {
        return aiClient;
    }
}