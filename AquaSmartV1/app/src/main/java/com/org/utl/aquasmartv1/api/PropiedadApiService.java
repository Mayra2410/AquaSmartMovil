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
    // Opción 1: Si tu endpoint está en la raíz
    @GET("propiedad/porUsuario2")
    // Opción 2: Si está bajo /api/
    // @GET("api/propiedad/porUsuario")
    Call<List<Propiedad>> obtenerPropiedadesPorUsuario(
            @Query("usuario") String nombreUsuario
    );
    @FormUrlEncoded
    @POST("propiedad/insertPropiedad")
    Call<Void> insertarPropiedad(
            @Field("numExt") String numExt,
            @Field("numInt") String numInt,
            @Field("calle") String calle,
            @Field("colonia") String colonia,
            @Field("latitud") double latitud,
            @Field("longitud") double longitud,
            @Field("codigoP") String codigoP,
            @Field("foto") String foto,
            @Field("estatus") int estatus,
            @Field("idCliente") int idCliente,
            @Field("idCiudad") int idCiudad,
            @Field("idMedidor") int idMedidor
    );

    // Versión 2: Insertar propiedad con objeto JSON (recomendado)
    @POST("propiedad/insert")
    Call<Propiedad> insertarPropiedad(@Body Propiedad propiedad);

    // Obtener todas las propiedades
    @GET("propiedad/getAll")
    Call<List<Propiedad>> obtenerTodasLasPropiedades();

    // Obtener propiedad por ID
    @GET("propiedad/{id}")
    Call<Propiedad> obtenerPropiedadPorId(@Query("id") int idPropiedad);
}