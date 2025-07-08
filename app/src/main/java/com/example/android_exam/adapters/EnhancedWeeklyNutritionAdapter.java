package com.example.android_exam.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android_exam.databinding.ItemWeeklyNutritionBinding;
import com.example.android_exam.data.dto.nutrition.DailyNutritionSummaryDto;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EnhancedWeeklyNutritionAdapter extends RecyclerView.Adapter<EnhancedWeeklyNutritionAdapter.WeeklyDayViewHolder> {
    private List<DailyNutritionSummaryDto> weeklyData = new ArrayList<>();
    private OnDaySelectedListener onDaySelectedListener;
    private Calendar selectedWeekStart = Calendar.getInstance();

    public interface OnDaySelectedListener {
        void onDaySelected(DailyNutritionSummaryDto selectedDay);
    }

    public EnhancedWeeklyNutritionAdapter(OnDaySelectedListener listener) {
        this.onDaySelectedListener = listener;
    }

    public void updateWeeklyData(List<DailyNutritionSummaryDto> newWeeklyData) {
        this.weeklyData.clear();
        if (newWeeklyData != null) {
            this.weeklyData.addAll(newWeeklyData);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WeeklyDayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWeeklyNutritionBinding binding = ItemWeeklyNutritionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new WeeklyDayViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WeeklyDayViewHolder holder, int position) {
        holder.bind(weeklyData.get(position));
    }

    @Override
    public int getItemCount() {
        return weeklyData.size();
    }

    class WeeklyDayViewHolder extends RecyclerView.ViewHolder {
        private ItemWeeklyNutritionBinding binding;
        private SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("vi", "VN"));
        private SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'Tháng' MM, yyyy", new Locale("vi", "VN"));

        public WeeklyDayViewHolder(ItemWeeklyNutritionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Setup click listener for day selection
            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onDaySelectedListener != null) {
                    onDaySelectedListener.onDaySelected(weeklyData.get(position));
                }
            });
        }

        public void bind(DailyNutritionSummaryDto dayData) {
            // Set day name and date
            binding.tvDayName.setText(dayFormat.format(dayData.getDate()));
            binding.tvDayDate.setText(dateFormat.format(dayData.getDate()));

            // Set calories
            binding.tvDayCalories.setText(String.format("%.0f", dayData.getTotalCalories()));

            // Set progress
            if (dayData.getTargetCalories() != null && dayData.getTargetCalories() > 0) {
                int progress = (int) ((dayData.getTotalCalories() / dayData.getTargetCalories()) * 100);
                binding.progressDayCalories.setProgress(Math.min(progress, 100));
            } else {
                binding.progressDayCalories.setProgress(0);
            }

            // Set macros
            binding.tvMealProtein.setText(String.format("%.0fg", dayData.getTotalProtein()));
            binding.tvMealCarbs.setText(String.format("%.0fg", dayData.getTotalCarbs()));
            binding.tvMealFat.setText(String.format("%.0fg", dayData.getTotalFat()));
            binding.tvMealFiber.setText(String.format("%.0fg", dayData.getTotalFiber()));
        }
    }
}