package com.example.android_exam.data.dto.nutrition;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class DailyNutritionSummaryDto {
    private Date date;
    private double totalCalories;
    private double totalProtein;
    private double totalCarbs;
    private double totalFat;
    private double totalFiber;

    // User's targets (nullable)
    private Double targetCalories;
    private Double targetProtein;
    private Double targetCarbs;
    private Double targetFat;
    private Double targetFiber;

    private List<NutritionDto> mealBreakdown = new ArrayList<>();

    // Getters and setters
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public double getTotalCalories() { return totalCalories; }
    public void setTotalCalories(double totalCalories) { this.totalCalories = totalCalories; }

    public double getTotalProtein() { return totalProtein; }
    public void setTotalProtein(double totalProtein) { this.totalProtein = totalProtein; }

    public double getTotalCarbs() { return totalCarbs; }
    public void setTotalCarbs(double totalCarbs) { this.totalCarbs = totalCarbs; }

    public double getTotalFat() { return totalFat; }
    public void setTotalFat(double totalFat) { this.totalFat = totalFat; }

    public double getTotalFiber() { return totalFiber; }
    public void setTotalFiber(double totalFiber) { this.totalFiber = totalFiber; }

    public Double getTargetCalories() { return targetCalories; }
    public void setTargetCalories(Double targetCalories) { this.targetCalories = targetCalories; }

    public Double getTargetProtein() { return targetProtein; }
    public void setTargetProtein(Double targetProtein) { this.targetProtein = targetProtein; }

    public Double getTargetCarbs() { return targetCarbs; }
    public void setTargetCarbs(Double targetCarbs) { this.targetCarbs = targetCarbs; }

    public Double getTargetFat() { return targetFat; }
    public void setTargetFat(Double targetFat) { this.targetFat = targetFat; }

    public Double getTargetFiber() { return targetFiber; }
    public void setTargetFiber(Double targetFiber) { this.targetFiber = targetFiber; }

    public List<NutritionDto> getMealBreakdown() { return mealBreakdown; }
    public void setMealBreakdown(List<NutritionDto> mealBreakdown) { this.mealBreakdown = mealBreakdown; }


    @NonNull
    @Override
    public String toString() {
        //Convert to json string
        return "DailyNutritionSummaryDto{" +
                "date=" + date +
                ", totalCalories=" + totalCalories +
                ", totalProtein=" + totalProtein +
                ", totalCarbs=" + totalCarbs +
                ", totalFat=" + totalFat +
                ", totalFiber=" + totalFiber +
                ", targetCalories=" + targetCalories +
                ", targetProtein=" + targetProtein +
                ", targetCarbs=" + targetCarbs +
                ", targetFat=" + targetFat +
                ", targetFiber=" + targetFiber +
                ", mealBreakdown=" + mealBreakdown +
                '}';
    }
}
