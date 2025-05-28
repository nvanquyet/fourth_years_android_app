package com.example.android_exam.data.remote.api;

import android.content.Context;
import com.example.android_exam.R;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AiService {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public static void suggestMeal(Context context, String ingredientList, Callback callback) {
        if (context == null || ingredientList == null || callback == null) {
            throw new IllegalArgumentException("Context, ingredientList, và callback không được null");
        }

        OkHttpClient client = new OkHttpClient();
        String API_KEY = context.getString(R.string.open_ai_key); // Lấy API key từ resources

        try {
            // Tạo JSON cho tin nhắn
            JSONObject message = new JSONObject();
            message.put("role", "user");
            message.put("content", "Tôi có các nguyên liệu sau: " + ingredientList +
                    ". Gợi ý món ăn đơn giản, bao gồm thông tin calo và cách làm.");

            // Tạo body yêu cầu
            JSONObject body = new JSONObject();
            body.put("model", "gpt-3.5-turbo");
            body.put("messages", new JSONArray().put(message));

            // Tạo request body cho OkHttp
            RequestBody requestBody = RequestBody.create(
                    body.toString(), MediaType.get("application/json; charset=utf-8"));

            // Xây dựng yêu cầu HTTP
            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .post(requestBody)
                    .build();

            // Thực thi yêu cầu bất đồng bộ
            client.newCall(request).enqueue(callback);

        } catch (JSONException e) {
            e.printStackTrace();
            callback.onFailure(null, new IOException("Lỗi tạo JSON: " + e.getMessage()));
        }
    }
}