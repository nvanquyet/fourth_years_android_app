package com.example.android_exam.adapters.Home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.android_exam.R;
import com.example.android_exam.data.local.entity.Ingredient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ExpiringIngredientsAdapter extends RecyclerView.Adapter<ExpiringIngredientsAdapter.ViewHolder> {

    private List<Ingredient> ingredients;
    private Context context;
    private OnIngredientClickListener listener;
    private String[] categories;

    public interface OnIngredientClickListener {
        void onIngredientClick(Ingredient ingredient);
    }

    public ExpiringIngredientsAdapter(Context context, List<Ingredient> ingredients) {
        this.context = context;
        this.ingredients = ingredients;
        this.categories = context.getResources().getStringArray(R.array.categories_array);
    }

    public void setOnIngredientClickListener(OnIngredientClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ingredient_compact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.bind(ingredient);
    }

    @Override
    public int getItemCount() {
        return ingredients != null ? ingredients.size() : 0;
    }


    public void updateIngredients(List<Ingredient> newIngredients) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return ingredients != null ? ingredients.size() : 0;
            }

            @Override
            public int getNewListSize() {
                return newIngredients != null ? newIngredients.size() : 0;
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                // So sánh theo ID hoặc unique key nếu có
                return ingredients.get(oldItemPosition).id ==
                        newIngredients.get(newItemPosition).id;
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                // So sánh toàn bộ nội dung
                return ingredients.get(oldItemPosition).equals(newIngredients.get(newItemPosition));
            }
        });

        this.ingredients = newIngredients;
        diffResult.dispatchUpdatesTo(this);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivIngredientIcon;
        private TextView tvIngredientName;
        private TextView tvIngredientQuantity;
        private TextView tvExpiryDate;
        private TextView tvDaysLeft;
        private View vExpiryIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivIngredientIcon = itemView.findViewById(R.id.iv_ingredient_icon);
            tvIngredientName = itemView.findViewById(R.id.tv_ingredient_name);
            tvIngredientQuantity = itemView.findViewById(R.id.tv_ingredient_quantity);
            tvExpiryDate = itemView.findViewById(R.id.tv_expiry_date);
            tvDaysLeft = itemView.findViewById(R.id.tv_days_left);
            vExpiryIndicator = itemView.findViewById(R.id.v_expiry_indicator);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onIngredientClick(ingredients.get(getAdapterPosition()));
                }
            });
        }

        public void bind(Ingredient ingredient) {
            if (ingredient == null) return;
            // Set ingredient name
            tvIngredientName.setText(ingredient.getName());

            // Set quantity
            tvIngredientQuantity.setText(ingredient.getQuantity() + " " + ingredient.getUnit());

            // Set expiry date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            tvExpiryDate.setText(dateFormat.format(ingredient.getExpiryDate()));

            // Calculate days left and set colors
            long daysLeft = calculateDaysLeft(ingredient.getExpiryDate());
            setExpiryStatus(daysLeft);

            // Set ingredient icon based on category
            setIngredientIcon(ingredient.getCategory());
        }

        private long calculateDaysLeft(String expiryDate) {
            try {
                // Create a SimpleDateFormat to parse the String to a Date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Adjust the format to match your input string
                Date parsedExpiryDate = sdf.parse(expiryDate);
                Date currentDate = new Date();
                long diffInMillies = parsedExpiryDate.getTime() - currentDate.getTime();
                return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            } catch (ParseException e) {
                e.printStackTrace();
                return -1;
            }
        }

        private void setExpiryStatus(long daysLeft) {
            if (daysLeft < 0) {
                // Expired
                tvDaysLeft.setText("Đã hết hạn");
                tvDaysLeft.setBackgroundResource(R.drawable.expired_background);
                vExpiryIndicator.setBackgroundResource(R.drawable.expiry_indicator_red);
            } else if (daysLeft == 0) {
                // Expires today
                tvDaysLeft.setText("Hết hạn hôm nay");
                tvDaysLeft.setBackgroundResource(R.drawable.expires_today_background);
                vExpiryIndicator.setBackgroundResource(R.drawable.expiry_indicator_red);
            } else if (daysLeft <= 3) {
                // Expires in 1-3 days
                tvDaysLeft.setText("Còn " + daysLeft + " ngày");
                tvDaysLeft.setBackgroundResource(R.drawable.expires_soon_background);
                vExpiryIndicator.setBackgroundResource(R.drawable.expiry_indicator_yellow);
            } else if (daysLeft <= 7) {
                // Expires in 4-7 days
                tvDaysLeft.setText("Còn " + daysLeft + " ngày");
                tvDaysLeft.setBackgroundResource(R.drawable.expires_week_background);
                vExpiryIndicator.setBackgroundResource(R.drawable.expiry_indicator_yellow);
            } else {
                // Fresh
                tvDaysLeft.setText("Còn " + daysLeft + " ngày");
                tvDaysLeft.setBackgroundResource(R.drawable.fresh_background);
                vExpiryIndicator.setBackgroundResource(R.drawable.expiry_indicator_green);
            }
        }

        private void setIngredientIcon(String category) {
            // Convert input category to lowercase for case-insensitive comparison
            String categoryLower = category != null ? category.toLowerCase() : "";

            // Set icon based on category
            if (categoryLower.equals(categories[0].toLowerCase()) || categoryLower.equals("thịt")) {
                ivIngredientIcon.setImageResource(R.drawable.ic_meat);
            } else if (categoryLower.equals(categories[1].toLowerCase()) || categoryLower.equals("rau củ")) {
                ivIngredientIcon.setImageResource(R.drawable.ic_vegetables);
            } else if (categoryLower.equals(categories[2].toLowerCase()) || categoryLower.equals("trái cây")) {
                ivIngredientIcon.setImageResource(R.drawable.ic_fruits);
            } else if (categoryLower.equals(categories[3].toLowerCase()) || categoryLower.equals("sữa")) {
                ivIngredientIcon.setImageResource(R.drawable.ic_dairy);
            } else if (categoryLower.equals(categories[4].toLowerCase())) {
                ivIngredientIcon.setImageResource(R.drawable.ic_grain);
            } else {
                ivIngredientIcon.setImageResource(R.drawable.ic_food_default);
            }
        }
    }
}