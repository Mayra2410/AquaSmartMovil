package com.org.utl.aquasmartv1.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Globals {
    public static String BASE_URL = "https://69aa-2806-264-5487-9a2-6944-bf47-908e-4e64.ngrok-free.app/AquaSmart/api/";
    private static Retrofit retrofit = null;

    public static <T> T getApiService(Class<T> serviceClass) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(serviceClass);
    }
}
