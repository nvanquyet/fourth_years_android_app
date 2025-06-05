package com.example.android_exam.adapters.Ingredient;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.android_exam.R;
import com.google.android.material.chip.Chip;

import java.util.List;

public class ExpiryFilterAdapter extends RecyclerView.Adapter<ExpiryFilterAdapter.ViewHolder> {
    private List<String> filters;
    private OnExpiryFilterClickListener listener;
    private int selectedPosition = 0;

    public interface OnExpiryFilterClickListener {
        void onExpiryFilterClick(String filter);
    }

    public ExpiryFilterAdapter(List<String> filters, OnExpiryFilterClickListener listener) {
        this.filters = filters;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expiry_chip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String filter = filters.get(position);
        holder.bind(filter, position == selectedPosition, listener, position);
    }

    @Override
    public int getItemCount() {
        return filters.size();
    }

    public void setSelectedPosition(int position) {
        int previousSelected = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previousSelected);
        notifyItemChanged(selectedPosition);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        Chip chipFilter;

        ViewHolder(View itemView) {
            super(itemView);
            chipFilter = itemView.findViewById(R.id.chip_expiry_filter);
        }

        void bind(String filter, boolean isSelected, OnExpiryFilterClickListener listener, int position) {
            chipFilter.setText(filter);
            chipFilter.setChecked(isSelected);

            // Set different colors for different filter types
            int colorRes = getFilterColor(filter);
            chipFilter.setChipBackgroundColorResource(colorRes);

            chipFilter.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onExpiryFilterClick(filter);
                    ExpiryFilterAdapter.this.setSelectedPosition(position);
                }
            });
        }

        private int getFilterColor(String filter) {
            switch (filter) {
                case "Còn tốt": return R.color.success_green;
                case "Sắp hết hạn": return R.color.warning_color;
                case "Đã hết hạn": return R.color.error_color;
                default: return R.color.primary_green_light;
            }
        }
    }
}
