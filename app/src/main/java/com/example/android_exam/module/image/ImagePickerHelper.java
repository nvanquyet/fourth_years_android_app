package com.example.android_exam.module.image;

import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Map;

public class ImagePickerHelper {

    private static ImagePickerHelper instance;
    private final Map<String, ImagePickerModule> moduleMap;

    private ImagePickerHelper() {
        moduleMap = new HashMap<>();
    }

    public static ImagePickerHelper getInstance() {
        if (instance == null) {
            instance = new ImagePickerHelper();
        }
        return instance;
    }

    // Phương thức để khởi tạo module sớm (gọi trong onCreate hoặc SplashActivity)
    public static void initialize(AppCompatActivity activity) {
        getInstance().createModule(activity);
    }

    // Phương thức static để sử dụng dễ dàng
    public static void pickImage(AppCompatActivity activity, ImagePickerModule.ImagePickerCallback callback) {
        getInstance().openImagePicker(activity, callback);
    }

    // Phương thức static với lambda support
    public static void pickImage(AppCompatActivity activity, OnImageSelected onImageSelected) {
        getInstance().openImagePicker(activity, new ImagePickerModule.ImagePickerCallback() {
            @Override
            public void onImagePicked(Uri imageUri) {
                onImageSelected.onSelected(imageUri);
            }

            @Override
            public void onError(String error) {
                onImageSelected.onError(error);
            }
        });
    }

    // Phương thức static chỉ với success callback
    public static void pickImage(AppCompatActivity activity, OnImagePickedSuccess onSuccess) {
        getInstance().openImagePicker(activity, new ImagePickerModule.ImagePickerCallback() {
            @Override
            public void onImagePicked(Uri imageUri) {
                onSuccess.onSuccess(imageUri);
            }

            @Override
            public void onError(String error) {
                // Default error handling
                android.widget.Toast.makeText(activity, "Lỗi: " + error, android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Phương thức để tạo module sớm
    private void createModule(AppCompatActivity activity) {
        String activityKey = activity.getClass().getName() + "@" + activity.hashCode();

        if (!moduleMap.containsKey(activityKey)) {
            ImagePickerModule module = new ImagePickerModule(activity);
            moduleMap.put(activityKey, module);
        }
    }

    private void openImagePicker(AppCompatActivity activity, ImagePickerModule.ImagePickerCallback callback) {
        String activityKey = activity.getClass().getName() + "@" + activity.hashCode();

        ImagePickerModule module = moduleMap.get(activityKey);
        if (module == null) {
            // Tự động tạo module nếu chưa được khởi tạo
            module = new ImagePickerModule(activity);
            moduleMap.put(activityKey, module);
        }

        module.openImagePicker(callback);
    }

    // Phương thức để xử lý permission result
    public static void handlePermissionResult(AppCompatActivity activity, int requestCode, String[] permissions, int[] grantResults) {
        getInstance().onRequestPermissionsResult(activity, requestCode, permissions, grantResults);
    }

    private void onRequestPermissionsResult(AppCompatActivity activity, int requestCode, String[] permissions, int[] grantResults) {
        String activityKey = activity.getClass().getName() + "@" + activity.hashCode();
        ImagePickerModule module = moduleMap.get(activityKey);
        if (module != null) {
            module.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // Cleanup method để gọi khi Activity bị destroy
    public static void cleanup(AppCompatActivity activity) {
        getInstance().removeActivity(activity);
    }

    private void removeActivity(AppCompatActivity activity) {
        String activityKey = activity.getClass().getName() + "@" + activity.hashCode();
        moduleMap.remove(activityKey);
    }

    // Phương thức để kiểm tra xem module đã được khởi tạo chưa
    public static boolean isInitialized(AppCompatActivity activity) {
        String activityKey = activity.getClass().getName() + "@" + activity.hashCode();
        return getInstance().moduleMap.containsKey(activityKey);
    }

    // Phương thức để khởi tạo cho nhiều activity cùng lúc (gọi từ SplashActivity)
    public static void preInitialize(AppCompatActivity... activities) {
        for (AppCompatActivity activity : activities) {
            initialize(activity);
        }
    }

    public interface OnImageSelected {
        void onSelected(Uri imageUri);
        void onError(String error);
    }

    public interface OnImagePickedSuccess {
        void onSuccess(Uri imageUri);
    }
}