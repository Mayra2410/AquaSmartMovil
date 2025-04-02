package com.org.utl.aquasmartv1.api;

import com.google.gson.JsonObject;
import com.org.utl.aquasmartv1.modal.Ciudad;
import com.org.utl.aquasmartv1.modal.Cliente;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ClienteApiService {
    @FormUrlEncoded
    @POST("cliente/insertCliente")
    Call<JsonObject> insertCliente(@Field("datosCliente") String cliente);

    @GET("cliente/porUsuarioC")
    Call<List<Cliente>> getClientePorUsuario(@Query("nombre") String nombreUsuario);

    @FormUrlEncoded
    @POST("cliente/updateCliente")
    Call<JsonObject> updateCliente(@Field("datosCliente") String cliente);
}
