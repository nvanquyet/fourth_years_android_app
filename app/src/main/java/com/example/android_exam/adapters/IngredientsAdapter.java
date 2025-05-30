package com.example.android_exam.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android_exam.R;
import com.example.android_exam.data.local.entity.Ingredient;
import com.example.android_exam.utils.DateUtils;
import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder> {

    private List<Ingredient> ingredients;
    private OnIngredientActionListener listener;

    public interface OnIngredientActionListener {
        void onEdit(Ingredient ingredient);
        void onDelete(Ingredient ingredient);
    }

    public IngredientsAdapter(List<Ingredient> ingredients, OnIngredientActionListener listener) {
        this.ingredients = ingredients;
        this.listener = listener;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.bind(ingredient);
    }

    @Override
    public int getItemCount() {
        return ingredients != null ? ingredients.size() : 0;
    }

    public void updateIngredients(List<Ingredient> newIngredients) {
        this.ingredients = newIngredients;
        notifyDataSetChanged();
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvQuantity, tvExpiry, tvCategory;
        private Button btnEdit, btnDelete;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_ingredient_name);
            tvQuantity = itemView.findViewById(R.id.tv_ingredient_quantity);
            tvExpiry = itemView.findViewById(R.id.tv_expiry_date);
            tvCategory = itemView.findViewById(R.id.tv_ingredient_category);
            btnEdit = itemView.findViewById(R.id.btn_edit_ingredient);
            btnDelete = itemView.findViewById(R.id.btn_delete_ingredient);

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEdit(ingredients.get(position));
                    }
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDelete(ingredients.get(position));
                    }
                }
            });
        }

        public void bind(Ingredient ingredient) {
            tvName.setText(ingredient.name);
            tvQuantity.setText(String.format("%.1f %s", ingredient.quantity, ingredient.unit));
            tvExpiry.setText(ingredient.expiryDate != null ? "Expires: " + ingredient.expiryDate : "No expiry");
            tvCategory.setText(ingredient.category != null ? ingredient.category : "No category");

            if (ingredient.expiryDate != null && DateUtils.isExpiringSoon(ingredient.expiryDate, 7)) {
                itemView.setBackgroundColor(itemView.getResources().getColor(android.R.color.holo_orange_light));
            } else if (ingredient.expiryDate != null && DateUtils.isExpired(ingredient.expiryDate)) {
                itemView.setBackgroundColor(itemView.getResources().getColor(android.R.color.holo_red_light));
            } else {
                itemView.setBackgroundColor(itemView.getResources().getColor(android.R.color.transparent));
            }
        }
    }
}