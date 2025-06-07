package com.example.android_exam.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.android_exam.R;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static androidx.room.ForeignKey.CASCADE;
import static com.example.android_exam.data.local.entity.ExpiryStatus.EXPIRED;
import static com.example.android_exam.data.local.entity.ExpiryStatus.EXPIRING_SOON;
import static com.example.android_exam.data.local.entity.ExpiryStatus.FRESH;

@Entity(
        tableName = "ingredients",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = CASCADE
        ),
        indices = {@Index(value = {"userId"})}
)
public class Ingredient {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int userId;

    @NonNull
    public String name;

    public double quantity;
    public String unit;
    public long expiryDateTimestamp; // Lưu dưới dạng milliseconds since epoch
    public String category;
    private int iconResource;

    public Ingredient() {
        this.name = "Unknown";
        this.quantity = 0.0;
        this.unit = "";
        this.expiryDateTimestamp = 0L;
        this.category = "";
        this.iconResource = R.drawable.ic_food_default;
    }

    @Ignore
    public Ingredient(@NonNull String name, double quantity, String unit, long expiryDateTimestamp,
                      String category, int iconResource) {
        this.name = name.isEmpty() ? "Unknown" : name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDateTimestamp = expiryDateTimestamp;
        this.category = category;
        this.iconResource = iconResource;
    }

    // Getters and setters
    @NonNull
    public String getName() { return name; }
    public void setName(@NonNull String name) { this.name = name.isEmpty() ? "Unknown" : name; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public long getExpiryDateTimestamp() { return expiryDateTimestamp; }
    public void setExpiryDateTimestamp(long expiryDateTimestamp) { this.expiryDateTimestamp = expiryDateTimestamp; }

    public void setExpiryDateTimestamp(String expiryDate) {
        // Chuyển đổi chuỗi ngày tháng sang timestamp
        LocalDate date = LocalDate.parse(expiryDate);
        Instant instant = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Date expiryDateTimestamp = Date.from(instant);
        // Lưu timestamp dưới dạng milliseconds since epoch
        this.expiryDateTimestamp = expiryDateTimestamp.getTime();
    }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getId() { return id; }

    public int getIconResource() { return iconResource; }
    public void setIconResource(int iconResource) { this.iconResource = iconResource; }

    public String getFormattedQuantity() {
        if (quantity == (int) quantity) {
            return String.valueOf((int) quantity) + " " + unit;
        } else {
            return String.valueOf(quantity) + " " + unit;
        }
    }

    public int getDaysUntilExpiry() {
        long currentTime = System.currentTimeMillis();
        long diffInMillies = expiryDateTimestamp - currentTime;
        return (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public ExpiryStatus getExpiryStatus() {
        int days = getDaysUntilExpiry();
        if (days < 0) return EXPIRED;
        else if (days <= 7) return EXPIRING_SOON;
        else return FRESH;
    }

    public int getExpiryStatusColor() {
        switch (getExpiryStatus()) {
            case EXPIRED:
                return android.R.color.holo_red_light;
            case EXPIRING_SOON:
                return android.R.color.holo_orange_light;
            case FRESH:
            default:
                return android.R.color.holo_green_light;
        }
    }

    public String getExpiryDate(){
        Instant instant = Instant.ofEpochMilli(expiryDateTimestamp);
        LocalDate date = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        return date.toString(); // Trả về định dạng YYYY-MM-DD
    }

    private int getCategoryIcon(String category) {
        switch (category) {
            case "Rau củ": return R.drawable.ic_vegetables;
            case "Thịt cá": return R.drawable.ic_meat;
            case "Gia vị": return R.drawable.ic_spice;
            case "Ngũ cốc": return R.drawable.ic_grain;
            case "Trái cây": return R.drawable.ic_fruit;
            case "Sữa & trứng": return R.drawable.ic_dairy;
            case "Đồ uống": return R.drawable.ic_drink;
            case "Đồ khô": return R.drawable.ic_dry_food;
            default: return R.drawable.ic_food_default;
        }
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", unit='" + unit + '\'' +
                ", expiryDateTimestamp=" + expiryDateTimestamp +
                ", category='" + category + '\'' +
                '}';
    }
}