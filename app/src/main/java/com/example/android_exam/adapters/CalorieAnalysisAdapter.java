package com.example.android_exam.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_exam.R;
import com.example.android_exam.data.local.entity.Food;
import com.example.android_exam.data.models.DiaryWithMeals;
import com.example.android_exam.data.models.MealWithFoods;

import java.util.List;
public class CalorieAnalysisAdapter extends RecyclerView.Adapter<CalorieAnalysisAdapter.DiaryViewHolder> {

    private final List<DiaryWithMeals> diaryList;

    public CalorieAnalysisAdapter(List<DiaryWithMeals> diaryList) {
        this.diaryList = diaryList;
    }

    public static class DiaryViewHolder extends RecyclerView.ViewHolder {
        TextView diaryDate, finalCal;
        RecyclerView mealsRecyclerView;

        public DiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            diaryDate = itemView.findViewById(R.id.diary_date);
            finalCal = itemView.findViewById(R.id.final_cal);
            mealsRecyclerView = itemView.findViewById(R.id.mealsRecyclerView);
        }
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diary_entry, parent, false);
        return new DiaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
        DiaryWithMeals diary = diaryList.get(position);
        holder.diaryDate.setText(diary.date);

        // Tổng kcal
        float totalCal = 0;
        for (MealWithFoods meal : diary.mealsWithFoods) {
            for (Food food : meal.foods) {
                totalCal += food.totalCalories;
            }
        }
        holder.finalCal.setText("Final calo: " + totalCal + " kcal");

        MealAdapter mealAdapter = new MealAdapter(diary.mealsWithFoods);
        holder.mealsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.mealsRecyclerView.setAdapter(mealAdapter);
    }

    @Override
    public int getItemCount() {
        return diaryList.size();
    }
}