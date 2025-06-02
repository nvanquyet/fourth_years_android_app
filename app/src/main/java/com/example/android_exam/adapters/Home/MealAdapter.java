package com.example.android_exam.adapters.Home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_exam.R;
import com.example.android_exam.data.models.MealWithFoods;

import java.util.List;
public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private final List<MealWithFoods> meals;

    public MealAdapter(List<MealWithFoods> meals) {
        this.meals = meals;
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView mealType;
        RecyclerView foodRecyclerView;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealType = itemView.findViewById(R.id.meal_type);
            foodRecyclerView = itemView.findViewById(R.id.foodItemsRecyclerView);
        }
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        MealWithFoods meal = meals.get(position);
        holder.mealType.setText(meal.meal.mealType.toString());

        FoodAdapter foodAdapter = new FoodAdapter(meal.foods);
        holder.foodRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.foodRecyclerView.setAdapter(foodAdapter);
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }
}

