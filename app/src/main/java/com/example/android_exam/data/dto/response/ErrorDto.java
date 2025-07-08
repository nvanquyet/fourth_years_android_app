package com.example.android_exam.data.dto.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class ErrorDto {
    @SerializedName("errorCode")
    private String errorCode = "";
    @SerializedName("errorMessage")
    private String errorMessage = "";
    @SerializedName("validationErrors")
    private Map<String, List<String>> validationErrors;

    // Getters and Setters
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public Map<String, List<String>> getValidationErrors() { return validationErrors; }
    public void setValidationErrors(Map<String, List<String>> validationErrors) { this.validationErrors = validationErrors; }
}
