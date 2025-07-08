package com.example.android_exam.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.android_exam.R;
import com.example.android_exam.data.dto.ingredient.IngredientDataResponseDto;
import com.example.android_exam.utils.IngredientUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

public class IngredientAdapter extends ListAdapter<IngredientDataResponseDto, IngredientAdapter.IngredientViewHolder> {

    private OnIngredientActionListener listener;
    private Context context;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public interface OnIngredientActionListener {
        void onIngredientClick(IngredientDataResponseDto ingredient);
        void onEditIngredient(IngredientDataResponseDto ingredient);
        void onDeleteIngredient(IngredientDataResponseDto ingredient);
        void onViewDetails(IngredientDataResponseDto ingredient);
    }

    public IngredientAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    public void setOnIngredientActionListener(OnIngredientActionListener listener) {
        this.listener = listener;
    }
    private static final DiffUtil.ItemCallback<IngredientDataResponseDto> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<IngredientDataResponseDto>() {
                @Override
                public boolean areItemsTheSame(@NonNull IngredientDataResponseDto oldItem, @NonNull IngredientDataResponseDto newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull IngredientDataResponseDto oldItem, @NonNull IngredientDataResponseDto newItem) {
                    // So sánh tất cả các trường quan trọng
                    return oldItem.getName().equals(newItem.getName()) &&
                            oldItem.getQuantity().compareTo(newItem.getQuantity()) == 0 &&
                            compareExpiryDates(oldItem.getExpiryDate(), newItem.getExpiryDate()) &&
                            oldItem.getUnit().equals(newItem.getUnit()) &&
                            oldItem.getCategory().equals(newItem.getCategory()) &&
                            oldItem.isExpired() == newItem.isExpired() &&
                            compareStrings(oldItem.getDescription(), newItem.getDescription()) &&
                            compareStrings(oldItem.getImageUrl(), newItem.getImageUrl());
                }

                private boolean compareExpiryDates(Date date1, Date date2) {
                    if (date1 == null && date2 == null) return true;
                    if (date1 == null || date2 == null) return false;
                    return date1.getTime() == date2.getTime();
                }

                private boolean compareExpiryDates(LocalDateTime date1, LocalDateTime date2) {
                    if (date1 == null && date2 == null) return true;
                    if (date1 == null || date2 == null) return false;
                    return date1.equals(date2);
                }

                private boolean compareStrings(String str1, String str2) {
                    if (str1 == null && str2 == null) return true;
                    if (str1 == null || str2 == null) return false;
                    return str1.equals(str2);
                }
            };
    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);
        return new IngredientViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        IngredientDataResponseDto ingredient = getItem(position);
        holder.bind(ingredient);
    }



    class IngredientViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgIngredient;
        private TextView tvIngredientName;
        private TextView tvQuantity;
        private TextView tvCategory;
        private TextView tvExpiryDate;
        //private View statusIndicator;
        //private ImageView ivMore;

        public IngredientViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIngredient = itemView.findViewById(R.id.imgIngredient);
            tvIngredientName = itemView.findViewById(R.id.tvIngredientName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvExpiryDate = itemView.findViewById(R.id.tvExpiryDate);
            //statusIndicator = itemView.findViewById(R.id.statusIndicator);
            //ivMore = itemView.findViewById(R.id.ivMore);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onIngredientClick(getItem(position));
                }
            });

            //ivMore.setOnClickListener(v -> showPopupMenu(v, getAdapterPosition()));
        }

        public void bind(IngredientDataResponseDto ingredient) {
            tvIngredientName.setText(ingredient.getName());
            String category = IngredientUtils.getCategoryDisplayName(ingredient.getCategory());
            String quantity = IngredientUtils.formatQuantity(ingredient.getQuantity().doubleValue(), ingredient.getUnit());
            tvCategory.setText(category);
            tvQuantity.setText(quantity);
            setExpiryDateAndStatus(ingredient);
            loadIngredientImage(ingredient);
        }

        private void setExpiryDateAndStatus(IngredientDataResponseDto ingredient) {
            String expiryText;
            int statusColor;
            int textColor;

            if (ingredient.getExpiryDate() != null) {
                String formattedDate = dateFormat.format(ingredient.getExpiryDate());

                if (ingredient.isExpired()) {
                    statusColor = ContextCompat.getColor(context, R.color.expiry_danger);
                    textColor = ContextCompat.getColor(context, R.color.expiry_danger);
                    expiryText = "Đã hết hạn: " + formattedDate;
                } else {
                    statusColor = ContextCompat.getColor(context, R.color.expiry_fresh);
                    textColor = ContextCompat.getColor(context, android.R.color.darker_gray);
                    expiryText = "Hết hạn: " + formattedDate;
                }
            } else {
                statusColor = ContextCompat.getColor(context, android.R.color.darker_gray);
                textColor = ContextCompat.getColor(context, android.R.color.darker_gray);
                expiryText = "Chưa có ngày hết hạn";
            }

            tvExpiryDate.setText(expiryText);
            tvExpiryDate.setTextColor(textColor);
            //statusIndicator.setBackgroundColor(statusColor);
        }

        private void loadIngredientImage(IngredientDataResponseDto ingredient) {
            if (ingredient.getImageUrl() != null && !ingredient.getImageUrl().isEmpty()) {
                //Log check image URL
                Log.d("IngredientAdapter", "Loading image: " + ingredient.getImageUrl());
                Glide.with(context)
                        .load(ingredient.getImageUrl())
                        .placeholder(R.drawable.ic_ingredient_placeholder)
                        .error(R.drawable.ic_ingredient_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .circleCrop()
                        .into(imgIngredient);
            } else {
                Log.d("IngredientAdapter", "Loading image: false ");
                imgIngredient.setImageResource(IngredientUtils.getDefaultImageForCategory(ingredient.getCategory()));
            }
        }

        private void showPopupMenu(View view, int position) {
            if (position == RecyclerView.NO_POSITION || listener == null) return;

            PopupMenu popup = new PopupMenu(context, view);
            popup.getMenuInflater().inflate(R.menu.menu_ingredient_item, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                IngredientDataResponseDto ingredient = getItem(position);
                int itemId = item.getItemId();

                if (itemId == R.id.action_edit) {
                    listener.onEditIngredient(ingredient);
                    return true;
                } else if (itemId == R.id.action_delete) {
                    listener.onDeleteIngredient(ingredient);
                    return true;
                } else if (itemId == R.id.action_view_details) {
                    listener.onViewDetails(ingredient);
                    return true;
                }
                return false;
            });

            popup.show();
        }
    }
}