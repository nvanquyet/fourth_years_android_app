package com.example.android_exam.data.api;

import com.example.android_exam.data.dto.AuthResponseDto;
import com.example.android_exam.data.dto.response.ApiResponse;
import com.example.android_exam.data.dto.user.ChangePasswordDto;
import com.example.android_exam.data.dto.user.LoginDto;
import com.example.android_exam.data.dto.user.RegisterDto;
import com.example.android_exam.data.dto.user.UserProfileDto;
import com.google.gson.reflect.TypeToken;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AuthApiClient extends BaseApiClient {

    public void login(LoginDto loginDto, AuthCallback<ApiResponse<AuthResponseDto>> callback) {
        RequestBody requestBody = createJsonRequestBody(loginDto);

        Request request = createRequestBuilder("auth/login")
                .post(requestBody)
                .build();

        executeRequest(request, new TypeToken<ApiResponse<AuthResponseDto>>() {}, callback);
    }

    public void register(RegisterDto registerDto, AuthCallback<ApiResponse<AuthResponseDto>> callback) {
        RequestBody requestBody = createJsonRequestBody(registerDto);

        Request request = createRequestBuilder("auth/register")
                .post(requestBody)
                .build();

        executeRequest(request, new TypeToken<ApiResponse<AuthResponseDto>>(){} , callback);
    }

    public void validateToken(String token, AuthCallback<ApiResponse<UserProfileDto>> callback) {
        if (token == null || token.trim().isEmpty()) {
            callback.onFailure(new Throwable("Token is required"));
            return;
        }

        // Tạo request với token trong query parameter
        Request request = createRequestBuilder("auth/validateToken")
                .url(createRequestBuilder("auth/validateToken").build().url().newBuilder()
                        .addQueryParameter("token", token)
                        .build())
                .get()
                .build();

        // Execute request
        executeRequest(request, new TypeToken<ApiResponse<UserProfileDto>>(){}, callback);
    }

    public void logout(AuthCallback<ApiResponse<Boolean>> callback) {
        Request request = createRequestBuilder("auth/logout")
                .post(RequestBody.create("", MediaType.parse("application/json")))
                .build();

        //Change to ResponseCallback
        executeRequest(request, new TypeToken<ApiResponse<Boolean>>(){}, callback);
    }

    public void getUserProfile(AuthCallback<ApiResponse<UserProfileDto>> callback) {
        Request request = createRequestBuilder("auth/me")
                .get()
                .build();

        executeRequest(request, new TypeToken<ApiResponse<UserProfileDto>>(){}, callback);
    }

    public void updateUserProfile(UserProfileDto userProfileDto, AuthCallback<ApiResponse<UserProfileDto>> callback) {
        executeJsonRequest("auth/me", "PUT", userProfileDto, new TypeToken<ApiResponse<UserProfileDto>>(){}, callback);
    }

    public void changePassword(ChangePasswordDto changePasswordDto, AuthCallback<ApiResponse<Object>> callback) {
        RequestBody requestBody = createJsonRequestBody(changePasswordDto);

        Request request = createRequestBuilder("auth/change_password")
                .put(requestBody)
                .build();

        executeRequest(request, new TypeToken<ApiResponse<Object>>(){}, callback);
    }
}