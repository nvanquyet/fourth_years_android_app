package com.example.android_exam.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.android_exam.R;
import com.example.android_exam.data.local.database.AppDatabase;
import com.example.android_exam.data.local.entity.Ingredient;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class IngredientRepository {

    private static com.example.android_exam.data.repository.IngredientRepository instance;
    private MutableLiveData<List<Ingredient>> allIngredients;
    private MutableLiveData<List<Ingredient>> filteredIngredients;
    private List<Ingredient> ingredientList;

    private IngredientRepository() {
        allIngredients = new MutableLiveData<>();
        filteredIngredients = new MutableLiveData<>();
        ingredientList = new ArrayList<>();
        initSampleData();
    }

    public static synchronized com.example.android_exam.data.repository.IngredientRepository getInstance() {
        if (instance == null) {
            instance = new com.example.android_exam.data.repository.IngredientRepository();
        }
        return instance;
    }

    private void initSampleData() {
        ingredientList.add(new Ingredient("Cà chua", 2.5, "kg", "25/12/2024", "Rau củ", R.drawable.ic_vegetables));
        ingredientList.add(new Ingredient("Thịt bò", 1.5, "kg", "20/12/2024", "Thịt cá", R.drawable.ic_meat));
        ingredientList.add(new Ingredient("Muối", 500, "gram", "31/12/2025", "Gia vị", R.drawable.ic_spice));
        ingredientList.add(new Ingredient("Gạo tẻ", 5, "kg", "15/01/2025", "Ngũ cốc", R.drawable.ic_grain));
        ingredientList.add(new Ingredient("Táo", 1, "kg", "18/12/2024", "Trái cây", R.drawable.ic_fruit));
        ingredientList.add(new Ingredient("Sữa tươi", 1, "lít", "10/12/2024", "Sữa & trứng", R.drawable.ic_dairy));
        ingredientList.add(new Ingredient("Nước cam", 500, "ml", "15/12/2024", "Đồ uống", R.drawable.ic_drink));
        ingredientList.add(new Ingredient("Hành tây", 3, "cái", "22/12/2024", "Rau củ", R.drawable.ic_vegetables));

        allIngredients.setValue(new ArrayList<>(ingredientList));
        filteredIngredients.setValue(new ArrayList<>(ingredientList));
    }

    public LiveData<List<Ingredient>> getAllIngredients() {
        return allIngredients;
    }

    public LiveData<List<Ingredient>> getFilteredIngredients() {
        return filteredIngredients;
    }

    public void addIngredient(Ingredient ingredient) {
        ingredientList.add(ingredient);
        allIngredients.setValue(new ArrayList<>(ingredientList));
        applyCurrentFilters();
    }

    public void updateIngredient(Ingredient updatedIngredient) {
        for (int i = 0; i < ingredientList.size(); i++) {
            if (ingredientList.get(i).getId() == updatedIngredient.getId()) {
                ingredientList.set(i, updatedIngredient);
                break;
            }
        }
        allIngredients.setValue(new ArrayList<>(ingredientList));
        applyCurrentFilters();
    }

    public void deleteIngredient(String ingredientId) {
        ingredientList.removeIf(ingredient -> String.valueOf(ingredient.getId()).equals(ingredientId));
        allIngredients.setValue(new ArrayList<>(ingredientList));
        applyCurrentFilters();
    }

    public void searchIngredients(String query) {
        if (query == null || query.trim().isEmpty()) {
            filteredIngredients.setValue(new ArrayList<>(ingredientList));
            return;
        }
        String lowerQuery = query.toLowerCase().trim();
        List<Ingredient> filtered = ingredientList.stream()
                .filter(ingredient -> ingredient.getName().toLowerCase().contains(lowerQuery))
                .collect(Collectors.toList());
        filteredIngredients.setValue(filtered);
    }

    public void filterByCategory(String category) {
        if (category == null || category.equals("Tất cả")) {
            filteredIngredients.setValue(new ArrayList<>(ingredientList));
            return;
        }
        List<Ingredient> filtered = ingredientList.stream()
                .filter(ingredient -> ingredient.getCategory().equals(category))
                .collect(Collectors.toList());
        filteredIngredients.setValue(filtered);
    }

    public void filterByExpiryStatus(String status) {
        if (status == null || status.equals("Tất cả")) {
            filteredIngredients.setValue(new ArrayList<>(ingredientList));
            return;
        }
        List<Ingredient> filtered = ingredientList.stream()
                .filter(ingredient -> matchesExpiryFilter(ingredient, status))
                .collect(Collectors.toList());
        filteredIngredients.setValue(filtered);
    }

    private boolean matchesExpiryFilter(Ingredient ingredient, String filter) {
        int daysUntilExpiry = ingredient.getDaysUntilExpiry();
        switch (filter) {
            case "Còn tốt":
                return daysUntilExpiry > 7;
            case "Sắp hết hạn":
                return daysUntilExpiry >= 0 && daysUntilExpiry <= 7;
            case "Đã hết hạn":
                return daysUntilExpiry < 0;
            default:
                return true;
        }
    }

    public void sortIngredients(String sortMode) {
        List<Ingredient> currentFiltered = filteredIngredients.getValue();
        if (currentFiltered == null) return;
        List<Ingredient> sorted = new ArrayList<>(currentFiltered);
        switch (sortMode) {
            case "name":
                Collections.sort(sorted, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
                break;
            case "expiry":
                Collections.sort(sorted, Comparator.comparingInt(Ingredient::getDaysUntilExpiry));
                break;
            case "quantity":
                Collections.sort(sorted, (a, b) -> Double.compare(b.getQuantity(), a.getQuantity()));
                break;
            case "category":
                Collections.sort(sorted, (a, b) -> a.getCategory().compareToIgnoreCase(b.getCategory()));
                break;
        }
        filteredIngredients.setValue(sorted);
    }

    public void applyComplexFilter(String searchQuery, String category, String expiryFilter) {
        List<Ingredient> filtered = new ArrayList<>(ingredientList);
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            String lowerQuery = searchQuery.toLowerCase().trim();
            filtered = filtered.stream()
                    .filter(ingredient -> ingredient.getName().toLowerCase().contains(lowerQuery))
                    .collect(Collectors.toList());
        }
        if (category != null && !category.equals("Tất cả")) {
            filtered = filtered.stream()
                    .filter(ingredient -> ingredient.getCategory().equals(category))
                    .collect(Collectors.toList());
        }
        if (expiryFilter != null && !expiryFilter.equals("Tất cả")) {
            filtered = filtered.stream()
                    .filter(ingredient -> matchesExpiryFilter(ingredient, expiryFilter))
                    .collect(Collectors.toList());
        }
        filteredIngredients.setValue(filtered);
    }

    private void applyCurrentFilters() {
        filteredIngredients.setValue(new ArrayList<>(ingredientList));
    }

    public int getTotalCount() {
        return ingredientList.size();
    }

    public int getExpiringSoonCount() {
        return (int) ingredientList.stream()
                .filter(ingredient -> {
                    int days = ingredient.getDaysUntilExpiry();
                    return days >= 0 && days <= 7;
                })
                .count();
    }

    public int getExpiredCount() {
        return (int) ingredientList.stream()
                .filter(ingredient -> ingredient.getDaysUntilExpiry() < 0)
                .count();
    }

    public Ingredient getIngredientById(int id) {
        return ingredientList.stream()
                .filter(ingredient -> ingredient.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("Tất cả");
        categories.addAll(ingredientList.stream()
                .map(Ingredient::getCategory)
                .distinct()
                .sorted()
                .collect(Collectors.toList()));
        return categories;
    }

    public void loadIngredients() {
        allIngredients.setValue(new ArrayList<>(ingredientList));
        filteredIngredients.setValue(new ArrayList<>(ingredientList));
    }

    // Cache để tránh tạo quá nhiều LiveData objects
    private static MutableLiveData<List<Ingredient>> cachedExpiringIngredients;
    private static MutableLiveData<List<Ingredient>> cachedAllIngredients;
    private static int lastUserId = -1;

    public static LiveData<List<Ingredient>> getExpiringIngredients(int userId) {
        return getExpiringIngredients(userId, 10);
    }

    public static LiveData<List<Ingredient>> getExpiringIngredients(int userId, int days) {
        // Kiểm tra cache nếu cùng userId
        if (cachedExpiringIngredients != null && lastUserId == userId) {
            return cachedExpiringIngredients;
        }

        // Tạo mới nếu chưa có hoặc userId khác
        cachedExpiringIngredients = new MutableLiveData<>();
        lastUserId = userId;

        // Load data một lần
        loadExpiringIngredientsFromDB(userId, days);

        return cachedExpiringIngredients;
    }

    private static void loadExpiringIngredientsFromDB(int userId, int days) {
        AppDatabase.getExpiringIngredients(userId, days, new AppDatabase.DatabaseCallback<List<Ingredient>>() {
            @Override
            public void onSuccess(List<Ingredient> result) {
                if (cachedExpiringIngredients != null) {
                    cachedExpiringIngredients.postValue(result != null ? result : new ArrayList<>());
                }
            }

            @Override
            public void onError(String error) {
                if (cachedExpiringIngredients != null) {
                    cachedExpiringIngredients.postValue(new ArrayList<>());
                }
            }
        });
    }

    public static LiveData<List<Ingredient>> getAllIngredient(int userId) {
        // Tương tự cho getAllIngredient
        if (cachedAllIngredients != null && lastUserId == userId) {
            return cachedAllIngredients;
        }

        cachedAllIngredients = new MutableLiveData<>();

        AppDatabase.getAllIngredients(userId, new AppDatabase.DatabaseCallback<List<Ingredient>>() {
            @Override
            public void onSuccess(List<Ingredient> result) {
                if (cachedAllIngredients != null) {
                    cachedAllIngredients.postValue(result != null ? result : new ArrayList<>());
                }
            }

            @Override
            public void onError(String error) {
                if (cachedAllIngredients != null) {
                    cachedAllIngredients.postValue(new ArrayList<>());
                }
            }
        });

        return cachedAllIngredients;
    }

    // Method để refresh data khi cần
    public static void refreshExpiringIngredients(int userId) {
        if (cachedExpiringIngredients != null) {
            loadExpiringIngredientsFromDB(userId, 10);
        }
    }

    // Method để clear cache khi cần
    public static void clearCache() {
        cachedExpiringIngredients = null;
        cachedAllIngredients = null;
        lastUserId = -1;
    }
}