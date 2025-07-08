package com.example.android_exam.data.dto.food;

import com.example.android_exam.data.dto.user.UserInformationDto;

public class FoodSuggestionRequestDto {

    private int maxSuggestions = 5;
    private UserInformationDto userInformation = new UserInformationDto();

    // Constructors
    public FoodSuggestionRequestDto() {}

    public FoodSuggestionRequestDto(int maxSuggestions, UserInformationDto userInformation) {
        this.maxSuggestions = maxSuggestions;
        this.userInformation = userInformation;
    }

    // Getters and Setters
    public int getMaxSuggestions() {
        return maxSuggestions;
    }

    public void setMaxSuggestions(int maxSuggestions) {
        this.maxSuggestions = maxSuggestions;
    }

    public UserInformationDto getUserInformation() {
        return userInformation;
    }

    public void setUserInformation(UserInformationDto userInformation) {
        this.userInformation = userInformation;
    }
}