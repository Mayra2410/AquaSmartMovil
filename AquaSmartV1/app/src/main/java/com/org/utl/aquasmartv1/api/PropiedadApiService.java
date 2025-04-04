package com.org.utl.aquasmartv1.api;

import com.org.utl.aquasmartv1.modal.Propiedad;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PropiedadApiService {
    @GET("propiedad/porUsuario2")
    Call<List<Propiedad>> obtenerPropiedadesPorUsuario(
            @Query("usuario") String nombreUsuario
    );
    @FormUrlEncoded
    @POST("propiedad/insertPropiedad")
    Call<Void> insertarPropiedad(
            @Field("datosPropiedad") String datosPropiedadJson
    );
}
