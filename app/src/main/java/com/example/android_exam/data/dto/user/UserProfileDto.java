package com.example.android_exam.data.dto.user;

import com.example.android_exam.data.models.base.User;
import com.example.android_exam.data.models.enums.ActivityLevel;
import com.example.android_exam.data.models.enums.Gender;
import com.example.android_exam.data.models.enums.NutritionGoal;
import com.example.android_exam.utils.ISODateAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class UserProfileDto {
    private int id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    @SerializedName("gender")
    private Gender gender;

    @JsonAdapter(ISODateAdapter.class)
    private Date dateOfBirth;
    private Double height;
    private Double weight;
    private Double targetWeight;
    @SerializedName("primaryNutritionGoal")
    private NutritionGoal primaryNutritionGoal;
    @SerializedName("activityLevel")
    private ActivityLevel activityLevel;
    private Boolean hasFoodAllergies;
    private String foodAllergies;
    private String foodPreferences;
    private Boolean enableNotifications;
    private Boolean enableMealReminders;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
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
    public Boolean getHasFoodAllergies() { return hasFoodAllergies; }
    public void setHasFoodAllergies(Boolean hasFoodAllergies) { this.hasFoodAllergies = hasFoodAllergies; }
    public String getFoodAllergies() { return foodAllergies; }
    public void setFoodAllergies(String foodAllergies) { this.foodAllergies = foodAllergies; }
    public String getFoodPreferences() { return foodPreferences; }
    public void setFoodPreferences(String foodPreferences) { this.foodPreferences = foodPreferences; }
    public Boolean getEnableNotifications() { return enableNotifications; }
    public void setEnableNotifications(Boolean enableNotifications) { this.enableNotifications = enableNotifications; }
    public Boolean getEnableMealReminders() { return enableMealReminders; }
    public void setEnableMealReminders(Boolean enableMealReminders) { this.enableMealReminders = enableMealReminders; }

    public User toUser() {
        User user = new User();
        user.setId(this.id);
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        user.setGender(this.gender);
        user.setDateOfBirth(this.dateOfBirth);
        user.setHeight(this.height);
        user.setWeight(this.weight);
        user.setTargetWeight(this.targetWeight);
        user.setPrimaryNutritionGoal(this.primaryNutritionGoal);
        user.setActivityLevel(this.activityLevel);
        user.setHasFoodAllergies(this.hasFoodAllergies);
        user.setFoodAllergies(this.foodAllergies);
        user.setFoodPreferences(this.foodPreferences);
        user.setEnableNotifications(this.enableNotifications);
        user.setEnableMealReminders(this.enableMealReminders);
        return user;
    }
    public static UserProfileDto fromUser(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setGender(user.getGender());
        dto.setHeight(user.getHeight());
        dto.setWeight(user.getWeight());
        dto.setTargetWeight(user.getTargetWeight());
        dto.setPrimaryNutritionGoal(user.getPrimaryNutritionGoal());
        dto.setActivityLevel(user.getActivityLevel());
        dto.setHasFoodAllergies(user.getHasFoodAllergies());
        dto.setFoodAllergies(user.getFoodAllergies());
        dto.setFoodPreferences(user.getFoodPreferences());
        dto.setEnableNotifications(user.getEnableNotifications());
        dto.setEnableMealReminders(user.getEnableMealReminders());
        return dto;
    }

    public String toJson() {
        return new com.google.gson.Gson().toJson(this);
    }
}
