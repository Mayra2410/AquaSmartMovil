package com.org.utl.aquasmartv1.api;

import com.org.utl.aquasmartv1.modal.Ciudad;
import com.org.utl.aquasmartv1.modal.Estado;
import com.org.utl.aquasmartv1.ApiResponse;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface EstadoApiService {
    @GET("estado/getAllEstado")
    Call<List<Estado>> obtenerTodosLosEstados();

}
