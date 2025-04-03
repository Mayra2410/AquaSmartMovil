package com.org.utl.aquasmartv1.ui.gallery;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.org.utl.aquasmartv1.api.Globals;
import com.org.utl.aquasmartv1.api.PropiedadApiService;
import com.org.utl.aquasmartv1.modal.Propiedad;

import java.io.IOException;
import java.util.Collections;
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
        Gson gson = new GsonBuilder()
                .setDateFormat("MMM dd, yyyy, hh:mm:ss a")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Globals.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
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
        Call<List<Propiedad>> call = apiService.obtenerPropiedadesPorUsuario(nombreUsuario);
        call.enqueue(new Callback<List<Propiedad>>() {
            @Override
            public void onResponse(Call<List<Propiedad>> call, Response<List<Propiedad>> response) {
                if (response.isSuccessful()) {
                    List<Propiedad> propiedades = response.body();
                    if (propiedades != null && !propiedades.isEmpty()) {
                        propiedadesLiveData.postValue(propiedades);
                    } else {
                        // Envía una lista vacía para indicar "sin propiedades"
                        propiedadesLiveData.postValue(Collections.emptyList());
                    }
                } else {
                    // En caso de error, también considera como "sin propiedades"
                    propiedadesLiveData.postValue(Collections.emptyList());
                    errorLiveData.postValue("Error al cargar propiedades");
                }
            }

            @Override
            public void onFailure(Call<List<Propiedad>> call, Throwable t) {
                propiedadesLiveData.postValue(Collections.emptyList());
                errorLiveData.postValue("Error de conexión");
            }
        });
    }
}