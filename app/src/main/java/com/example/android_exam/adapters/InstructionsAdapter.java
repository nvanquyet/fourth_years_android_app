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

public class InstructionsAdapter extends RecyclerView.Adapter<InstructionsAdapter.InstructionViewHolder> {

    private List<String> instructions = new ArrayList<>();

    public void setInstructions(List<String> instructions) {
        this.instructions = instructions != null ? instructions : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InstructionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_instruction, parent, false);
        return new InstructionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionViewHolder holder, int position) {
        String instruction = instructions.get(position);
        holder.bind(instruction, position + 1);
    }

    @Override
    public int getItemCount() {
        return instructions.size();
    }

    class InstructionViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStepNumber;
        private TextView tvStepContent;

        public InstructionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStepNumber = itemView.findViewById(R.id.textView);
            tvStepContent = itemView.findViewById(R.id.tv_step_content);
        }

        public void bind(String instruction, int stepNumber) {
            tvStepNumber.setText(String.valueOf(stepNumber));
            tvStepContent.setText(instruction);
        }
    }
}