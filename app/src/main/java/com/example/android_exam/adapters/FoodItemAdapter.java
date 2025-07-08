package com.example.android_exam.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_exam.data.dto.nutrition.FoodNutritionDto;
import com.example.android_exam.databinding.ItemFoodAnalystBinding;
import java.util.ArrayList;
import java.util.List;

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.FoodItemViewHolder> {
    private static final String TAG = "FoodItemAdapter";
    private List<FoodNutritionDto> foodItems = new ArrayList<>();
    private OnFoodItemClickListener clickListener;

    // Interface for handling food item clicks
    public interface OnFoodItemClickListener {
        void onFoodItemClick(FoodNutritionDto foodItem);
    }

    // Constructor with click listener
    public FoodItemAdapter(OnFoodItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    // Default constructor (existing functionality)
    public FoodItemAdapter() {
        this.clickListener = null;
    }

    public void updateFoodItems(List<FoodNutritionDto> newFoodItems) {
        this.foodItems.clear();
        if (newFoodItems != null) {
            this.foodItems.addAll(newFoodItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFoodAnalystBinding binding = ItemFoodAnalystBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new FoodItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodItemViewHolder holder, int position) {
        holder.bind(foodItems.get(position));
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    class FoodItemViewHolder extends RecyclerView.ViewHolder {
        private ItemFoodAnalystBinding binding;
        private FoodNutritionDto foodItem;

        public FoodItemViewHolder(ItemFoodAnalystBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Set click listener on the entire item view
            binding.getRoot().setOnClickListener(v -> {
                if (foodItem != null) {
                    // Log the food nutrition data
                    Log.d(TAG, "Food item clicked: " + foodItem.getFoodName());
                    Log.d(TAG, "Calories: " + foodItem.getCalories());
                    Log.d(TAG, "Protein: " + foodItem.getProtein() + "g");
                    Log.d(TAG, "Carbs: " + foodItem.getCarbs() + "g");
                    Log.d(TAG, "Fat: " + foodItem.getFat() + "g");
                    Log.d(TAG, "Fiber: " + foodItem.getFiber() + "g");
                    Log.d(TAG, "Full FoodNutritionDto: " + foodItem.toString());

                    // Call the click listener if it exists
                    if (clickListener != null) {
                        clickListener.onFoodItemClick(foodItem);
                    }
                }
            });
        }

        public void bind(FoodNutritionDto foodItem) {
            this.foodItem = foodItem;
            binding.tvFoodName.setText(foodItem.getFoodName());
            binding.tvFoodCalories.setText(String.format("%.0f kcal", foodItem.getCalories()));
            binding.tvFoodProtein.setText(String.format("%.0fg", foodItem.getProtein()));
            binding.tvFoodCarbs.setText(String.format("%.0fg", foodItem.getCarbs()));
            binding.tvFoodFat.setText(String.format("%.0fg", foodItem.getFat()));
            binding.tvFoodFiber.setText(String.format("%.0fg", foodItem.getFiber()));

            // Load image if available
            // if (foodItem.getImageUrl() != null && !foodItem.getImageUrl().isEmpty()) {
            //     // Use your image loading library here (Glide, Picasso, etc.)
            //     // For example with Glide:
            //     Glide.with(binding.getRoot().getContext())
            //         .load(foodItem.getImageUrl())
            //         .placeholder(R.drawable.ic_food_placeholder)
            //         .into(binding.ivFoodImage);
            // }
        }
    }
}