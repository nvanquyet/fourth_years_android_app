package com.example.android_exam.utils;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateDeserializer implements JsonDeserializer<Date> {
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        try {
            String dateString = json.getAsString();
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            return formatter.parse(dateString);
        } catch (ParseException e) {
            throw new JsonParseException("Unable to parse date: " + json.getAsString(), e);
        }
    }
}