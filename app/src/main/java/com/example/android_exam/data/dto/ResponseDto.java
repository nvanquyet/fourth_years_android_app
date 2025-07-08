package com.example.android_exam.data.dto;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class ResponseDto<T> {
    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message = "";
    @SerializedName("data")
    private T data;
    @SerializedName("errors")
    private List<String> errors = new ArrayList<>();

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}