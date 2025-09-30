package com.example.android_exam.adapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android_exam.R;
import com.example.android_exam.data.dto.nutrition.FoodNutritionDto;
import com.example.android_exam.data.dto.nutrition.NutritionDto;
import com.example.android_exam.databinding.ItemMealExpandableBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EnhancedMealAdapter extends RecyclerView.Adapter<EnhancedMealAdapter.MealViewHolder> {
    private static final String TAG = "EnhancedMealAdapter";
    private List<NutritionDto> meals = new ArrayList<>();
    private List<Boolean> expandedStates = new ArrayList<>();
    private OnDateSelectedListener dateSelectedListener;
    private OnFoodItemClickListener foodItemClickListener;
    private Calendar selectedDate = Calendar.getInstance();

    public interface OnDateSelectedListener {
        void onDateSelected(Calendar date);
    }

    public interface OnFoodItemClickListener {
        void onFoodItemClick(FoodNutritionDto foodItem);
    }

    public EnhancedMealAdapter(OnDateSelectedListener dateSelectedListener) {
        this.dateSelectedListener = dateSelectedListener;
    }

    public EnhancedMealAdapter(OnDateSelectedListener dateSelectedListener, OnFoodItemClickListener foodItemClickListener) {
        this.dateSelectedListener = dateSelectedListener;
        this.foodItemClickListener = foodItemClickListener;
    }

    public void updateMeals(List<NutritionDto> newMeals) {
        this.meals.clear();
        this.meals.addAll(newMeals);

        // Debug log
        for (NutritionDto meal : newMeals) {
            Log.d(TAG, "Meal type: " + meal.getMealType().toString());
        }

        // Initialize expanded states
        this.expandedStates.clear();
        for (int i = 0; i < newMeals.size(); i++) {
            expandedStates.add(false);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMealExpandableBinding binding = ItemMealExpandableBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MealViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        holder.bind(meals.get(position), expandedStates.get(position));
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    class MealViewHolder extends RecyclerView.ViewHolder {
        private ItemMealExpandableBinding binding;
        private FoodItemAdapter foodAdapter;

        public MealViewHolder(ItemMealExpandableBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Setup food items RecyclerView with click listener
            foodAdapter = new FoodItemAdapter(foodItem -> {
                // Log the food item data
                Log.d(TAG, "Food item clicked from meal adapter: " + foodItem.getFoodName());
                Log.d(TAG, "Food details - Calories: " + foodItem.getCalories() + ", Protein: " + foodItem.getProtein() + "g");

                // Call the external click listener if it exists
                if (foodItemClickListener != null) {
                    foodItemClickListener.onFoodItemClick(foodItem);
                }
            });

            binding.rvFoodItems.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
            binding.rvFoodItems.setAdapter(foodAdapter);

            // Setup click listener for expand/collapse
            binding.layoutMealHeader.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    toggleExpanded(position);
                }
            });
        }

        public void bind(NutritionDto meal, boolean isExpanded) {
            binding.tvMealName.setText(meal.getMealType().toString());
            binding.tvMealCalories.setText(String.format("%.0f kcal", meal.getTotalCalories()));
            binding.tvMealItemCount.setText(String.format("%d món", meal.getFoods().size()));

            // Set meal time based on meal type
            String mealTime = getMealTime(meal.getMealType().toString());
            binding.tvMealTime.setText(mealTime);

            // Set meal date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd 'Tháng' MM, yyyy", new Locale("vi", "VN"));
            binding.tvMealDate.setText(dateFormat.format(meal.getMealDate()));

            // Set meal icon based on meal type
            int iconRes = getMealIcon(meal.getMealType().toString());
            binding.ivMealIcon.setImageResource(iconRes);

            // Update macros
            double totalProtein = meal.getFoods().stream()
                    .mapToDouble(f -> f.getProtein().doubleValue())
                    .sum();

            double totalCarbs = meal.getFoods().stream()
                    .mapToDouble(f -> f.getCarbs().doubleValue())
                    .sum();

            double totalFat = meal.getFoods().stream()
                    .mapToDouble(f -> f.getFat().doubleValue())
                    .sum();

            binding.tvMealProtein.setText(String.format("%.0fg", totalProtein));
            binding.tvMealCarbs.setText(String.format("%.0fg", totalCarbs));
            binding.tvMealFat.setText(String.format("%.0fg", totalFat));

            // Update expand/collapse state
            updateExpandedState(isExpanded);

            // Update food items
            foodAdapter.updateFoodItems(meal.getFoods());
        }

        private void toggleExpanded(int position) {
            boolean currentState = expandedStates.get(position);
            expandedStates.set(position, !currentState);
            updateExpandedState(!currentState);
        }

        private void updateExpandedState(boolean isExpanded) {
            if (isExpanded) {
                binding.rvFoodItems.setVisibility(View.VISIBLE);
                binding.ivExpandArrow.setRotation(180f);
            } else {
                binding.rvFoodItems.setVisibility(View.GONE);
                binding.ivExpandArrow.setRotation(0f);
            }

            // Animate arrow rotation
            float fromRotation = isExpanded ? 0f : 180f;
            float toRotation = isExpanded ? 180f : 0f;

            RotateAnimation rotate = new RotateAnimation(fromRotation, toRotation,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(200);
            rotate.setFillAfter(true);
            binding.ivExpandArrow.startAnimation(rotate);
        }

        private String getMealTime(String mealType) {
            return switch (mealType) {
                case "Bữa Sáng" -> "7:30 AM";
                case "Bữa Trưa" -> "12:00 PM";
                case "Bữa Chiều" -> "3:00 PM";
                case "Bữa Tối" -> "7:00 PM";
                default -> "N/A";
            };
        }

        private int getMealIcon(String mealType) {
            return switch (mealType) {
                case "Bữa Sáng" -> R.drawable.ic_breakfast;
                case "Bữa Trưa" -> R.drawable.ic_lunch;
                case "Bữa Chiều" -> R.drawable.ic_snack;
                case "Bữa Tối" -> R.drawable.ic_dinner;
                default -> R.drawable.ic_food_placeholder;
            };
        }
    }
}