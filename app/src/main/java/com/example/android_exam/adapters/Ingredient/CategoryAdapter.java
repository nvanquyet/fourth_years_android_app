package com.example.android_exam.adapters.Ingredient;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.android_exam.R;
import com.google.android.material.chip.Chip;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<String> categories;
    private OnCategoryClickListener listener;
    private int selectedPosition = 0;

    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
    }

    public CategoryAdapter(List<String> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_chip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String category = categories.get(position);
        holder.bind(category, position == selectedPosition, listener, position);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setSelectedPosition(int position) {
        int previousSelected = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousSelected);
        notifyItemChanged(selectedPosition);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        Chip chipCategory;

        ViewHolder(View itemView) {
            super(itemView);
            chipCategory = itemView.findViewById(R.id.chip_category);
        }

        void bind(String category, boolean isSelected, OnCategoryClickListener listener, int position) {
            chipCategory.setText(category);
            chipCategory.setChecked(isSelected);

            chipCategory.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(category);
                    CategoryAdapter.this.setSelectedPosition(position);
                }
            });
        }
    }
}
