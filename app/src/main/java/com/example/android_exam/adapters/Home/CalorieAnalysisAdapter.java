package com.example.android_exam.adapters.Home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_exam.R;
import com.example.android_exam.data.local.entity.Food;
import com.example.android_exam.data.models.DiaryWithMeals;
import com.example.android_exam.data.models.MealWithFoods;

import java.util.List;
import java.util.ArrayList;

public class CalorieAnalysisAdapter extends RecyclerView.Adapter<CalorieAnalysisAdapter.DiaryViewHolder> {

    private List<DiaryWithMeals> diaryList;

    public CalorieAnalysisAdapter(List<DiaryWithMeals> diaryList) {
        this.diaryList = diaryList != null ? diaryList : new ArrayList<>();
    }

    // Method để update data với DiffUtil
    public void updateData(List<DiaryWithMeals> newData) {
        // Tạo biến final để lưu danh sách mới
        final List<DiaryWithMeals> updatedData = (newData != null) ? newData : new ArrayList<>();

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return diaryList.size();
            }

            @Override
            public int getNewListSize() {
                return updatedData.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                DiaryWithMeals oldItem = diaryList.get(oldItemPosition);
                DiaryWithMeals newItem = updatedData.get(newItemPosition);
                return oldItem.date != null && newItem.date != null && oldItem.date.equals(newItem.date);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                DiaryWithMeals oldItem = diaryList.get(oldItemPosition);
                DiaryWithMeals newItem = updatedData.get(newItemPosition);

                // So sánh date và số lượng meals
                if (!oldItem.date.equals(newItem.date)) {
                    return false;
                }

                if (oldItem.mealsWithFoods.size() != newItem.mealsWithFoods.size()) {
                    return false;
                }

                // So sánh tổng calories
                float oldTotalCal = calculateTotalCalories(oldItem);
                float newTotalCal = calculateTotalCalories(newItem);

                return Math.abs(oldTotalCal - newTotalCal) < 0.01f; // So sánh float với tolerance
            }
        });

        this.diaryList = updatedData;
        diffResult.dispatchUpdatesTo(this);
    }

    private float calculateTotalCalories(DiaryWithMeals diary) {
        float totalCal = 0;
        if (diary.mealsWithFoods != null) {
            for (MealWithFoods meal : diary.mealsWithFoods) {
                if (meal.foods != null) {
                    for (Food food : meal.foods) {
                        totalCal += food.totalCalories;
                    }
                }
            }
        }
        return totalCal;
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
        if (diaryList != null && position < diaryList.size()) {
            DiaryWithMeals diary = diaryList.get(position);
            holder.diaryDate.setText(diary.date != null ? diary.date : "Unknown Date");

            // Tính tổng kcal
            float totalCal = calculateTotalCalories(diary);
            holder.finalCal.setText(String.format("Final calo: %.0f kcal", totalCal));

            // Setup nested RecyclerView cho meals
            if (diary.mealsWithFoods != null) {
                MealAdapter mealAdapter = new MealAdapter(diary.mealsWithFoods);
                holder.mealsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
                holder.mealsRecyclerView.setAdapter(mealAdapter);

                // Optimizations cho nested RecyclerView
                holder.mealsRecyclerView.setHasFixedSize(true);
                holder.mealsRecyclerView.setNestedScrollingEnabled(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return diaryList != null ? diaryList.size() : 0;
    }
}