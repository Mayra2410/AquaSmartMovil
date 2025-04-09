package com.org.utl.aquasmartv1.api;

import com.google.gson.JsonObject;
import com.org.utl.aquasmartv1.modal.Propiedad;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Headers;
public interface PropiedadApiService {
    @GET("propiedad/porUsuario2")
    Call<List<Propiedad>> obtenerPropiedadesPorUsuario(
            @Query("usuario") String nombreUsuario
    );
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("propiedad/insertPropiedad")
    Call<JsonObject> insertarPropiedad(@Field("datosPropiedad") String datosPropiedad);
}
