package com.org.utl.aquasmartv1.api;

import com.org.utl.aquasmartv1.modal.Propiedad;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PropiedadApiService {
    @GET("propiedad/porUsuario")
    Call<List<Propiedad>> obtenerPropiedadesPorUsuario(
            @Query("usuario") String nombreUsuario
    );
}