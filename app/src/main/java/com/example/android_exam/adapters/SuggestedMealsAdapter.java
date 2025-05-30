package com.example.android_exam.adapters;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android_exam.R;
import com.example.android_exam.data.local.entity.Food;
import java.util.List;

public class SuggestedMealsAdapter extends RecyclerView.Adapter<SuggestedMealsAdapter.MealViewHolder> {

    private List<Food> meals;
    private OnMealClickListener listener;

    public interface OnMealClickListener {
        void onMealClick(Food meal);
    }

    public SuggestedMealsAdapter(List<Food> meals, OnMealClickListener listener) {
        this.meals = meals;
        this.listener = listener;
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
        Food meal = meals.get(position);
        holder.bind(meal);
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
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onMealClick(meals.get(position));
                    }
                }
            });
        }

        public void bind(Food meal) {
            tvMealName.setText(meal.name);
            tvMealCalories.setText(String.format("%.0f kcal", meal.totalCalories));
            tvMealDescription.setText(meal.description);

            // Set default image - you can implement image loading here
            ivMealImage.setImageResource(R.drawable.ic_food_default);
        }
    }
}