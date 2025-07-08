package com.example.android_exam.data.models.base;

import com.example.android_exam.data.dto.user.UserInformationDto;
import com.example.android_exam.data.dto.user.UserProfileDto;
import com.example.android_exam.data.models.enums.ActivityLevel;
import com.example.android_exam.data.models.enums.Gender;
import com.example.android_exam.data.models.enums.NutritionGoal;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class User extends BaseEntity {
    @SerializedName("username")
    private String username = "";
    @SerializedName("email")
    private String email = "";
    @SerializedName("passwordHash")
    private String passwordHash = "";
    @SerializedName("firstName")
    private String firstName = "";
    @SerializedName("lastName")
    private String lastName = "";
    @SerializedName("isActive")
    private boolean isActive = true;
    @SerializedName("dateOfBirth")
    private Date dateOfBirth;
    @SerializedName("gender")
    private Gender gender;
    @SerializedName("height")
    private Double height;
    @SerializedName("weight")
    private Double weight;
    @SerializedName("targetWeight")
    private Double targetWeight;
    @SerializedName("primaryNutritionGoal")
    private NutritionGoal primaryNutritionGoal = NutritionGoal.BALANCED;
    @SerializedName("activityLevel")
    private ActivityLevel activityLevel = ActivityLevel.SEDENTARY;
    @SerializedName("hasFoodAllergies")
    private boolean hasFoodAllergies = false;
    @SerializedName("foodAllergies")
    private String foodAllergies;
    @SerializedName("foodPreferences")
    private String foodPreferences;
    @SerializedName("enableNotifications")
    private boolean enableNotifications = true;
    @SerializedName("enableMealReminders")
    private boolean enableMealReminders = true;

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
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
    public boolean getHasFoodAllergies() { return hasFoodAllergies; }
    public void setHasFoodAllergies(boolean hasFoodAllergies) { this.hasFoodAllergies = hasFoodAllergies; }
    public String getFoodAllergies() { return foodAllergies; }
    public void setFoodAllergies(String foodAllergies) { this.foodAllergies = foodAllergies; }
    public String getFoodPreferences() { return foodPreferences; }
    public void setFoodPreferences(String foodPreferences) { this.foodPreferences = foodPreferences; }
    public boolean getEnableNotifications() { return enableNotifications; }
    public void setEnableNotifications(boolean enableNotifications) { this.enableNotifications = enableNotifications; }
    public boolean getEnableMealReminders() { return enableMealReminders; }
    public void setEnableMealReminders(boolean enableMealReminders) { this.enableMealReminders = enableMealReminders; }

    public Double getBMI() {
        if (height == null || weight == null || height == 0) return null;
        double heightInMeters = height / 100.0; // Vì bạn đang lưu height đơn vị cm
        return weight / (heightInMeters * heightInMeters);
    }

    public Double getBMR() {
        if (height == null || weight == null || dateOfBirth == null || gender == null) return null;

        // Tính tuổi
        LocalDate birthDate = dateOfBirth.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        if (gender == Gender.MALE) {
            return 10 * weight + 6.25 * height - 5 * age + 5;
        } else if (gender == Gender.FEMALE) {
            return 10 * weight + 6.25 * height - 5 * age - 161;
        } else {
            return null; // Nếu giới tính chưa xác định
        }
    }

    public Double getTDEE() {
        Double bmr = getBMR();
        if (bmr == null || activityLevel == null) return null;

        double activityFactor = switch (activityLevel) {
            case SEDENTARY -> 1.2;
            case LIGHT -> 1.375;
            case MODERATE -> 1.55;
            case ACTIVE -> 1.725;
            case VERY_ACTIVE -> 1.9;
        };

        return bmr * activityFactor;
    }

    public UserInformationDto toUserInformationDto() {
        UserInformationDto dto = new UserInformationDto();
        dto.setDateOfBirth(dateOfBirth);
        dto.setGender(gender);
        dto.setHeight(height);
        dto.setWeight(weight);
        dto.setTargetWeight(targetWeight);
        dto.setPrimaryNutritionGoal(primaryNutritionGoal);
        dto.setActivityLevel(activityLevel);
        return dto;
    }

    public String toJson(){
        return new com.google.gson.Gson().toJson(this);
    }
}