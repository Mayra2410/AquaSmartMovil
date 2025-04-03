package com.org.utl.aquasmartv1.api;

import com.org.utl.aquasmartv1.modal.Medidor;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MedidorApiService {
    @GET("medidor/getAllMedidor")
    Call<List<Medidor>> obtenerTodosLosMedidores();
}