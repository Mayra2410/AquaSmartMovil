package com.org.utl.aquasmartv1.api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginApiService {
    @POST("login/validarLogin")
    @FormUrlEncoded
    Call<JsonObject> validarLogin(@Field("nombre") String nombre,
                                  @Field("contrasenia")String contrasenia);
}
