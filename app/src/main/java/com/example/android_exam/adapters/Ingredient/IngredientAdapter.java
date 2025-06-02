package com.example.android_exam.adapters.Ingredient;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_exam.R;
import com.example.android_exam.data.local.entity.Ingredient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    private Context context;
    private List<Ingredient> ingredientList;
    private List<Ingredient> ingredientListFull; // For search functionality
    private OnIngredientItemClickListener listener;
    private SimpleDateFormat dateFormat;

    public interface OnIngredientItemClickListener {
        void onIngredientClick(Ingredient ingredient);
        void onEditClick(Ingredient ingredient);
        void onDeleteClick(Ingredient ingredient);
        void onDuplicateClick(Ingredient ingredient);
        void onInfoClick(Ingredient ingredient); // New method for product info
    }

    public IngredientAdapter(Context context) {
        this.context = context;
        this.ingredientList = new ArrayList<>();
        this.ingredientListFull = new ArrayList<>();
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    public void setOnIngredientItemClickListener(OnIngredientItemClickListener listener) {
        this.listener = listener;
    }

    public void setIngredientList(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
        this.ingredientListFull = new ArrayList<>(ingredientList);
        notifyDataSetChanged();
    }

    public void addIngredient(Ingredient ingredient) {
        ingredientList.add(0, ingredient);
        ingredientListFull.add(0, ingredient);
        notifyItemInserted(0);
    }

    public void updateIngredient(Ingredient updatedIngredient) {
        for (int i = 0; i < ingredientList.size(); i++) {
            if (ingredientList.get(i).getId().== updatedIngredient.getId()) {
                ingredientList.set(i, updatedIngredient);
                notifyItemChanged(i);
                break;
            }
        }

        for (int i = 0; i < ingredientListFull.size(); i++) {
            if (ingredientListFull.get(i).getId() == updatedIngredient.getId()) {
                ingredientListFull.set(i, updatedIngredient);
                break;
            }
        }
    }

    public void removeIngredient(int ingredientId) {
        for (int i = 0; i < ingredientList.size(); i++) {
            if (ingredientList.get(i).getId() == ingredientId) {
                ingredientList.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }

        for (int i = 0; i < ingredientListFull.size(); i++) {
            if (ingredientListFull.get(i).getId() == ingredientId) {
                ingredientListFull.remove(i);
                break;
            }
        }
    }

    public void filter(String query) {
        ingredientList.clear();
        if (query.isEmpty()) {
            ingredientList.addAll(ingredientListFull);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (Ingredient ingredient : ingredientListFull) {
                if (ingredient.getName().toLowerCase().contains(lowerCaseQuery) ||
                        ingredient.getCategory().toLowerCase().contains(lowerCaseQuery)) {
                    ingredientList.add(ingredient);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filterByCategory(String category) {
        ingredientList.clear();
        if (category == null || category.equals("Tất cả")) {
            ingredientList.addAll(ingredientListFull);
        } else {
            for (Ingredient ingredient : ingredientListFull) {
                if (ingredient.getCategory().equals(category)) {
                    ingredientList.add(ingredient);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void filterByExpiryStatus(String status) {
        ingredientList.clear();
        for (Ingredient ingredient : ingredientListFull) {
            boolean shouldAdd = false;
            switch (status) {
                case "Tất cả":
                    shouldAdd = true;
                    break;
                case "Hết hạn":
                    shouldAdd = ingredient.isExpired();
                    break;
                case "Sắp hết hạn":
                    shouldAdd = ingredient.isExpiringSoon();
                    break;
                case "Còn tươi":
                    //shouldAdd = ingredient.getExpiryStatus() == Ingredient.ExpiryStatus.FRESH;
                    break;
            }
            if (shouldAdd) {
                ingredientList.add(ingredient);
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Ingredient ingredient = ingredientList.get(position);
        holder.bind(ingredient);
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    public class IngredientViewHolder extends RecyclerView.ViewHolder {
        private TextView tvIngredientName, tvQuantity, tvExpiryDate;
        private ImageView ivIngredientIcon, btnEditIngredient, btnDeleteIngredient, btnInfoIngredient;
        private View vExpiryIndicator;
        private com.google.android.material.chip.Chip chipCategory;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);

            tvIngredientName = itemView.findViewById(R.id.tv_ingredient_name);
            tvQuantity = itemView.findViewById(R.id.tv_ingredient_quantity);
            tvExpiryDate = itemView.findViewById(R.id.tv_expiry_date);
            ivIngredientIcon = itemView.findViewById(R.id.iv_ingredient_icon);
            btnEditIngredient = itemView.findViewById(R.id.btn_edit_ingredient);
            btnDeleteIngredient = itemView.findViewById(R.id.btn_delete_ingredient);
            btnInfoIngredient = itemView.findViewById(R.id.btn_info_ingredient);
            vExpiryIndicator = itemView.findViewById(R.id.v_expiry_indicator);
            chipCategory = itemView.findViewById(R.id.chip_category);

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onIngredientClick(ingredientList.get(getAdapterPosition()));
                }
            });

            btnEditIngredient.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onEditClick(ingredientList.get(getAdapterPosition()));
                }
            });

            btnDeleteIngredient.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onDeleteClick(ingredientList.get(getAdapterPosition()));
                }
            });

            btnInfoIngredient.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onInfoClick(ingredientList.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Ingredient ingredient) {
            tvIngredientName.setText(ingredient.getName());
            chipCategory.setText(ingredient.getCategory());
            tvQuantity.setText(ingredient.getQuantityWithUnit());

            if (ingredient.getExpiryDate() != null) {
                tvExpiryDate.setText(dateFormat.format(ingredient.getExpiryDate()));
            } else {
                tvExpiryDate.setText("Chưa xác định");
            }

            // Set expiry status indicator
            Ingredient.ExpiryStatus status = ingredient.getExpiryStatus();
            updateStatusIndicator(status);

            // Set ingredient image based on category
            setIngredientImage(ingredient.getCategory());
        }

        private void updateStatusIndicator(Ingredient.ExpiryStatus status) {
            int color;
            switch (status) {
                case EXPIRED:
                    color = ContextCompat.getColor(context, R.color.error_color);
                    break;
                case EXPIRING_SOON:
                    color = ContextCompat.getColor(context, R.color.warning_color);
                    break;
                case EXPIRING_WEEK:
                    color = ContextCompat.getColor(context, R.color.caution_color);
                    break;
                case FRESH:
                default:
                    color = ContextCompat.getColor(context, R.color.success_color);
                    break;
            }
            vExpiryIndicator.setBackgroundColor(color);
        }

        private void setIngredientImage(String category) {
            int imageRes;
            switch (category) {
                case "Thịt":
                    imageRes = R.drawable.ic_meat;
                    break;
                case "Cá - Hải sản":
                    imageRes = R.drawable.ic_fish;
                    break;
                case "Rau củ":
                    imageRes = R.drawable.ic_vegetables;
                    break;
                case "Trái cây":
                    imageRes = R.drawable.ic_fruits;
                    break;
                case "Sữa - Phô mai":
                    imageRes = R.drawable.ic_dairy;
                    break;
                case "Bánh mì - Ngũ cốc":
                    imageRes = R.drawable.ic_bread;
                    break;
                case "Đồ uống":
                    imageRes = R.drawable.ic_drinks;
                    break;
                case "Gia vị":
                    imageRes = R.drawable.ic_spices;
                    break;
                case "Đồ đóng hộp":
                    imageRes = R.drawable.ic_canned;
                    break;
                case "Đồ đông lạnh":
                    imageRes = R.drawable.ic_frozen;
                    break;
                default:
                    imageRes = R.drawable.ic_food_default;
                    break;
            }
            ivIngredientIcon.setImageResource(imageRes);
        }
    }
}