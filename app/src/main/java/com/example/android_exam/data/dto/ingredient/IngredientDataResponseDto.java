package com.example.android_exam.data.dto.ingredient;


import com.example.android_exam.data.models.base.Ingredient;
import com.example.android_exam.data.models.enums.IngredientCategory;
import com.example.android_exam.data.models.enums.IngredientUnit;
import com.example.android_exam.utils.DateUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

public class IngredientDataResponseDto implements Serializable {

    private int id;
    private String name = "";
    private String description;
    private BigDecimal quantity;
    private IngredientUnit unit;
    private IngredientCategory category;
    private Date expiryDate;
    private String imageUrl;

    // Constructors
    public IngredientDataResponseDto() {}

    public IngredientDataResponseDto(int id, String name, String description,
                                     BigDecimal quantity, IngredientUnit unit,
                                     IngredientCategory category, Date  expiryDate,
                                     String imageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quantity = quantity;
        this.unit = unit;
        this.category = category;
        this.expiryDate = expiryDate;
        this.imageUrl = imageUrl;
    }

    public boolean isExpired() {
        return expiryDate != null && expiryDate.before(new Date());
    }

    public boolean isExpiringSoon() {
        return expiryDate != null && DateUtils.isExpiringSoon(expiryDate.toString(), 7);
    }
    public static IngredientDataResponseDto fromIngredient(Ingredient ingredient) {
        return new IngredientDataResponseDto(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getDescription(),
                BigDecimal.valueOf(ingredient.getQuantity()),
                ingredient.getUnit(),
                ingredient.getCategory(),
                DateUtils.convertToDate(ingredient.getExpiryDate()),
                ingredient.getImageUrl()
        );
    }

    public static CreateIngredientRequestDto toCreateRequest(Ingredient ingredient) {
        CreateIngredientRequestDto dto = new CreateIngredientRequestDto();
        dto.setName(ingredient.getName());
        dto.setDescription(ingredient.getDescription());
        dto.setQuantity(BigDecimal.valueOf(ingredient.getQuantity()));
        dto.setUnit(ingredient.getUnit());
        dto.setCategory(ingredient.getCategory());
        dto.setExpiryDate(DateUtils.formatDateTimeToIso(ingredient.getExpiryDate()));
        return dto;
    }

    public static UpdateIngredientRequestDto toUpdateRequest(Ingredient ingredient) {
        UpdateIngredientRequestDto dto = new UpdateIngredientRequestDto();
        dto.setId(ingredient.getId());
        dto.setName(ingredient.getName());
        dto.setDescription(ingredient.getDescription());
        dto.setQuantity(BigDecimal.valueOf(ingredient.getQuantity()));
        dto.setUnit(ingredient.getUnit());
        dto.setCategory(ingredient.getCategory());
        dto.setExpiryDate(DateUtils.formatDateTimeToIso(ingredient.getExpiryDate()));
        return dto;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public IngredientUnit getUnit() {
        return unit;
    }

    public void setUnit(IngredientUnit unit) {
        this.unit = unit;
    }

    public IngredientCategory getCategory() {
        return category;
    }

    public void setCategory(IngredientCategory category) {
        this.category = category;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}