package com.example.android_exam.base.abstraction;

import com.example.android_exam.data.dto.AuthRequest;
import com.example.android_exam.data.dto.AuthResponse;
import com.example.android_exam.data.dto.UserResponse;
import com.example.android_exam.data.models.Ingredient;
import com.example.android_exam.data.remote.DataRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    // User Authentication
    @POST("auth/login")
    Call<AuthResponse> login(@Body AuthRequest request);

    @POST("auth/register")
    Call<AuthResponse> register(@Body AuthRequest request);

    @POST("auth/me")
    Call<UserResponse> getUserInformation();

    @POST("auth/logout")
    Call<Void> logout();



    //Ingredients
    // Get all ingredients
    @GET("ingredients")
    Call<List<Ingredient>> getAllIngredients();

    // Get ingredient by ID
    @GET("ingredient/{id}")
    Call<Ingredient> getIngredientById(@Path("id") int id);

    //Get expiring ingredients with limit
    @GET("ingredients/expiring/{limit}")
    Call<List<Ingredient>> getExpiringIngredients(@Path("limit") int limit);

    //Post new ingredient
    @POST("ingredient")
    Call<Ingredient> addIngredient(@Body Ingredient ingredient);

    //Update ingredient
    @PUT("ingredient/{id}")
    Call<Void> updateIngredient(@Path("id") int id, @Body Ingredient ingredient);

    //Delete ingredient
    @PUT("ingredient/{id}/delete")
    Call<Void> deleteIngredient(@Path("id") int id);
}
