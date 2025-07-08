package com.example.android_exam.utils;

import android.util.Log;
import com.example.android_exam.data.models.enums.IngredientUnit;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

public class IngredientUnitDeserializer implements JsonDeserializer<IngredientUnit> {
    @Override
    public IngredientUnit deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        if (json.isJsonPrimitive()) {
            JsonPrimitive primitive = json.getAsJsonPrimitive();

            // Nếu là string từ API
            if (primitive.isString()) {
                String unitString = primitive.getAsString();
                return IngredientUnit.fromString(unitString);
            }
            // Nếu là number (backward compatibility)
            else if (primitive.isNumber()) {
                return IngredientUnit.fromInt(primitive.getAsInt());
            }
        }

        Log.w("UnitDeserializer", "Invalid unit format, using OTHER");
        return IngredientUnit.OTHER;
    }
}
