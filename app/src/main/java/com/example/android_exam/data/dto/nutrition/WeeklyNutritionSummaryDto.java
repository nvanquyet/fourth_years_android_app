package com.example.android_exam.data.dto.nutrition;


import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class WeeklyNutritionSummaryDto {
    private Date weekStart;
    private Date weekEnd;
    private double averageCalories;
    private double averageProtein;
    private double averageCarbs;
    private double averageFat;
    private double averageFiber;

    private Double targetCalories;
    private Double targetProtein;
    private Double targetCarbs;
    private Double targetFat;
    private Double targetFiber;

    private List<DailyNutritionSummaryDto> dailyBreakdown = new ArrayList<>();

    // Getters and setters
    public Date getWeekStart() { return weekStart; }
    public void setWeekStart(Date weekStart) { this.weekStart = weekStart; }

    public Date getWeekEnd() { return weekEnd; }
    public void setWeekEnd(Date weekEnd) { this.weekEnd = weekEnd; }

    public double getAverageCalories() { return averageCalories; }
    public void setAverageCalories(double averageCalories) { this.averageCalories = averageCalories; }

    public double getAverageProtein() { return averageProtein; }
    public void setAverageProtein(double averageProtein) { this.averageProtein = averageProtein; }

    public double getAverageCarbs() { return averageCarbs; }
    public void setAverageCarbs(double averageCarbs) { this.averageCarbs = averageCarbs; }

    public double getAverageFat() { return averageFat; }
    public void setAverageFat(double averageFat) { this.averageFat = averageFat; }

    public double getAverageFiber() { return averageFiber; }
    public void setAverageFiber(double averageFiber) { this.averageFiber = averageFiber; }

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

    public List<DailyNutritionSummaryDto> getDailyBreakdown() { return dailyBreakdown; }
    public void setDailyBreakdown(List<DailyNutritionSummaryDto> dailyBreakdown) { this.dailyBreakdown = dailyBreakdown; }
}