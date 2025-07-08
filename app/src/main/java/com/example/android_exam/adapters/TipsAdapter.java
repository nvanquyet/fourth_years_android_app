package com.example.android_exam.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_exam.R;

import java.util.ArrayList;
import java.util.List;

public class TipsAdapter extends RecyclerView.Adapter<TipsAdapter.TipViewHolder> {

    private List<String> tips = new ArrayList<>();

    public void setTips(List<String> tips) {
        this.tips = tips != null ? tips : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tip_food, parent, false);
        return new TipViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TipViewHolder holder, int position) {
        String tip = tips.get(position);
        holder.bind(tip, position + 1);
    }

    @Override
    public int getItemCount() {
        return tips.size();
    }

    class TipViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTipNumber;
        private TextView tvTipContent;

        public TipViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTipNumber = itemView.findViewById(R.id.textView);
            tvTipContent = itemView.findViewById(R.id.tv_tip_content);
        }

        public void bind(String tip, int tipNumber) {
            tvTipNumber.setText(String.valueOf(tipNumber));
            tvTipContent.setText(tip);
        }
    }
}