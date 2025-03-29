package com.org.utl.aquasmartv1;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TimestampTypeAdapter extends TypeAdapter<Timestamp> {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy, h:mm:ss a", Locale.ENGLISH);

    @Override
    public void write(JsonWriter out, Timestamp value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(dateFormat.format(value));
        }
    }

    @Override
    public Timestamp read(JsonReader in) throws IOException {
        String dateString = null;
        try {
            if (in.peek() == null) {
                return null;
            }
            dateString = in.nextString();
            return new Timestamp(dateFormat.parse(dateString).getTime());
        } catch (ParseException e) {
            throw new IOException("Error al parsear fecha: " + dateString, e);
        }
    }
}