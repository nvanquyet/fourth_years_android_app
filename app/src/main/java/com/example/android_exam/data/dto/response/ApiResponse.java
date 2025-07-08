package com.example.android_exam.data.dto.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class ApiResponse<T> {
    @SerializedName("success")
    private boolean success;
    @SerializedName("data")
    private T data;
    @SerializedName("message")
    private String message = "";
    @SerializedName("metadata")
    private Map<String, List<String>> metadata;

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Map<String, List<String>> getMetadata() { return metadata; }
    public void setMetadata(Map<String, List<String>> metadata) { this.metadata = metadata; }
}
