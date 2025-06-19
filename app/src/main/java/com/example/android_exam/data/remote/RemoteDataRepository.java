package com.example.android_exam.data.remote;

import android.content.Context;
import android.os.Build;

import com.example.android_exam.base.abstraction.ApiService;
import com.example.android_exam.data.dto.AuthRequest;
import com.example.android_exam.data.dto.AuthResponse;
import com.example.android_exam.data.dto.UserResponse;
import com.example.android_exam.data.models.Ingredient;
import com.example.android_exam.data.models.User;
import com.example.android_exam.utils.SessionManager;

import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Remote API implementation of DataRepository
 * This handles all API calls for data operations
 */
public class RemoteDataRepository implements DataRepository {
    private final Context context;
    private final ExecutorService executor;
    private final ApiService apiService;
    private static volatile RemoteDataRepository INSTANCE;

    private RemoteDataRepository(Context context) {
        this.context = context.getApplicationContext();
        this.executor = Executors.newFixedThreadPool(4);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    String token = SessionManager.getToken(context);
                    if (token != null) {
                        return chain.proceed(chain.request().newBuilder()
                                .header("Authorization", "Bearer " + token)
                                .build());
                    }
                    return chain.proceed(chain.request());
                })
                .build();
        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://52.187.131.203:80/") // Replace with your API base URL
                .client(client) // Use OkHttp client with interceptor
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.apiService = retrofit.create(ApiService.class);
    }

    public static RemoteDataRepository getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (RemoteDataRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new RemoteDataRepository(context);
                }
            }
        }
        return INSTANCE;
    }

    public static RemoteDataRepository getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        throw new IllegalStateException(
                "Repository not initialized. Call getInstance(Context) first."
        );
    }


    // ========== USER AUTHENTICATION ==========
    @Override
    public void login(String username, String password, AuthCallback<User> callback) {
        executor.execute(() -> {
            try {
                if (isNullOrEmpty(username)) {
                    callback.onError("Username cannot be empty");
                    return;
                }
                if (isNullOrEmpty(password)) {
                    callback.onError("Password cannot be empty");
                    return;
                }

                // AuthRequest constructor is not public, so use a static factory if available, or make constructor public
                AuthRequest request = new AuthRequest(username.trim(), hashPassword(password.trim()));
                apiService.login(request).enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            AuthResponse authResponse = response.body();
                            SessionManager.saveUserSession(context, authResponse.data.user, authResponse.data.token);
                            callback.onSuccess(authResponse.data.user);
                        } else {
                            callback.onError("Invalid username or password");
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        callback.onError("Login failed: " + t.getMessage());
                    }
                });

            } catch (Exception e) {
                callback.onError("Login failed: " + e.getMessage());
            }
        });
    }
    @Override
    public void getUserInformation(String token, AuthCallback<User> callback) {
        executor.execute(() -> {
            try {
                apiService.getUserInformation().enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            UserResponse userResponse = response.body();
                            callback.onSuccess(userResponse.data);
                        } else {
                            callback.onError("Failed to fetch user information");
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponse> call, Throwable t) {
                        callback.onError("Failed to fetch user information: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                callback.onError("Failed to fetch user information: " + e.getMessage());
            }
        });
    }
    @Override
    public void register(String username, String password, String email, AuthCallback<User> callback) {
        executor.execute(() -> {
            try {
                if (isNullOrEmpty(username)) {
                    callback.onError("Username cannot be empty");
                    return;
                }
                if (email == null || email.length() < 6) {
                    callback.onError("Email cannot be empty");
                    return;
                }

                if (password == null || password.length() < 6) {
                    callback.onError("Password must be at least 6 characters");
                    return;
                }

                AuthRequest request = new AuthRequest(username.trim(), hashPassword(password.trim()), email.trim());
                apiService.register(request).enqueue(new Callback<AuthResponse>() {
                    @Override
                    public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            AuthResponse authResponse = response.body();
                            callback.onSuccess(authResponse.data.user);
                        } else {
                            callback.onError("Username already exists or registration failed");
                        }
                    }

                    @Override
                    public void onFailure(Call<AuthResponse> call, Throwable t) {
                        callback.onError("Registration failed: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                callback.onError("Registration failed: " + e.getMessage());
            }
        });
    }
    @Override
    public void logout(AuthCallback<Boolean> callback) {
        executor.execute(() -> {
            try {
                apiService.logout().enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        SessionManager.clearSession(context);
                        callback.onSuccess(true);
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        callback.onError("Logout failed: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                callback.onError("Logout failed: " + e.getMessage());
            }
        });
    }

    // ========== INGREDIENTS MANAGEMENT ==========
    @Override
    public void getAllIngredients(AuthCallback<List<Ingredient>> callback) {
        executor.execute(() -> {
            try {
                String token = SessionManager.getToken(context);
                if (isNullOrEmpty(token)) {
                    callback.onError("Người dùng chưa đăng nhập");
                    return;
                }
                apiService.getAllIngredients().enqueue(new Callback<List<Ingredient>>() {
                    @Override
                    public void onResponse(Call<List<Ingredient>> call, Response<List<Ingredient>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            if (response.code() == 401) {
                                // Token không hợp lệ, xóa phiên
                                SessionManager.clearSession(context);
                                callback.onError("Phiên đăng nhập không hợp lệ, vui lòng đăng nhập lại");
                            } else {
                                callback.onError("Không thể lấy danh sách nguyên liệu: " +
                                        (response.body() == null ? "Dữ liệu trống" : response.message()));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Ingredient>> call, Throwable t) {
                        callback.onError("Không thể lấy danh sách nguyên liệu: " + t.getMessage());
                    }
                });
            }
            catch (Exception e) {
                callback.onError("Failed to fetch ingredients: " + e.getMessage());
            }
        });
    }

    @Override
    public void getIngredientById(int id, AuthCallback<Ingredient> callback) {
        executor.execute(() -> {
            try {
                String token = SessionManager.getToken(context);
                if (isNullOrEmpty(token)) {
                    callback.onError("Người dùng chưa đăng nhập");
                    return;
                }
                if (id <= 0) {
                    callback.onError("ID nguyên liệu không hợp lệ");
                    return;
                }

                apiService.getIngredientById(id).enqueue(new Callback<Ingredient>() {
                    @Override
                    public void onResponse(Call<Ingredient> call, Response<Ingredient> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            String errorMessage;
                            if (response.code() == 401) {
                                SessionManager.clearSession(context);
                                errorMessage = "Phiên đăng nhập không hợp lệ, vui lòng đăng nhập lại";
                            } else if (response.code() == 404) {
                                errorMessage = "Không tìm thấy nguyên liệu";
                            } else {
                                errorMessage = "Không thể lấy thông tin nguyên liệu: " + response.message();
                            }
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<Ingredient> call, Throwable t) {
                        callback.onError("Không thể lấy thông tin nguyên liệu: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                callback.onError("Không thể lấy thông tin nguyên liệu: " + e.getMessage());
            }
        });
    }

    @Override
    public void getFilteredIngredients(String filter, AuthCallback<List<Ingredient>> callback) {
        executor.execute(() -> {
            try {
                if (validateUserId()) {
                    callback.onError("User not logged in");
                    return;
                }
                if (isNullOrEmpty(filter)) {
                    callback.onError("Filter cannot be empty");
                    return;
                }

                apiService.getFilteredIngredients(filter.trim()).enqueue(new Callback<List<Ingredient>>() {
                    @Override
                    public void onResponse(Call<List<Ingredient>> call, Response<List<Ingredient>> response) {
                        callback.onSuccess(response.isSuccessful() ? response.body() : new ArrayList<>());
                    }

                    @Override
                    public void onFailure(Call<List<Ingredient>> call, Throwable t) {
                        callback.onError("Failed to fetch filtered ingredients: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                callback.onError("Failed to fetch filtered ingredients: " + e.getMessage());
            }
        });
    }

    @Override
    public void addIngredient(Ingredient ingredient, AuthCallback<Ingredient> callback) {
        executor.execute(() -> {
            try {
                String token = SessionManager.getToken(context);
                if (isNullOrEmpty(token)) {
                    callback.onError("Người dùng chưa đăng nhập");
                    return;
                }

                if (ingredient == null || isNullOrEmpty(ingredient.getName())) {
                    callback.onError("Ingredient name cannot be empty");
                    return;
                }
                if (ingredient.getQuantity() < 0) {
                    callback.onError("Quantity cannot be negative");
                    return;
                }

                ingredient.setName(ingredient.getName().trim());
                apiService.addIngredient(ingredient).enqueue(new Callback<Ingredient>() {
                    @Override
                    public void onResponse(Call<Ingredient> call, Response<Ingredient> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Failed to add ingredient");
                        }
                    }

                    @Override
                    public void onFailure(Call<Ingredient> call, Throwable t) {
                        callback.onError("Failed to add ingredient: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                callback.onError("Failed to add ingredient: " + e.getMessage());
            }
        });
    }

    @Override
    public void updateIngredient(Ingredient ingredient, AuthCallback<Boolean> callback) {
        executor.execute(() -> {
            try {
                if (validateUserId()) {
                    callback.onError("User not logged in");
                    return;
                }
                if (ingredient == null || ingredient.id <= 0) {
                    callback.onError("Invalid ingredient ID");
                    return;
                }
                if (isNullOrEmpty(ingredient.getName())) {
                    callback.onError("Ingredient name cannot be empty");
                    return;
                }
                ingredient.setName(ingredient.getName().trim());
                apiService.updateIngredient(ingredient.getId(), ingredient).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        callback.onSuccess(response.isSuccessful());
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        callback.onError("Failed to update ingredient: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                callback.onError("Failed to update ingredient: " + e.getMessage());
            }
        });
    }

    @Override
    public void deleteIngredient(int ingredientId, AuthCallback<Boolean> callback) {
        executor.execute(() -> {
            try {
                if (validateUserId()) {
                    callback.onError("User not logged in");
                    return;
                }
                if (ingredientId <= 0) {
                    callback.onError("Invalid ingredient ID");
                    return;
                }

                apiService.deleteIngredient(ingredientId).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        callback.onSuccess(response.isSuccessful());
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        callback.onError("Failed to delete ingredient: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                callback.onError("Failed to delete ingredient: " + e.getMessage());
            }
        });
    }

    @Override
    public void refreshIngredients(AuthCallback<List<Ingredient>> callback) {
        getAllIngredients(callback); // Simply re-fetch all ingredients
    }

    @Override
    public void refreshExpiringIngredients(AuthCallback<List<Ingredient>> callback) {
        getExpiringIngredients(10, callback); // Default limit to 10
    }

    @Override
    public void getExpiringIngredients(int limit, AuthCallback<List<Ingredient>> callback) {
        executor.execute(() -> {
            try {
                if (validateUserId()) {
                    callback.onError("User not logged in");
                    return;
                }
                final int finalLimit = Math.max(limit, 1);
                apiService.getExpiringIngredients(finalLimit).enqueue(new Callback<List<Ingredient>>() {
                    @Override
                    public void onResponse(Call<List<Ingredient>> call, Response<List<Ingredient>> response) {
                        callback.onSuccess(response.isSuccessful() ? response.body() : new ArrayList<>());
                    }

                    @Override
                    public void onFailure(Call<List<Ingredient>> call, Throwable t) {
                        callback.onError("Failed to fetch expiring ingredients: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                callback.onError("Failed to fetch expiring ingredients: " + e.getMessage());
            }
        });
    }

    // ========== MEALS MANAGEMENT ==========
    @Override
    public void getAllMeals(AuthCallback<List<Meal>> callback) {
        executor.execute(() -> {
            try {
                if (validateUserId()) {
                    callback.onError("User not logged in");
                    return;
                }
                apiService.getAllMeals().enqueue(new Callback<List<Meal>>() {
                    @Override
                    public void onResponse(Call<List<Meal>> call, Response<List<Meal>> response) {
                        callback.onSuccess(response.isSuccessful() ? response.body() : new ArrayList<>());
                    }

                    @Override
                    public void onFailure(Call<List<Meal>> call, Throwable t) {
                        callback.onError("Failed to fetch meals: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                callback.onError("Failed to fetch meals: " + e.getMessage());
            }
        });
    }

    @Override
    public void addMeal(Meal meal, AuthCallback<Meal> callback) {
        executor.execute(() -> {
            try {
                if (validateUserId()) {
                    callback.onError("User not logged in");
                    return;
                }
                if (meal == null || isNullOrEmpty(meal.getName())) {
                    callback.onError("Meal name cannot be empty");
                    return;
                }

                meal.userId = currentUserId;
                meal.setName(meal.getName().trim());

                // Set meal type based on time if not set
                if (isNullOrEmpty(meal.getMealType())) {
                    long timestamp = meal.getDateTimestamp();
                    if (timestamp == 0) {
                        timestamp = System.currentTimeMillis();
                        meal.setDateTimestamp(timestamp);
                    }
                    int hour = extractHourFromTimestamp(timestamp);
                    meal.setMealType(determineMealType(hour));
                }

                apiService.addMeal(meal).enqueue(new Callback<Meal>() {
                    @Override
                    public void onResponse(Call<Meal> call, Response<Meal> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Failed to add meal");
                        }
                    }

                    @Override
                    public void onFailure(Call<Meal> call, Throwable t) {
                        callback.onError("Failed to add meal: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                callback.onError("Failed to add meal: " + e.getMessage());
            }
        });
    }

    @Override
    public void getMealsByDate(String date, AuthCallback<List<Meal>> callback) {
        executor.execute(() -> {
            try {
                if (validateUserId()) {
                    callback.onError("User not logged in");
                    return;
                }
                if (isNullOrEmpty(date)) {
                    callback.onError("Date cannot be empty");
                    return;
                }

                apiService.getMealsByDate(date.trim()).enqueue(new Callback<List<Meal>>() {
                    @Override
                    public void onResponse(Call<List<Meal>> call, Response<List<Meal>> response) {
                        callback.onSuccess(response.isSuccessful() ? response.body() : new ArrayList<>());
                    }

                    @Override
                    public void onFailure(Call<List<Meal>> call, Throwable t) {
                        callback.onError("Failed to fetch meals by date: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                callback.onError("Failed to fetch meals by date: " + e.getMessage());
            }
        });
    }

    @Override
    public void getFoodsInMeal(int mealId, AuthCallback<List<Food>> callback) {
        executor.execute(() -> {
            try {
                if (mealId <= 0) {
                    callback.onError("Invalid meal ID");
                    return;
                }

                apiService.getFoodsInMeal(mealId).enqueue(new Callback<List<Food>>() {
                    @Override
                    public void onResponse(Call<List<Food>> call, Response<List<Food>> response) {
                        callback.onSuccess(response.isSuccessful() ? response.body() : new ArrayList<>());
                    }

                    @Override
                    public void onFailure(Call<List<Food>> call, Throwable t) {
                        callback.onError("Failed to fetch foods in meal: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                callback.onError("Failed to fetch foods in meal: " + e.getMessage());
            }
        });
    }

    @Override
    public void getTotalCaloriesByDate(String date, AuthCallback<Double> callback) {
        executor.execute(() -> {
            try {
                if (validateUserId()) {
                    callback.onError("User not logged in");
                    return;
                }
                if (isNullOrEmpty(date)) {
                    callback.onError("Date cannot be empty");
                    return;
                }

                apiService.getTotalCaloriesByDate(date.trim()).enqueue(new Callback<Double>() {
                    @Override
                    public void onResponse(Call<Double> call, Response<Double> response) {
                        callback.onSuccess(response.isSuccessful() && response.body() != null ? response.body() : 0.0);
                    }

                    @Override
                    public void onFailure(Call<Double> call, Throwable t) {
                        callback.onError("Failed to calculate total calories: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                callback.onError("Failed to calculate total calories: " + e.getMessage());
            }
        });
    }

    // ========== FOOD MANAGEMENT ==========
    @Override
    public void addFood(Food food, AuthCallback<Food> callback) {
        executor.execute(() -> {
            try {
                if (food == null || isNullOrEmpty(food.getName())) {
                    callback.onError("Food name cannot be empty");
                    return;
                }
                if (food.totalCalories < 0) {
                    callback.onError("Calories cannot be negative");
                    return;
                }
                if (food.mealId <= 0) {
                    callback.onError("Invalid meal ID");
                    return;
                }

                food.setName(food.getName().trim());
                apiService.addFood(food).enqueue(new Callback<Food>() {
                    @Override
                    public void onResponse(Call<Food> call, Response<Food> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Failed to add food");
                        }
                    }

                    @Override
                    public void onFailure(Call<Food> call, Throwable t) {
                        callback.onError("Failed to add food: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                callback.onError("Failed to add food: " + e.getMessage());
            }
        });
    }

    @Override
    public void updateFood(Food food, AuthCallback<Boolean> callback) {
        executor.execute(() -> {
            try {
                if (food == null || food.id <= 0) {
                    callback.onError("Invalid food ID");
                    return;
                }
                if (isNullOrEmpty(food.getName())) {
                    callback.onError("Food name cannot be empty");
                    return;
                }
                if (food.totalCalories < 0) {
                    callback.onError("Calories cannot be negative");
                    return;
                }

                food.setName(food.getName().trim());
                apiService.updateFood(food.id, food).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        callback.onSuccess(response.isSuccessful());
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        callback.onError("Failed to update food: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                callback.onError("Failed to update food: " + e.getMessage());
            }
        });
    }

    @Override
    public void getFoodsByMealId(int mealId, AuthCallback<List<Food>> callback) {
        executor.execute(() -> {
            try {
                if (mealId <= 0) {
                    callback.onError("Invalid meal ID");
                    return;
                }

                apiService.getFoodsByMealId(mealId).enqueue(new Callback<List<Food>>() {
                    @Override
                    public void onResponse(Call<List<Food>> call, Response<List<Food>> response) {
                        callback.onSuccess(response.isSuccessful() ? response.body() : new ArrayList<>());
                    }

                    @Override
                    public void onFailure(Call<List<Food>> call, Throwable t) {
                        callback.onError("Failed to fetch foods by meal ID: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                callback.onError("Failed to fetch foods by meal ID: " + e.getMessage());
            }
        });
    }

    // ========== NUTRITION GOALS ==========
    @Override
    public void getNutritionGoal(AuthCallback<NutritionGoal> callback) {
        executor.execute(() -> {
            try {
                if (validateUserId()) {
                    callback.onError("User not logged in");
                    return;
                }

                apiService.getNutritionGoal().enqueue(new Callback<NutritionGoal>() {
                    @Override
                    public void onResponse(Call<NutritionGoal> call, Response<NutritionGoal> response) {
                        callback.onSuccess(response.isSuccessful() ? response.body() : null);
                    }

                    @Override
                    public void onFailure(Call<NutritionGoal> call, Throwable t) {
                        callback.onError("Failed to fetch nutrition goal: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                callback.onError("Failed to fetch nutrition goal: " + e.getMessage());
            }
        });
    }

    @Override
    public void setNutritionGoal(NutritionGoal goal, AuthCallback<NutritionGoal> callback) {
        executor.execute(() -> {
            try {
                if (validateUserId()) {
                    callback.onError("User not logged in");
                    return;
                }
                if (goal == null) {
                    callback.onError("Nutrition goal cannot be null");
                    return;
                }
                if (goal.dailyCalorieGoal <= 0) {
                    callback.onError("Daily calorie goal must be positive");
                    return;
                }
                if (goal.dailyProteinGoal < 0 || goal.dailyCarbGoal < 0 || goal.dailyFatGoal < 0) {
                    callback.onError("Nutrition goals cannot be negative");
                    return;
                }

                goal.userId = currentUserId;
                if (isNullOrEmpty(goal.getGoalType())) {
                    goal.setGoalType("maintain");
                }

                String goalType = goal.getGoalType().toLowerCase().trim();
                if (!isValidGoalType(goalType)) {
                    callback.onError("Invalid goal type. Must be: weight_loss, muscle_gain, or maintain");
                    return;
                }
                goal.setGoalType(goalType);

                apiService.setNutritionGoal(goal).enqueue(new Callback<NutritionGoal>() {
                    @Override
                    public void onResponse(Call<NutritionGoal> call, Response<NutritionGoal> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Failed to set nutrition goal");
                        }
                    }

                    @Override
                    public void onFailure(Call<NutritionGoal> call, Throwable t) {
                        callback.onError("Failed to set nutrition goal: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                callback.onError("Failed to set nutrition goal: " + e.getMessage());
            }
        });
    }

    // ========== AI SUGGESTIONS ==========
    @Override
    public void getSuggestedMeals(AuthCallback<List<Food>> callback) {
        executor.execute(() -> {
            try {
                if (validateUserId()) {
                    callback.onError("User not logged in");
                    return;
                }

                apiService.getSuggestedMeals().enqueue(new Callback<List<Food>>() {
                    @Override
                    public void onResponse(Call<List<Food>> call, Response<List<Food>> response) {
                        callback.onSuccess(response.isSuccessful() ? response.body() : new ArrayList<>());
                    }

                    @Override
                    public void onFailure(Call<List<Food>> call, Throwable t) {
                        callback.onError("Failed to generate meal suggestions: " + t.getMessage());
                    }
                });
            } catch (Exception e) {
                callback.onError("Failed to generate meal suggestions: " + e.getMessage());
            }
        });
    }

    // ========== HELPER METHODS ==========

    private boolean validateUserId() {
        String token = SessionManager.getToken(context);
        if (isNullOrEmpty(token)) {
            callback.onError("Người dùng chưa đăng nhập");
            return true;
        }
        return false;
    }

    private String determineMealType(int hour) {
        if (hour >= 5 && hour < 11) return "breakfast";
        else if (hour >= 11 && hour < 17) return "lunch";
        else if (hour >= 17 && hour < 22) return "dinner";
        else return "snack";
    }

    private int extractHourFromTimestamp(long timestamp) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Instant.ofEpochMilli(timestamp)
                        .atZone(ZoneId.systemDefault())
                        .getHour();
            }
        } catch (Exception e) {
            // Fallback for older Android versions or errors
        }
        return 12; // Default to noon
    }

    private String hashPassword(String password) {
        // Note: Password hashing should be handled server-side in a production app
        return "hashed_" + password; // Placeholder for demonstration
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private boolean isValidGoalType(String goalType) {
        return "weight_loss".equals(goalType) || "muscle_gain".equals(goalType) || "maintain".equals(goalType);
    }

    // ========== CLEANUP ==========
    public void cleanup() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}