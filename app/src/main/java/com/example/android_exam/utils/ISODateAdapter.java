package com.example.android_exam.utils;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ISODateAdapter extends TypeAdapter<Date> {
    private final SimpleDateFormat isoFormat;
    private final SimpleDateFormat[] fallbackFormats;

    public ISODateAdapter() {
        // Format chính để serialize/deserialize - ISO 8601
        isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        // Các format fallback để parse các định dạng khác
        fallbackFormats = new SimpleDateFormat[] {
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US),
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US),
                new SimpleDateFormat("MMM d, yyyy HH:mm:ss", Locale.US), // Format hiện tại của bạn
                new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US)
        };

        // Set timezone cho tất cả fallback formats
        for (SimpleDateFormat format : fallbackFormats) {
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
    }

    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        synchronized (isoFormat) {
            String dateString = isoFormat.format(value);
            out.value(dateString);
        }
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        String dateString = in.nextString();

        // Thử parse với format chính trước
        synchronized (isoFormat) {
            try {
                return isoFormat.parse(dateString);
            } catch (ParseException ignored) {}
        }

        // Thử các format fallback
        for (SimpleDateFormat format : fallbackFormats) {
            synchronized (format) {
                try {
                    return format.parse(dateString);
                } catch (ParseException ignored) {}
            }
        }

        throw new IOException("Unable to parse date: " + dateString);
    }
}