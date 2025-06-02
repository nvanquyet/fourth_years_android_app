package com.example.android_exam.adapters.Home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android_exam.R;
import com.example.android_exam.data.local.entity.Food;
import java.util.List;
import java.util.ArrayList;

public class SuggestedMealsAdapter extends RecyclerView.Adapter<SuggestedMealsAdapter.MealViewHolder> {

    private List<Food> meals;
    private OnMealClickListener listener;

    public interface OnMealClickListener {
        void onMealClick(Food meal);
    }

    public SuggestedMealsAdapter(List<Food> meals, OnMealClickListener listener) {
        this.meals = meals != null ? meals : new ArrayList<>();
        this.listener = listener;
    }

    // Method để update data với DiffUtil
    public void updateMeals(List<Food> newMeals) {
        // Tạo biến final để lưu danh sách mới
        final List<Food> updatedMeals = (newMeals != null) ? newMeals : new ArrayList<>();

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return meals.size();
            }

            @Override
            public int getNewListSize() {
                return updatedMeals.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                Food oldMeal = meals.get(oldItemPosition);
                Food newMeal = updatedMeals.get(newItemPosition);
                return oldMeal.name != null && oldMeal.name.equals(newMeal.name);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                Food oldMeal = meals.get(oldItemPosition);
                Food newMeal = updatedMeals.get(newItemPosition);
                return oldMeal.name != null && oldMeal.name.equals(newMeal.name) &&
                        oldMeal.totalCalories == newMeal.totalCalories &&
                        ((oldMeal.description == null && newMeal.description == null) ||
                                (oldMeal.description != null && oldMeal.description.equals(newMeal.description)));
            }
        });

        this.meals = updatedMeals;
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_suggested_meal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        if (meals != null && position < meals.size()) {
            Food meal = meals.get(position);
            holder.bind(meal);
        }
    }

    @Override
    public int getItemCount() {
        return meals != null ? meals.size() : 0;
    }

    class MealViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivMealImage;
        private TextView tvMealName;
        private TextView tvMealCalories;
        private TextView tvMealDescription;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMealImage = itemView.findViewById(R.id.iv_meal_image);
            tvMealName = itemView.findViewById(R.id.tv_meal_name);
            tvMealCalories = itemView.findViewById(R.id.tv_meal_calories);
            tvMealDescription = itemView.findViewById(R.id.tv_meal_description);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && position < meals.size()) {
                        listener.onMealClick(meals.get(position));
                    }
                }
            });
        }

        public void bind(Food meal) {
            if (meal != null) {
                tvMealName.setText(meal.name != null ? meal.name : "Unknown Meal");
                tvMealCalories.setText(String.format("%.0f kcal", meal.totalCalories));
                tvMealDescription.setText(meal.description != null ? meal.description : "No description");

                // Set default image - you can implement image loading here
                ivMealImage.setImageResource(R.drawable.ic_food_default);
            }
        }
    }
}