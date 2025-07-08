package com.example.android_exam.module.image;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import okhttp3.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class ImageUploadUtil {

    private static final String TAG = "ImageUploadUtil";
    private static final int TIMEOUT_SECONDS = 30;

    private OkHttpClient client;
    private Context context;

    public interface UploadCallback {
        void onSuccess(String response);
        void onError(String error);
        void onProgress(int progress);
    }

    public ImageUploadUtil(Context context) {
        this.context = context;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build();
    }

    public void uploadImage(Uri imageUri, String apiUrl, UploadCallback callback) {
        new Thread(() -> {
            try {
                File imageFile = getFileFromUri(imageUri);
                if (imageFile == null) {
                    callback.onError("Cannot get file from URI");
                    return;
                }

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("Image", imageFile.getName(),
                                RequestBody.create(imageFile, MediaType.parse("image/*")))
                        .build();

                Request request = new Request.Builder()
                        .url(apiUrl)
                        .post(requestBody)
                        .addHeader("Content-Type", "multipart/form-data")
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        callback.onSuccess(responseBody);
                    } else {
                        callback.onError("Upload failed: " + response.code() + " - " + response.message());
                    }
                }

            } catch (IOException e) {
                Log.e(TAG, "Upload error", e);
                callback.onError("Upload error: " + e.getMessage());
            }
        }).start();
    }

    private File getFileFromUri(Uri uri) {
        try {
            // Nếu là file URI trực tiếp
            if ("file".equals(uri.getScheme())) {
                return new File(uri.getPath());
            }

            // Nếu là content URI, copy về cache
            String fileName = getFileName(uri);
            if (fileName == null) {
                fileName = "temp_image_" + System.currentTimeMillis() + ".jpg";
            }

            File tempFile = new File(context.getCacheDir(), fileName);

            try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
                 FileOutputStream outputStream = new FileOutputStream(tempFile)) {

                if (inputStream == null) {
                    return null;
                }

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                return tempFile;
            }

        } catch (IOException e) {
            Log.e(TAG, "Error getting file from URI", e);
            return null;
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }

        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    // Phương thức upload với authentication header
    public void uploadImageWithAuth(Uri imageUri, String apiUrl, String authToken, UploadCallback callback) {
        new Thread(() -> {
            try {
                File imageFile = getFileFromUri(imageUri);
                if (imageFile == null) {
                    callback.onError("Cannot get file from URI");
                    return;
                }

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("Image", imageFile.getName(),
                                RequestBody.create(imageFile, MediaType.parse("image/*")))
                        .build();

                Request request = new Request.Builder()
                        .url(apiUrl)
                        .post(requestBody)
                        .addHeader("Authorization", "Bearer " + authToken)
                        .addHeader("Content-Type", "multipart/form-data")
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful()) {
                        String responseBody = response.body() != null ? response.body().string() : "";
                        callback.onSuccess(responseBody);
                    } else {
                        callback.onError("Upload failed: " + response.code() + " - " + response.message());
                    }
                }

            } catch (IOException e) {
                Log.e(TAG, "Upload error", e);
                callback.onError("Upload error: " + e.getMessage());
            }
        }).start();
    }
}