package com.example.android_exam.viewmodels;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.android_exam.data.api.ApiManager;
import com.example.android_exam.data.api.DataCallback;
import com.example.android_exam.data.dto.ingredient.CreateIngredientRequestDto;
import com.example.android_exam.data.dto.ingredient.DeleteIngredientRequestDto;
import com.example.android_exam.data.dto.ingredient.IngredientDataResponseDto;
import com.example.android_exam.data.dto.ingredient.IngredientFilterDto;
import com.example.android_exam.data.dto.ingredient.IngredientSearchResultDto;
import com.example.android_exam.data.dto.ingredient.UpdateIngredientRequestDto;
import com.example.android_exam.data.dto.response.ApiResponse;
import com.example.android_exam.data.models.base.Ingredient;
import com.example.android_exam.data.models.enums.IngredientCategory;
import com.example.android_exam.utils.DateUtils;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IngredientViewModel extends ViewModel {
    private final MutableLiveData<List<IngredientDataResponseDto>> ingredientsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> successMessageLiveData = new MutableLiveData<>();
    private final MutableLiveData<Map<String, String>> validationErrorLiveData = new MutableLiveData<>();

    // Cache toàn bộ dữ liệu từ server
    private List<IngredientDataResponseDto> allIngredients = new ArrayList<>();
    private List<IngredientDataResponseDto> filteredIngredients = new ArrayList<>();

    // Trạng thái cache
    private boolean isDataCached = false;
    private boolean isLoadingFromServer = false;

    // Filter states
    private String currentSearchQuery = "";
    private IngredientCategory currentCategoryFilter = null;
    private Boolean currentExpiryFilter = null;
    private String currentSortBy = "expiryDate";
    private String currentSortDirection = "asc";

    public LiveData<List<IngredientDataResponseDto>> getIngredientsLiveData() {
        return ingredientsLiveData;
    }

    public LiveData<Boolean> getLoadingLiveData() {
        return loadingLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<String> getSuccessMessageLiveData() {
        return successMessageLiveData;
    }

    public LiveData<Map<String, String>> getValidationErrorLiveData() {
        return validationErrorLiveData;
    }

    /**
     * Load ingredients - chỉ gọi API nếu chưa có cache hoặc force refresh
     */
    public void loadIngredients() {
        loadIngredients(false);
    }
    //
    public void loadIngredients(IngredientSearchResultDto ingredients) {
        // Set to local cache
        if (ingredients != null) {
            allIngredients = new ArrayList<>(ingredients.getIngredients());
            isDataCached = true;

            Log.d("IngredientViewModel", "Cache thành công " + allIngredients.size() + " ingredients");
            // Apply filters trên dữ liệu đã cache
            applyFiltersAndSort();
        } else {
            errorLiveData.postValue( "Không thể tải danh sách thực phẩm");
        }
    }

    /**
     * Load ingredients với option force refresh
     * @param forceRefresh true nếu muốn bỏ qua cache và gọi API mới
     */
    public void loadIngredients(boolean forceRefresh) {
        // Nếu đã có cache và không force refresh, chỉ cần filter local
        if (isDataCached && !forceRefresh && !isLoadingFromServer) {
            Log.d("IngredientViewModel", "Sử dụng cache local, không gọi API");
            applyFiltersAndSort();
            return;
        }

        // Nếu đang loading từ server, không gọi thêm
        if (isLoadingFromServer) {
            Log.d("IngredientViewModel", "Đang loading từ server, bỏ qua request");
            return;
        }

        // Gọi API để lấy dữ liệu
        if(forceRefresh) loadFromServer();
    }

    /**
     * Gọi API để lấy dữ liệu từ server
     */
    private void loadFromServer() {
        Log.d("IngredientViewModel", "Gọi API để lấy dữ liệu");
        isLoadingFromServer = true;
        loadingLiveData.postValue(true);

        IngredientFilterDto filter = new IngredientFilterDto();
        filter.setSortBy("expiryDate");
        filter.setSortDirection("asc");

        ApiManager.getInstance().getIngredientClient().getAllIngredients(filter, new DataCallback<ApiResponse<IngredientSearchResultDto>>() {
            @Override
            public void onSuccess(@NonNull ApiResponse<IngredientSearchResultDto> response) {
                isLoadingFromServer = false;
                loadingLiveData.postValue(false);

                if (response.isSuccess() && response.getData() != null) {
                    // Cache dữ liệu
                    allIngredients = new ArrayList<>(response.getData().getIngredients());
                    isDataCached = true;

                    Log.d("IngredientViewModel", "Cache thành công " + allIngredients.size() + " ingredients");

                    // Apply filters trên dữ liệu đã cache
                    applyFiltersAndSort();
                } else {
                    String errorMsg = response.getMessage() != null ? response.getMessage() : "Không thể tải danh sách thực phẩm";
                    errorLiveData.postValue(errorMsg);
                    Log.e("IngredientViewModel", "API response error: " + errorMsg);
                }
            }

            @Override
            public void onError(@NonNull String error) {
                isLoadingFromServer = false;
                loadingLiveData.postValue(false);
                errorLiveData.postValue("Không thể tải danh sách thực phẩm: " + error);
                Log.e("IngredientViewModel", "API error: " + error);
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
                isLoadingFromServer = false;
                loadingLiveData.postValue(false);
                errorLiveData.postValue("Lỗi kết nối: " + throwable.getMessage());
                Log.e("IngredientViewModel", "API failure: " + throwable.getMessage());
            }
        });
    }

    /**
     * Apply filters và sort trên dữ liệu đã cache
     */
    private void applyFiltersAndSort() {
        Log.d("IngredientViewModel", "Applying filters locally - Search: '" + currentSearchQuery +
                "', Category: " + currentCategoryFilter + ", Expiry: " + currentExpiryFilter +
                ", Sort: " + currentSortBy + " " + currentSortDirection);

        List<IngredientDataResponseDto> result = new ArrayList<>(allIngredients);

        // Apply search filter
        if (!currentSearchQuery.isEmpty()) {
            result = result.stream()
                    .filter(ingredient -> ingredient.getName().toLowerCase()
                            .contains(currentSearchQuery.toLowerCase()))
                    .collect(Collectors.toList());
            Log.d("IngredientViewModel", "After search filter: " + result.size() + " items");
        }

        // Apply category filter
        if (currentCategoryFilter != null) {
            result = result.stream()
                    .filter(ingredient -> ingredient.getCategory() == currentCategoryFilter)
                    .collect(Collectors.toList());
            Log.d("IngredientViewModel", "After category filter: " + result.size() + " items");
        }

        // Apply expiry filter
        if (currentExpiryFilter != null) {
            if (currentExpiryFilter) {
                result = result.stream()
                        .filter(IngredientDataResponseDto::isExpired)
                        .collect(Collectors.toList());
            } else {
                result = result.stream()
                        .filter(ingredient -> !ingredient.isExpired())
                        .collect(Collectors.toList());
            }
            Log.d("IngredientViewModel", "After expiry filter: " + result.size() + " items");
        }

        // Apply sorting
        result = sortIngredientsList(result);
        Log.d("IngredientViewModel", "After sorting: " + result.size() + " items");

        filteredIngredients = result;
        ingredientsLiveData.postValue(filteredIngredients);
    }

    private List<IngredientDataResponseDto> sortIngredientsList(List<IngredientDataResponseDto> ingredients) {
        return ingredients.stream()
                .sorted((a, b) -> {
                    int comparison = 0;
                    switch (currentSortBy) {
                        case "name":
                            comparison = a.getName().compareToIgnoreCase(b.getName());
                            break;
                        case "quantity":
                            comparison = a.getQuantity().compareTo(b.getQuantity());
                            break;
                        case "expiryDate":
                            if (a.getExpiryDate() == null && b.getExpiryDate() == null) comparison = 0;
                            else if (a.getExpiryDate() == null) comparison = 1;
                            else if (b.getExpiryDate() == null) comparison = -1;
                            else comparison = a.getExpiryDate().compareTo(b.getExpiryDate());
                            break;
                        case "category":
                            comparison = a.getCategory().name().compareToIgnoreCase(b.getCategory().name());
                            break;
                        case "createdAt":
                            // Implement if needed
                            comparison = 0;
                            break;
                    }
                    return "desc".equals(currentSortDirection) ? -comparison : comparison;
                })
                .collect(Collectors.toList());
    }

    public void getIngredientById(int id, @NonNull DataCallback<IngredientDataResponseDto> callback) {
        // Tìm trong cache trước
        if (isDataCached) {
            for (IngredientDataResponseDto ingredient : allIngredients) {
                if (ingredient.getId() == id) {
                    Log.d("IngredientViewModel", "Found ingredient in cache: " + ingredient.getName());
                    callback.onSuccess(ingredient);
                    return;
                }
            }
        }

        // Nếu không tìm thấy trong cache, gọi API
        Log.d("IngredientViewModel", "Ingredient not found in cache, calling API");
        loadingLiveData.postValue(true);
        ApiManager.getInstance().getIngredientClient().getIngredientById(id, new DataCallback<ApiResponse<IngredientDataResponseDto>>() {
            @Override
            public void onSuccess(@NonNull ApiResponse<IngredientDataResponseDto> response) {
                loadingLiveData.postValue(false);
                if (response.isSuccess() && response.getData() != null) {
                    try {
                        IngredientDataResponseDto ingredient = (IngredientDataResponseDto) response.getData();
                        callback.onSuccess(ingredient);
                    } catch (ClassCastException e) {
                        callback.onError("Lỗi định dạng dữ liệu");
                    }
                } else {
                    String error = response.getMessage() != null ? response.getMessage() : "Không thể tải chi tiết thực phẩm";
                    errorLiveData.postValue(error);
                    callback.onError(error);
                }
            }

            @Override
            public void onError(@NonNull String error) {
                loadingLiveData.postValue(false);
                errorLiveData.postValue("Không thể tải chi tiết thực phẩm: " + error);
                callback.onError(error);
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
                loadingLiveData.postValue(false);
                errorLiveData.postValue("Lỗi kết nối: " + throwable.getMessage());
                callback.onFailure(throwable);
                Log.e("IngredientViewModel", "Lỗi kết nối: " + throwable.getMessage());
            }
        });
    }

    public void addIngredient(@Nullable Ingredient ingredient, File imageFile) {
        if (ingredient == null) {
            errorLiveData.postValue("Dữ liệu thực phẩm không hợp lệ");
            return;
        }

        Map<String, String> errors = validateIngredient(ingredient);
        if (!errors.isEmpty()) {
            validationErrorLiveData.postValue(errors);
            return;
        }

        loadingLiveData.postValue(true);
        CreateIngredientRequestDto dataDto = IngredientDataResponseDto.toCreateRequest(ingredient);

        //Log to debug
        Log.d("IngredientViewModel", "Adding ingredient: " + dataDto.toJson());
        Log.d("IngredientViewModel", "Image file: " + (imageFile != null ? imageFile.getName() : "No image file"));


        ApiManager.getInstance().getIngredientClient().createIngredient(dataDto, imageFile, new DataCallback<ApiResponse<IngredientDataResponseDto>>() {
            @Override
            public void onSuccess(@NonNull ApiResponse<IngredientDataResponseDto> response) {
                loadingLiveData.postValue(false);
                if (response.isSuccess() && response.getData() != null) {
                    successMessageLiveData.postValue("Thêm thực phẩm thành công!");

                    // Thêm ingredient mới vào cache
                    allIngredients.add(response.getData());
                    Log.d("IngredientViewModel", "Added to cache: " + response.getData().getName());

                    // Re-apply filters
                    applyFiltersAndSort();
                } else {
                    errorLiveData.postValue(response.getMessage() != null ? response.getMessage() : "Không thể thêm thực phẩm");
                }
            }

            @Override
            public void onError(@NonNull String error) {
                loadingLiveData.postValue(false);
                errorLiveData.postValue("Không thể thêm thực phẩm: " + error);
                Log.e("IngredientViewModel", "Lỗi khi thêm thực phẩm: " + error);
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
                loadingLiveData.postValue(false);
                errorLiveData.postValue("Lỗi kết nối: " + throwable.getMessage());
                Log.e("IngredientViewModel", "Lỗi kết nối: " + throwable.getMessage());
            }
        });
    }

    public void updateIngredient(int id, @NonNull Ingredient ingredient, File imageFile) {
        Map<String, String> errors = validateIngredient(ingredient);
        if (!errors.isEmpty()) {
            validationErrorLiveData.postValue(errors);
            return;
        }
        loadingLiveData.postValue(true);

        UpdateIngredientRequestDto dataDto = IngredientDataResponseDto.toUpdateRequest(ingredient);
        dataDto.setId(id);
        ApiManager.getInstance().getIngredientClient().updateIngredient(dataDto, imageFile, new DataCallback<ApiResponse<IngredientDataResponseDto>>() {
            @Override
            public void onSuccess(@NonNull ApiResponse<IngredientDataResponseDto> response) {
                loadingLiveData.postValue(false);
                if (response.isSuccess() && response.getData() != null) {
                    successMessageLiveData.postValue("Cập nhật thực phẩm thành công!");

                    // Cập nhật ingredient trong cache
                    updateLocalIngredient(id, response.getData());
                    Log.d("IngredientViewModel", "Updated in cache: " + response.getData().getName());

                    // Re-apply filters
                    applyFiltersAndSort();
                } else {
                    errorLiveData.postValue(response.getMessage() != null ? response.getMessage() : "Không thể cập nhật thực phẩm");
                }
            }

            @Override
            public void onError(@NonNull String error) {
                loadingLiveData.postValue(false);
                errorLiveData.postValue("Không thể cập nhật thực phẩm: " + error);
                Log.e("IngredientViewModel", "Lỗi khi cập nhật thực phẩm: " + error);
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
                loadingLiveData.postValue(false);
                errorLiveData.postValue("Lỗi kết nối: " + throwable.getMessage());
                Log.e("IngredientViewModel", "Lỗi kết nối: " + throwable.getMessage());
            }
        });
    }

    private void updateLocalIngredient(int id, IngredientDataResponseDto updatedIngredient) {
        for (int i = 0; i < allIngredients.size(); i++) {
            if (allIngredients.get(i).getId() == id) {
                allIngredients.set(i, updatedIngredient);
                break;
            }
        }
    }

    public void deleteIngredient(int ingredientId) {
        loadingLiveData.postValue(true);
        DeleteIngredientRequestDto dto = new DeleteIngredientRequestDto();
        dto.setId(ingredientId);
        ApiManager.getInstance().getIngredientClient().deleteIngredient(dto, new DataCallback<ApiResponse<Boolean>>() {
            @Override
            public void onSuccess(@NonNull ApiResponse<Boolean> response) {
                loadingLiveData.postValue(false);
                if (response.isSuccess()) {
                    successMessageLiveData.postValue("Xóa thực phẩm thành công!");

                    // Xóa ingredient khỏi cache
                    removeLocalIngredient(ingredientId);
                    Log.d("IngredientViewModel", "Removed from cache: ID " + ingredientId);

                    // Re-apply filters
                    applyFiltersAndSort();
                } else {
                    errorLiveData.postValue(response.getMessage() != null ? response.getMessage() : "Không thể xóa thực phẩm");
                }
            }

            @Override
            public void onError(@NonNull String error) {
                loadingLiveData.postValue(false);
                errorLiveData.postValue("Không thể xóa thực phẩm: " + error);
            }

            @Override
            public void onFailure(@NonNull Throwable throwable) {
                loadingLiveData.postValue(false);
                errorLiveData.postValue("Lỗi kết nối: " + throwable.getMessage());
            }
        });
    }

    private void removeLocalIngredient(int id) {
        allIngredients.removeIf(ingredient -> ingredient.getId() == id);
    }

    /**
     * Search ingredients - chỉ filter local
     */
    public void searchIngredients(@NonNull String query) {
        Log.d("IngredientViewModel", "Search query: '" + query + "'");
        currentSearchQuery = query.trim();

        // Nếu chưa có cache, load từ server
        if (!isDataCached) {
            loadIngredients();
        } else {
            // Nếu đã có cache, chỉ filter local
            applyFiltersAndSort();
        }
    }

    /**
     * Filter by category - chỉ filter local
     */
    public void filterByCategory(@NonNull IngredientCategory category) {
        Log.d("IngredientViewModel", "Filter by category: " + category);
        currentCategoryFilter = category;

        // Nếu chưa có cache, load từ server
        if (!isDataCached) {
            loadIngredients();
        } else {
            // Nếu đã có cache, chỉ filter local
            applyFiltersAndSort();
        }
    }

    /**
     * Clear category filter - chỉ filter local
     */
    public void clearCategoryFilter() {
        Log.d("IngredientViewModel", "Clear category filter");
        currentCategoryFilter = null;

        // Nếu chưa có cache, load từ server
        if (!isDataCached) {
            loadIngredients();
        } else {
            // Nếu đã có cache, chỉ filter local
            applyFiltersAndSort();
        }
    }

    /**
     * Filter by expiry - chỉ filter local
     */
    public void filterByExpiry(boolean isExpired) {
        Log.d("IngredientViewModel", "Filter by expiry: " + isExpired);
        currentExpiryFilter = isExpired;

        // Nếu chưa có cache, load từ server
        if (!isDataCached) {
            loadIngredients();
        } else {
            // Nếu đã có cache, chỉ filter local
            applyFiltersAndSort();
        }
    }

    /**
     * Clear expiry filter - chỉ filter local
     */
    public void clearExpiryFilter() {
        Log.d("IngredientViewModel", "Clear expiry filter");
        currentExpiryFilter = null;

        // Nếu chưa có cache, load từ server
        if (!isDataCached) {
            loadIngredients();
        } else {
            // Nếu đã có cache, chỉ filter local
            applyFiltersAndSort();
        }
    }

    /**
     * Sort ingredients - chỉ sort local
     */
    public void sortIngredients(@Nullable String sortBy, @Nullable String sortDirection) {
        Log.d("IngredientViewModel", "Sort: " + sortBy + " " + sortDirection);
        currentSortBy = sortBy != null ? sortBy : "expiryDate";
        currentSortDirection = sortDirection != null ? sortDirection : "asc";

        // Nếu chưa có cache, load từ server
        if (!isDataCached) {
            loadIngredients();
        } else {
            // Nếu đã có cache, chỉ sort local
            applyFiltersAndSort();
        }
    }

    /**
     * Get expired ingredients từ cache
     */
    public List<IngredientDataResponseDto> getExpiredIngredients() {
        return allIngredients.stream()
                .filter(ingredient -> ingredient.isExpired())
                .collect(Collectors.toList());
    }

    /**
     * Get not expired ingredients từ cache
     */
    public List<IngredientDataResponseDto> getNotExpiredIngredients() {
        return allIngredients.stream()
                .filter(ingredient -> !ingredient.isExpired())
                .collect(Collectors.toList());
    }

    /**
     * Get count by category từ cache
     */
    public int getCountByCategory(@NonNull IngredientCategory category) {
        return (int) allIngredients.stream()
                .filter(ingredient -> ingredient.getCategory() == category)
                .count();
    }

    public void clearError() {
        errorLiveData.postValue(null);
    }

    public void clearSuccessMessage() {
        successMessageLiveData.postValue(null);
    }

    public void clearValidationErrors() {
        validationErrorLiveData.postValue(null);
    }

    /**
     * Refresh - xóa cache và load lại từ server
     */
    public void refresh() {
        Log.d("IngredientViewModel", "Refreshing data from server");
        // Clear cache và filters
        allIngredients.clear();
        filteredIngredients.clear();
        isDataCached = false;

        // Load lại từ server
        loadIngredients(true);
    }

    /**
     * Check if data is cached
     */
    public boolean isDataCached() {
        return isDataCached;
    }

    /**
     * Get cache size
     */
    public int getCacheSize() {
        return allIngredients.size();
    }

    /**
     * Clear cache manually
     */
    public void clearCache() {
        Log.d("IngredientViewModel", "Clearing cache");
        allIngredients.clear();
        filteredIngredients.clear();
        isDataCached = false;
        ingredientsLiveData.postValue(new ArrayList<>());
    }

    private Map<String, String> validateIngredient(@NonNull Ingredient ingredient) {
        Map<String, String> errors = new HashMap<>();
        if (ingredient.getName() == null || ingredient.getName().trim().isEmpty()) {
            errors.put("name", "Vui lòng nhập tên thực phẩm");
        }
        if (ingredient.getQuantity() < 0) {
            errors.put("quantity", "Số lượng không thể âm");
        }
        if (ingredient.getExpiryDate() != null) {
            LocalDate expiryDate = ingredient.getExpiryDate();
            if (expiryDate != null && expiryDate.isBefore(LocalDate.now())) {
                errors.put("expiryDate", "Ngày hết hạn không thể là quá khứ");
            }
        }
        return errors;
    }
}