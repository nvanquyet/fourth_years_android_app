package com.example.android_exam.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android_exam.R;
import com.example.android_exam.data.dto.food.FoodDataResponseDto;
import com.example.android_exam.data.dto.food.FoodSuggestionResponseDto;

import java.util.ArrayList;
import java.util.List;

public class FoodSuggestionsAdapter extends RecyclerView.Adapter<FoodSuggestionsAdapter.ViewHolder> {
    private List<FoodSuggestionResponseDto> foodList = new ArrayList<>();
    private OnFoodClickListener listener;

    public interface OnFoodClickListener {
        void onFoodClick(FoodSuggestionResponseDto food);
    }

    public void setOnFoodClickListener(OnFoodClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<FoodSuggestionResponseDto> newList) {
        this.foodList.clear();
        this.foodList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food_suggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodSuggestionResponseDto food = foodList.get(position);
        holder.bind(food);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFoodName, tvCalories, tvTimeCooking, tvDifficulty;
        private ImageView ivFoodImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvCalories = itemView.findViewById(R.id.tvFoodKcal);
            tvTimeCooking = itemView.findViewById(R.id.tvPrepTime);
            tvDifficulty = itemView.findViewById(R.id.tvRating);
            ivFoodImage = itemView.findViewById(R.id.ivFoodImage);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onFoodClick(foodList.get(getAdapterPosition()));
                }
            });
        }

        @SuppressLint("DefaultLocale")
        public void bind(FoodSuggestionResponseDto food) {
            tvFoodName.setText(food.getName());
            var difficulty = food.getDifficulty();
            difficulty = Math.clamp(difficulty, 0, 5);
            tvDifficulty.setText(String.valueOf(difficulty));
            tvTimeCooking.setText(String.format("%d phút", food.getPrepTimeMinutes() + food.getCookTimeMinutes()));
            tvCalories.setText(food.getKcal() != null ? String.format("%.0f kcal", food.getKcal()) : "N/A");

            // Load image using your preferred image loading library (Glide, Picasso, etc.)
//            if(food.getImage() != null && !food.getImage().isEmpty()) {
//                // Example using Glide
//                Glide.with(itemView.getContext()).load(food.getImage()).into(ivFoodImage);
//            } else {
//                // Set a placeholder or default image if no image is available
//                ivFoodImage.setImageResource(R.drawable.ic_food_placeholder);
//            }
        }
    }
}