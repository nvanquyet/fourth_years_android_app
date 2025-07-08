package com.example.android_exam.utils;

import android.util.Log;
import com.example.android_exam.data.models.enums.IngredientCategory;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

public class IngredientCategoryDeserializer implements JsonDeserializer<IngredientCategory> {
    @Override
    public IngredientCategory deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        if (json.isJsonPrimitive()) {
            JsonPrimitive primitive = json.getAsJsonPrimitive();

            // Nếu là string từ API
            if (primitive.isString()) {
                String categoryString = primitive.getAsString();
                return IngredientCategory.fromString(categoryString);
            }
            // Nếu là number (backward compatibility)
            else if (primitive.isNumber()) {
                return IngredientCategory.fromInt(primitive.getAsInt());
            }
        }

        Log.w("CategoryDeserializer", "Invalid category format, using OTHER");
        return IngredientCategory.OTHER;
    }
}