package com.example.android_exam.utils;
import com.example.android_exam.R;
import com.example.android_exam.data.models.enums.IngredientCategory;
import com.example.android_exam.data.models.enums.IngredientUnit;

import java.util.Locale;

public class IngredientUtils {

    public static String getCategoryDisplayName(IngredientCategory category) {
        if (category == null) {
            return "Khác";
        }
        return switch (category) {
            case VEGETABLES -> "Rau củ quả";
            case FRUITS -> "Trái cây";
            case MEAT -> "Thịt";
            case DAIRY -> "Sản phẩm từ sữa";
            case GRAINS -> "Ngũ cốc";
            case SEAFOOD -> "Hải sản";
            case BEVERAGES -> "Đồ uống";
            case CONDIMENTS -> "Nước chấm và gia vị";
            case SNACKS -> "Đồ ăn nhẹ";
            case FROZEN -> "Thực phẩm đông lạnh";
            case CANNED -> "Thực phẩm đóng hộp";
            case SPICES -> "Gia vị";
            default -> "Khác";
        };
    }

    public static String getUnitDisplayName(IngredientUnit unit) {
        if (unit == null) {
            return "Cái";
        }
        return switch (unit) {
            case PIECE -> "Cái";
            case KILOGRAM -> "Kilogram";
            case GRAM -> "Gram";
            case LITER -> "Lít";
            case MILLILITER -> "Mililit";
            case CUP -> "Cốc";
            case TABLESPOON -> "Muỗng canh";
            case TEASPOON -> "Muỗng cà phê";
            case PACKAGE -> "Gói";
            case BOTTLE -> "Chai";
            case CAN -> "Hộp";
            default -> "Cái";
        };
    }


    public static IngredientCategory parseCategory(String categoryStr) {
        if (categoryStr == null || categoryStr.isEmpty()) {
            return IngredientCategory.OTHER;
        }
        return switch (categoryStr) {
            case "Rau củ quả" -> IngredientCategory.VEGETABLES;
            case "Trái cây" -> IngredientCategory.FRUITS;
            case "Thịt" -> IngredientCategory.MEAT;
            case "Sản phẩm từ sữa" -> IngredientCategory.DAIRY;
            case "Ngũ cốc" -> IngredientCategory.GRAINS;
            case "Hải sản" -> IngredientCategory.SEAFOOD;
            case "Đồ uống" -> IngredientCategory.BEVERAGES;
            case "Nước chấm và gia vị" -> IngredientCategory.CONDIMENTS;
            case "Đồ ăn nhẹ" -> IngredientCategory.SNACKS;
            case "Thực phẩm đông lạnh" -> IngredientCategory.FROZEN;
            case "Thực phẩm đóng hộp" -> IngredientCategory.CANNED;
            case "Gia vị" -> IngredientCategory.SPICES;
            default -> {
                yield IngredientCategory.OTHER;
            }
        };
    }

    public static IngredientUnit parseUnit(String unitStr) {
        if (unitStr == null || unitStr.isEmpty()) {
            return IngredientUnit.PIECE;
        }
        return switch (unitStr.toLowerCase()) {
            case "kilogram", "kg" -> IngredientUnit.KILOGRAM;
            case "gram", "g" -> IngredientUnit.GRAM;
            case "lít", "liter", "l" -> IngredientUnit.LITER;
            case "mililit", "milliliter", "ml" -> IngredientUnit.MILLILITER;
            case "cốc", "cup" -> IngredientUnit.CUP;
            case "muỗng canh", "tablespoon" -> IngredientUnit.TABLESPOON;
            case "muỗng cà phê", "teaspoon" -> IngredientUnit.TEASPOON;
            case "gói", "package" -> IngredientUnit.PACKAGE;
            case "chai", "bottle" -> IngredientUnit.BOTTLE;
            case "hộp", "lon", "can" -> IngredientUnit.CAN;
            case "cái", "piece" -> IngredientUnit.PIECE;
            default -> {
                yield IngredientUnit.PIECE;
            }
        };
    }

    public static String formatQuantity(double quantity, IngredientUnit unit) {
        String quantityStr;
        if (quantity == (int) quantity) {
            quantityStr = String.valueOf((int) quantity);
        } else {
            quantityStr = String.format(Locale.getDefault(), "%.1f", quantity);
        }
        return quantityStr + " " + getUnitDisplayName(unit);
    }

    public static int getDefaultImageForCategory(IngredientCategory category) {
        if (category == null) {
            return R.drawable.ic_ingredient_placeholder;
        }
        return switch (category) {
            case VEGETABLES -> R.drawable.ic_vegetables;
            case FRUITS -> R.drawable.ic_fruits;
            case MEAT -> R.drawable.ic_meat;
            case DAIRY -> R.drawable.ic_dairy;
            case GRAINS -> R.drawable.ic_grains;
            case BEVERAGES -> R.drawable.ic_beverages;
            case SPICES -> R.drawable.ic_spices;
            case FROZEN -> R.drawable.ic_frozen;
            case CANNED -> R.drawable.ic_canned;
            default -> R.drawable.ic_ingredient_placeholder;
        };
    }
}