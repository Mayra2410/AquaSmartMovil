package com.org.utl.aquasmartv1.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.org.utl.aquasmartv1.api.ClienteApiService;
import com.org.utl.aquasmartv1.api.Globals;
import com.org.utl.aquasmartv1.TimestampTypeAdapter;
import com.org.utl.aquasmartv1.modal.Cliente;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<Cliente>> clientesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private ClienteApiService apiService;

    public HomeViewModel() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Globals.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(ClienteApiService.class);
    }

    public LiveData<List<Cliente>> getClientesLiveData() {
        return clientesLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void cargarCliente(String nombreUsuario) {
        if (nombreUsuario == null || nombreUsuario.isEmpty()) {
            errorLiveData.postValue("Usuario no válido");
            return;
        }

        isLoading.postValue(true);

        Call<List<Cliente>> call = apiService.getClientePorUsuario(nombreUsuario);
        call.enqueue(new Callback<List<Cliente>>() {
            @Override
            public void onResponse(Call<List<Cliente>> call, Response<List<Cliente>> response) {
                isLoading.postValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    clientesLiveData.postValue(response.body());
                } else {
                    errorLiveData.postValue("Error del servidor: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Cliente>> call, Throwable t) {
                isLoading.postValue(false);
                errorLiveData.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }

}