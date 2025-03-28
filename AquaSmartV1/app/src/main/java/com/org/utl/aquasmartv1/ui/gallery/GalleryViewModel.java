package com.org.utl.aquasmartv1.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.org.utl.aquasmartv1.api.ClienteApiService;
import com.org.utl.aquasmartv1.api.Globals;
import com.org.utl.aquasmartv1.api.PropiedadApiService;
import com.org.utl.aquasmartv1.modal.Propiedad;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GalleryViewModel extends ViewModel {
    private final MutableLiveData<List<Propiedad>> propiedadesLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final PropiedadApiService apiService;

    public GalleryViewModel() {
        // Crear instancia de Retrofit directamente usando Globals.BASE_URL
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Globals.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(PropiedadApiService.class);
    }

    public LiveData<List<Propiedad>> getPropiedadesLiveData() {
        return propiedadesLiveData;
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    public void cargarPropiedades(String nombreUsuario) {
        // Verifica que el usuario no sea nulo
        if (nombreUsuario == null || nombreUsuario.isEmpty()) {
            errorLiveData.postValue("Usuario inválido");
            return;
        }

        Call<List<Propiedad>> call = apiService.obtenerPropiedadesPorUsuario(nombreUsuario);
        call.enqueue(new Callback<List<Propiedad>>() {
            @Override
            public void onResponse(Call<List<Propiedad>> call, Response<List<Propiedad>> response) {
                if (response.isSuccessful()) {
                    propiedadesLiveData.setValue(response.body());
                } else {
                    errorLiveData.setValue("Error: " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Propiedad>> call, Throwable t) {
                errorLiveData.setValue("Error de conexión: " + t.getMessage());
            }
        });
    }
}