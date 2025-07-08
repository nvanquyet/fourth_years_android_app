package com.example.android_exam.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_exam.R;
import com.example.android_exam.data.dto.food.FoodIngredientDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class IngredientsFoodAdapter extends RecyclerView.Adapter<IngredientsFoodAdapter.IngredientViewHolder> {

    private List<FoodIngredientDto> ingredients = new ArrayList<>();
    private OnIngredientInteractionListener listener;

    public interface OnIngredientInteractionListener {
        void onQuantityChanged(int ingredientId, BigDecimal newQuantity);
    }

    public IngredientsFoodAdapter(OnIngredientInteractionListener listener) {
        this.listener = listener;
    }

    public void setIngredients(List<FoodIngredientDto> ingredients) {
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient_in_food, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        FoodIngredientDto ingredient = ingredients.get(position);
        holder.bind(ingredient);
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder {
        private TextView tvIngredientName;
        private TextView tvIngredientUnit;
        private TextView tvQuantity;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIngredientName = itemView.findViewById(R.id.tv_ingredient_name);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvIngredientUnit = itemView.findViewById(R.id.tv_unit);
        }

        @SuppressLint("SetTextI18n")
        public void bind(FoodIngredientDto ingredient) {
            // Set ingredient name and weight (check null)
            String unit = ingredient.getUnit() != null ? ingredient.getUnit().toString() : "";
            tvIngredientName.setText(ingredient.getIngredientName() != null
                    ? ingredient.getIngredientName() : "Unknown Ingredient");

            // Set quantity (check null)
            BigDecimal quantity = ingredient.getQuantity() != null ? ingredient.getQuantity() : BigDecimal.ZERO;
            tvQuantity.setText(quantity.toString());

            // Check unit
            if (ingredient.getUnit() != null) {
                tvIngredientUnit.setText(ingredient.getUnit().toString());
            } else {
                tvIngredientUnit.setText("Unknown Unit");
            }
        }
    }
}