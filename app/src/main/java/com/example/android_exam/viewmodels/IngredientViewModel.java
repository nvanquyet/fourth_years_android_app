package com.example.android_exam.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.android_exam.R;
import com.example.android_exam.data.local.entity.Ingredient;
import com.example.android_exam.data.repository.IngredientRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class IngredientViewModel extends ViewModel {
    private IngredientRepository repository;

    // Filter states
    private MutableLiveData<String> selectedCategory;
    private MutableLiveData<String> selectedExpiryFilter;
    private MutableLiveData<String> currentSortMode;
    private MutableLiveData<String> searchQuery;

    // UI states
    private MutableLiveData<Statistics> statistics;
    private MutableLiveData<Boolean> isLoading;
    private MutableLiveData<String> errorMessage;
    private MutableLiveData<Boolean> fabMenuOpen;
    private MutableLiveData<List<String>> allCategories;
    private MutableLiveData<List<String>> expiryFilters;

    public IngredientViewModel() {
        repository = IngredientRepository.getInstance();

        // Initialize filter states
        selectedCategory = new MutableLiveData<>("Tất cả");
        selectedExpiryFilter = new MutableLiveData<>("Tất cả");
        currentSortMode = new MutableLiveData<>("name");
        searchQuery = new MutableLiveData<>("");

        // Initialize UI states
        statistics = new MutableLiveData<>();
        isLoading = new MutableLiveData<>(false);
        errorMessage = new MutableLiveData<>();
        fabMenuOpen = new MutableLiveData<>(false);
        allCategories = new MutableLiveData<>();
        expiryFilters = new MutableLiveData<>(Arrays.asList("Tất cả", "Còn tốt", "Sắp hết hạn", "Đã hết hạn"));

        // Load initial data
        loadIngredients();
        updateCategories();
    }

    // Repository LiveData
    public LiveData<List<Ingredient>> getAllIngredients() {
        return repository.getAllIngredients();
    }

    public LiveData<List<Ingredient>> getFilteredIngredients() {
        return repository.getFilteredIngredients();
    }

    public LiveData<List<String>> getAllCategories() {
        return allCategories;
    }

    public LiveData<List<String>> getExpiryFilters() {
        return expiryFilters;
    }

    // Filter states LiveData
    public LiveData<String> getSelectedCategory() { return selectedCategory; }
    public LiveData<String> getSelectedExpiryFilter() { return selectedExpiryFilter; }
    public LiveData<String> getCurrentSortMode() { return currentSortMode; }
    public LiveData<String> getSearchQuery() { return searchQuery; }

    // UI states LiveData
    public LiveData<Statistics> getStatistics() { return statistics; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getFabMenuOpen() { return fabMenuOpen; }

    // Data operations
    public void loadIngredients() {
        isLoading.setValue(true);
        try {
            repository.loadIngredients();
            updateStatistics();
            updateCategories();
            isLoading.setValue(false);
        } catch (Exception e) {
            isLoading.setValue(false);
            errorMessage.setValue("Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    public void addIngredient(Ingredient ingredient) {
        try {
            repository.addIngredient(ingredient);
            updateStatistics();
            updateCategories();
            errorMessage.setValue(null);
        } catch (Exception e) {
            errorMessage.setValue("Lỗi khi thêm nguyên liệu: " + e.getMessage());
        }
    }

    public void updateIngredient(Ingredient ingredient) {
        try {
            repository.updateIngredient(ingredient);
            updateStatistics();
            updateCategories();
            errorMessage.setValue(null);
        } catch (Exception e) {
            errorMessage.setValue("Lỗi khi cập nhật nguyên liệu: " + e.getMessage());
        }
    }

    public void deleteIngredient(String ingredientId) {
        try {
            repository.deleteIngredient(ingredientId);
            updateStatistics();
            updateCategories();
            errorMessage.setValue(null);
        } catch (Exception e) {
            errorMessage.setValue("Lỗi khi xóa nguyên liệu: " + e.getMessage());
        }
    }

    // Search and filter operations
    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
        applyFilters();
    }

    public void setSelectedCategory(String category) {
        selectedCategory.setValue(category);
        applyFilters();
    }

    public void setSelectedExpiryFilter(String filter) {
        selectedExpiryFilter.setValue(filter);
        applyFilters();
    }

    public void setSortMode(String sortMode) {
        currentSortMode.setValue(sortMode);
        repository.sortIngredients(sortMode);
    }

    // Combined filter operation
    public void applyFilters() {
        String category = selectedCategory.getValue();
        String expiryFilter = selectedExpiryFilter.getValue();
        String search = searchQuery.getValue();
        repository.applyComplexFilter(search, category, expiryFilter);
        String sortMode = currentSortMode.getValue();
        if (sortMode != null) {
            repository.sortIngredients(sortMode);
        }
        updateStatistics();
    }

    // Statistics and categories update
    private void updateStatistics() {
        Statistics stats = new Statistics();
        stats.totalItems = repository.getTotalCount();
        stats.expiringSoon = repository.getExpiringSoonCount();
        stats.expired = repository.getExpiredCount();
        statistics.setValue(stats);
    }

    private void updateCategories() {
        allCategories.setValue(repository.getAllCategories());
    }

    // Reset filters
    public void resetFilters() {
        selectedCategory.setValue("Tất cả");
        selectedExpiryFilter.setValue("Tất cả");
        searchQuery.setValue("");
        currentSortMode.setValue("name");
        loadIngredients();
    }

    // FAB menu operations
    public void toggleFabMenu() {
        Boolean currentState = fabMenuOpen.getValue();
        fabMenuOpen.setValue(currentState == null ? true : !currentState);
    }

    public void closeFabMenu() {
        fabMenuOpen.setValue(false);
    }

    public void openFabMenu() {
        fabMenuOpen.setValue(true);
    }

    // Validation methods
    public boolean validateIngredient(String name, String quantityStr, String unit, String expiryDate, String category) {
        if (name == null || name.trim().isEmpty()) {
            errorMessage.setValue("Tên nguyên liệu không được để trống");
            return false;
        }

        if (quantityStr == null || quantityStr.trim().isEmpty()) {
            errorMessage.setValue("Số lượng không được để trống");
            return false;
        }

        try {
            float quantity = Float.parseFloat(quantityStr);
            if (quantity <= 0) {
                errorMessage.setValue("Số lượng phải lớn hơn 0");
                return false;
            }
        } catch (NumberFormatException e) {
            errorMessage.setValue("Số lượng không hợp lệ");
            return false;
        }

        if (unit == null || unit.trim().isEmpty()) {
            errorMessage.setValue("Đơn vị không được để trống");
            return false;
        }

        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            errorMessage.setValue("Ngày hết hạn không được để trống");
            return false;
        }

        if (!isValidDate(expiryDate)) {
            errorMessage.setValue("Ngày hết hạn không đúng định dạng (dd/mm/yyyy)");
            return false;
        }

        if (category == null || category.trim().isEmpty()) {
            errorMessage.setValue("Danh mục không được để trống");
            return false;
        }

        errorMessage.setValue(null);
        return true;
    }

    private boolean isValidDate(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            sdf.setLenient(false);
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public Ingredient createIngredient(String name, String quantityStr, String unit, String expiryDate, String category) {
        if (!validateIngredient(name, quantityStr, unit, expiryDate, category)) {
            return null;
        }

        try {
            double quantity = Double.parseDouble(quantityStr);
            return new Ingredient(name.trim(), quantity, unit, expiryDate, category, R.drawable.ic_food_default);
        } catch (NumberFormatException e) {
            errorMessage.setValue("Lỗi khi tạo nguyên liệu: " + e.getMessage());
            return null;
        }
    }

    // Utility methods
    public void clearError() {
        errorMessage.setValue(null);
    }

    public Ingredient getIngredientById(String id) {
        return repository.getIngredientById(Integer.parseInt(id));
    }

    // Statistics data class
    public static class Statistics {
        public int totalItems;
        public int expiringSoon;
        public int expired;

        public Statistics() {
            this.totalItems = 0;
            this.expiringSoon = 0;
            this.expired = 0;
        }

        @Override
        public String toString() {
            return "Statistics{" +
                    "totalItems=" + totalItems +
                    ", expiringSoon=" + expiringSoon +
                    ", expired=" + expired +
                    '}';
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}