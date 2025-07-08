package com.example.android_exam.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// Thêm helper method để chuyển URI thành File
public class FileUtils {

    /**
     * Chuyển đổi URI thành File object
     * @param context Context
     * @param uri URI của ảnh
     * @return File object hoặc null nếu thất bại
     */
    public static File getFileFromUri(Context context, Uri uri) {
        if (uri == null) return null;

        try {
            // Tạo file tạm thời
            File tempFile = createTempImageFile(context);

            // Copy nội dung từ URI vào file tạm thời
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) return null;

            FileOutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            return tempFile;

        } catch (IOException e) {
            Log.e("FileUtils", "Error converting URI to File: " + e.getMessage());
            return null;
        }
    }

    /**
     * Tạo file tạm thời cho ảnh
     */
    private static File createTempImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getCacheDir(); // Sử dụng cache directory

        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    /**
     * Lấy tên file từ URI
     */
    public static String getFileNameFromUri(Context context, Uri uri) {
        String fileName = "image.jpg"; // Default name

        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0) {
                    fileName = cursor.getString(nameIndex);
                }
                cursor.close();
            }
        } else if (uri.getScheme().equals("file")) {
            fileName = new File(uri.getPath()).getName();
        }

        return fileName;
    }

    /**
     * Xóa file tạm thời sau khi sử dụng
     */
    public static void deleteTempFile(File file) {
        if (file != null && file.exists()) {
            boolean deleted = file.delete();
            Log.d("FileUtils", "Temp file deleted: " + deleted);
        }
    }
}