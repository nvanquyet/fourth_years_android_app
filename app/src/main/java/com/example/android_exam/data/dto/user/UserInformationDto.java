package com.example.android_exam.data.dto.user;

import com.example.android_exam.data.models.enums.ActivityLevel;
import com.example.android_exam.data.models.enums.Gender;
import com.example.android_exam.data.models.enums.NutritionGoal;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class UserInformationDto {
    private Gender gender;
    private Date dateOfBirth;
    private Double height;
    private Double weight;
    private Double targetWeight;
    private NutritionGoal primaryNutritionGoal;
    @SerializedName("activityLevel")
    private ActivityLevel activityLevel;

    // Getters and Setters
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }
    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
    public Double getTargetWeight() { return targetWeight; }
    public void setTargetWeight(Double targetWeight) { this.targetWeight = targetWeight; }
    public NutritionGoal getPrimaryNutritionGoal() { return primaryNutritionGoal; }
    public void setPrimaryNutritionGoal(NutritionGoal primaryNutritionGoal) { this.primaryNutritionGoal = primaryNutritionGoal; }
    public ActivityLevel getActivityLevel() { return activityLevel; }
    public void setActivityLevel(ActivityLevel activityLevel) { this.activityLevel = activityLevel; }
}
