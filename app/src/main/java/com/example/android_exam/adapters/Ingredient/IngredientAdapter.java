package com.example.android_exam.adapters.Ingredient;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_exam.R;
import com.example.android_exam.data.local.entity.Ingredient;
import com.google.android.material.chip.Chip;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {
    private List<Ingredient> ingredients;
    private OnIngredientClickListener listener;

    public interface OnIngredientClickListener {
        void onIngredientClick(Ingredient ingredient);
        void onIngredientLongClick(Ingredient ingredient);
    }

    public IngredientAdapter(List<Ingredient> ingredients, OnIngredientClickListener listener) {
        this.ingredients = ingredients;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.bind(ingredient, listener);
    }

    @Override
    public int getItemCount() {
        return ingredients != null ? ingredients.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName, tvQuantity, tvExpiryDate;
        Chip chipCategory;
        View vExpiryIndicator;

        ViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_ingredient_icon);
            tvName = itemView.findViewById(R.id.tv_ingredient_name);
            tvQuantity = itemView.findViewById(R.id.tv_ingredient_quantity);
            tvExpiryDate = itemView.findViewById(R.id.tv_expiry_date);
            chipCategory = itemView.findViewById(R.id.chip_category);
            vExpiryIndicator = itemView.findViewById(R.id.v_expiry_indicator);
        }

        void bind(Ingredient ingredient, OnIngredientClickListener listener) {
            ivIcon.setImageResource(ingredient.getIconResource());
            tvName.setText(ingredient.getName());
            tvQuantity.setText(ingredient.getFormattedQuantity());
            tvExpiryDate.setText(ingredient.getExpiryDate());
            chipCategory.setText(ingredient.getCategory());

            vExpiryIndicator.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(),
                            ingredient.getExpiryStatusColor())));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onIngredientClick(ingredient);
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onIngredientLongClick(ingredient);
                }
                return true;
            });
        }
    }
}