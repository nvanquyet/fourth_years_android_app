package com.example.android_exam.data.api;

import android.util.Log;

import okhttp3.*;
import com.example.android_exam.data.dto.response.ApiResponse;
import com.example.android_exam.data.models.enums.IngredientCategory;
import com.example.android_exam.data.models.enums.IngredientUnit;
import com.example.android_exam.utils.ISODateAdapter;
import com.example.android_exam.utils.IngredientCategoryDeserializer;
import com.example.android_exam.utils.IngredientUnitDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BaseApiClient {
    protected static final String BASE_URL = "http://20.6.33.23:80/api/";
    private static final int TIMEOUT_SECONDS = 30;

    private OkHttpClient httpClient;
    protected Gson gson;
    private String authToken;

    public BaseApiClient() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(IngredientCategory.class, new IngredientCategoryDeserializer())
                .registerTypeAdapter(IngredientUnit.class, new IngredientUnitDeserializer())
                .registerTypeAdapter(Date.class, new ISODateAdapter())
                .create();
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public String getAuthToken() {
        return authToken;
    }

    // Tạo request builder cơ bản với headers
    protected Request.Builder createRequestBuilder(String endpoint) {
        Request.Builder builder = new Request.Builder()
                .url(BASE_URL + endpoint);

        if (authToken != null && !authToken.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + authToken);
        }

        return builder;
    }

    // Tạo JSON request body
    protected RequestBody createJsonRequestBody(Object data) {
        String json = gson.toJson(data);
        return RequestBody.create(json, MediaType.parse("application/json"));
    }

    // Tạo multipart request body
    protected RequestBody createMultipartRequestBody(Map<String, String> formFields, File file, String fileFieldName) {
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        // Add form fields
        if (formFields != null) {
            for (Map.Entry<String, String> entry : formFields.entrySet()) {
                if (entry.getValue() != null) {
                    multipartBuilder.addFormDataPart(entry.getKey(), entry.getValue());
                }
            }
        }

        // Add file if exists
        if (file != null && fileFieldName != null) {
            RequestBody fileBody = RequestBody.create(file, MediaType.parse("image/*"));
            multipartBuilder.addFormDataPart(fileFieldName, file.getName(), fileBody);
        }

        return multipartBuilder.build();
    }

    // Tạo JSON request
    protected Request createJsonRequest(String endpoint, String method, Object data) {
        Request.Builder builder = createRequestBuilder(endpoint)
                .addHeader("Content-Type", "application/json");

        RequestBody body = null;
        if (data != null) {
            body = createJsonRequestBody(data);
        }

        switch (method.toUpperCase()) {
            case "GET":
                return builder.get().build();
            case "POST":
                return builder.post(body != null ? body : RequestBody.create("", null)).build();
            case "PUT":
                return builder.put(body != null ? body : RequestBody.create("", null)).build();
            case "DELETE":
                return builder.delete(body).build();
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
    }

    // Tạo multipart request
    protected Request createMultipartRequest(String endpoint, String method, Map<String, String> formFields, File file, String fileFieldName) {
        Request.Builder builder = createRequestBuilder(endpoint);
        RequestBody body = createMultipartRequestBody(formFields, file, fileFieldName);

        switch (method.toUpperCase()) {
            case "POST":
                return builder.post(body).build();
            case "PUT":
                return builder.put(body).build();
            default:
                throw new IllegalArgumentException("Unsupported HTTP method for multipart: " + method);
        }
    }

    // Execute request với callback
    protected <T> void executeRequest(Request request, TypeToken<T> responseType, ResponseCallback<T> callback) {
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {

                    String responseBody = response.body().string();

                    // ===== THÊM CÁC LOG DEBUG =====
                    Log.d("API_DEBUG", "==========================================");
                    Log.d("API_DEBUG", "Request URL: " + request.url());
                    Log.d("API_DEBUG", "Response Code: " + response.code());
                    Log.d("API_DEBUG", "Response Message: " + response.message());
                    Log.d("API_DEBUG", "Content-Type: " + response.header("Content-Type"));
                    Log.d("API_DEBUG", "Response Body: " + responseBody);
                    Log.d("API_DEBUG", "Response Body Length: " + responseBody.length());
                    Log.d("API_DEBUG", "Is Successful: " + response.isSuccessful());

                    // Kiểm tra định dạng response
                    if (responseBody.trim().startsWith("{")) {
                        Log.d("API_DEBUG", "Response format: JSON Object");
                    } else if (responseBody.trim().startsWith("[")) {
                        Log.d("API_DEBUG", "Response format: JSON Array");
                    } else if (responseBody.trim().startsWith("\"")) {
                        Log.d("API_DEBUG", "Response format: JSON String");
                    } else if (responseBody.trim().startsWith("<")) {
                        Log.d("API_DEBUG", "Response format: HTML/XML");
                    } else {
                        Log.d("API_DEBUG", "Response format: Plain Text");
                    }
                    Log.d("API_DEBUG", "==========================================");

                    if (response.isSuccessful()) {
                        T result = gson.fromJson(responseBody, responseType);
                        callback.onSuccess(result);
                    } else {
                        ApiResponse<Object> errorResponse = gson.fromJson(responseBody, ApiResponse.class);
                        String errorMessage = errorResponse != null ? errorResponse.getMessage() : "Unknown error";
                        callback.onError(errorMessage);
                    }
                } catch (Exception e) {
                    callback.onFailure(e);
                }
            }
        });
    }

    // Các phương thức tiện ích để tạo request nhanh
    protected <T> void executeJsonRequest(String endpoint, String method, Object data, TypeToken<T> responseType, ResponseCallback<T> callback) {
        Request request = createJsonRequest(endpoint, method, data);
        executeRequest(request, responseType, callback);
    }

    protected <T> void executeMultipartRequest(String endpoint, String method, Map<String, String> formFields, File file, String fileFieldName, TypeToken<T> responseType, ResponseCallback<T> callback) {
        Request request = createMultipartRequest(endpoint, method, formFields, file, fileFieldName);
        executeRequest(request, responseType, callback);
    }

    // Các phương thức GET, POST, PUT, DELETE đơn giản
    protected <T> void get(String endpoint, TypeToken<T> responseType, ResponseCallback<T> callback) {
        executeJsonRequest(endpoint, "GET", null, responseType, callback);
    }

    protected <T> void post(String endpoint, Object data, TypeToken<T> responseType, ResponseCallback<T> callback) {
        executeJsonRequest(endpoint, "POST", data, responseType, callback);
    }

    protected <T> void put(String endpoint, Object data, TypeToken<T> responseType, ResponseCallback<T> callback) {
        executeJsonRequest(endpoint, "PUT", data, responseType, callback);
    }

    protected <T> void delete(String endpoint, TypeToken<T> responseType, ResponseCallback<T> callback) {
        executeJsonRequest(endpoint, "DELETE", null, responseType, callback);
    }

    protected <T> void delete(String endpoint, Object data, TypeToken<T> responseType, ResponseCallback<T> callback) {
        executeJsonRequest(endpoint, "DELETE", data, responseType, callback);
    }

    protected <T> void postMultipart(String endpoint, Map<String, String> formFields, File file, String fileFieldName, TypeToken<T> responseType, ResponseCallback<T> callback) {
        executeMultipartRequest(endpoint, "POST", formFields, file, fileFieldName, responseType, callback);
    }

    protected <T> void putMultipart(String endpoint, Map<String, String> formFields, File file, String fileFieldName, TypeToken<T> responseType, ResponseCallback<T> callback) {
        executeMultipartRequest(endpoint, "PUT", formFields, file, fileFieldName, responseType, callback);
    }
}