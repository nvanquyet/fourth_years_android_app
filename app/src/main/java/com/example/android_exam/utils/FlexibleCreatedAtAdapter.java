package com.example.android_exam.utils;
import com.google.gson.*;
import java.lang.reflect.Type;

public class FlexibleCreatedAtAdapter implements JsonDeserializer<String> {
    @Override
    public String deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        if (json.isJsonPrimitive()) {
            return json.getAsString(); // Nếu là chuỗi thì trả về luôn
        } else if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();
            String date = obj.has("date") ? obj.get("date").getAsString() : "";
            String time = obj.has("time") ? obj.get("time").getAsString() : "";
            return date + "T" + time; // Tự xử lý format ISO
        }
        return null;
    }
}