package com.example.android_exam.data.dto.nutrition;

import com.example.android_exam.data.dto.user.UserInformationDto;
import java.util.Date;

public class UserNutritionRequestDto {
    private Date currentDate;
    private Date startDate;
    private Date endDate;
    private UserInformationDto userInformationDto;

    public UserNutritionRequestDto() {
        this.currentDate = new Date();
        this.userInformationDto = new UserInformationDto();
    }

    // Getters and setters
    public Date getCurrentDate() { return currentDate; }
    public void setCurrentDate(Date currentDate) { this.currentDate = currentDate; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
        //End date is set to 7 days after start date by default
        this.endDate = new Date(startDate.getTime() + 7 * 24 * 60 * 60 * 1000L);
    }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public UserInformationDto getUserInformationDto() { return userInformationDto; }
    public void setUserInformationDto(UserInformationDto userInformationDto) { this.userInformationDto = userInformationDto; }
}
