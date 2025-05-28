package com.example.android_exam.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_exam.R;
import com.example.android_exam.data.local.entity.IngredientEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    private List<IngredientEntity> ingredients = new ArrayList<>();

    public void setIngredients(List<IngredientEntity> ingredients) {
        this.ingredients = ingredients;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IngredientAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientAdapter.ViewHolder holder, int position) {
        IngredientEntity ingredient = ingredients.get(position);
        holder.name.setText(ingredient.getName());
        holder.qty.setText("Số lượng: " + ingredient.getQuantity() + " " + ingredient.getUnit());

        Date expiry = ingredient.getExpiryDate();
        if (expiry != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            holder.expiry.setText("HSD: " + sdf.format(expiry));
        } else {
            holder.expiry.setText("Không có hạn sử dụng");
        }
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, qty, expiry;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_name);
            qty = itemView.findViewById(R.id.text_qty);
            expiry = itemView.findViewById(R.id.text_expiry);
        }
    }
}
