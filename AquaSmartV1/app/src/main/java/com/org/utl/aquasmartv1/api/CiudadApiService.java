package com.org.utl.aquasmartv1.api;

import com.org.utl.aquasmartv1.modal.Ciudad;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CiudadApiService {
    @GET("ciudad/getAllCiudad")
    Call<List<Ciudad>> obtenerTodosLasCiudadesEstados();
}
