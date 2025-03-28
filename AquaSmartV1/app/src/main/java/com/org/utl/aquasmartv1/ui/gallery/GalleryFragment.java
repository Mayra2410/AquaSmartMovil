package com.org.utl.aquasmartv1.ui.gallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.org.utl.aquasmartv1.api.CiudadApiService;
import com.org.utl.aquasmartv1.databinding.FragmentGalleryBinding;
import com.org.utl.aquasmartv1.modal.Ciudad;
import com.org.utl.aquasmartv1.modal.Estado;
import com.org.utl.aquasmartv1.modal.Medidor;
import com.org.utl.aquasmartv1.modal.Propiedad;
import com.org.utl.aquasmartv1.api.Globals;

import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private GalleryViewModel galleryViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configurar observadores
        galleryViewModel.getPropiedadesLiveData().observe(getViewLifecycleOwner(), this::actualizarUI);
        galleryViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), this::mostrarError);

        // Verificación avanzada de SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        Log.d("GalleryPrefs", "Contenido de preferencias: " + prefs.getAll().toString());

        // Obtener usuario y cargar propiedades
        String usuario = obtenerUsuarioLogueado();
        Log.d("GalleryUser", "Usuario obtenido: " + usuario);

        if (usuario != null && !usuario.isEmpty()) {
            Log.d("GalleryLoad", "Cargando propiedades para: " + usuario);
            galleryViewModel.cargarPropiedades(usuario);
        } else {
            mostrarError("Usuario no identificado");
            Log.e("GalleryError", "Usuario es nulo o vacío");
            verificarArchivoPrefs();
        }
    }

    private String obtenerUsuarioLogueado() {
        try {
            Context context = requireContext();
            SharedPreferences sharedPref = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
            String usuario = sharedPref.getString("username", null);

            Log.d("GalleryPrefs", "Usuario en SharedPreferences: " + usuario);
            Log.d("GalleryPrefs", "Todas las claves: " + sharedPref.getAll().keySet());

            return usuario;
        } catch (Exception e) {
            Log.e("GalleryError", "Error al obtener usuario", e);
            return null;
        }
    }

    private void verificarArchivoPrefs() {
        try {
            Context context = requireContext();
            File prefsFile = new File(context.getApplicationInfo().dataDir + "/shared_prefs/login_prefs.xml");
            Log.d("GalleryPrefs", "Archivo existe: " + prefsFile.exists());
            Log.d("GalleryPrefs", "Ruta completa: " + prefsFile.getAbsolutePath());
        } catch (Exception e) {
            Log.e("GalleryError", "Error al verificar archivo prefs", e);
        }
    }

    private void actualizarUI(List<Propiedad> propiedades) {
        if (propiedades == null || propiedades.isEmpty()) {
            mostrarError("No se encontraron propiedades");
            return;
        }

        Propiedad propiedad = propiedades.get(0);
        try {
            // Dirección completa
            String direccionCompleta = propiedad.getCalle() + " " + propiedad.getNumExt() +
                    (propiedad.getNumInt() != null && !propiedad.getNumInt().isEmpty() ?
                            " Int. " + propiedad.getNumInt() : "") + ", " + propiedad.getColonia();
            binding.txtDireccionCompleta.setText(direccionCompleta);

            // Detalles de ubicación
            binding.txtNumExt.setText("Número Exterior: " + propiedad.getNumExt());
            binding.txtNumInt.setText("Número Interior: " + propiedad.getNumInt());
            binding.txtNumInt.setVisibility(propiedad.getNumInt() != null &&
                    !propiedad.getNumInt().isEmpty() ? View.VISIBLE : View.GONE);

            binding.txtCalle.setText("Calle: " + propiedad.getCalle());
            binding.txtColonia.setText("Colonia: " + propiedad.getColonia());
            binding.txtCodigoPostal.setText("Código Postal: " + propiedad.getCodigoP());

            // Coordenadas
            binding.txtLatitud.setText("Latitud: " + propiedad.getLatitud());
            binding.txtLongitud.setText("Longitud: " + propiedad.getLongitud());

            if (propiedad.getCiudad() != null && propiedad.getCiudad().getIdCiudad() != 0) {
                cargarTodasCiudadesYFiltrar(propiedad.getCiudad().getIdCiudad());
            } else {
                binding.txtCiudad.setText("Ciudad: No especificada");
                binding.txtEstado.setText("Estado: No especificado");
            }

            // Medidor
            Medidor medidor = propiedad.getMedidor();
            if (medidor != null) {
                binding.txtIdMedidor.setText("ID Medidor: " + medidor.getIdMedidor());
                binding.txtNombreMedidor.setText("Medidor: " + medidor.getNombre());
                binding.txtModeloMedidor.setText("Modelo: " + medidor.getModelo());
            }

            // Estado
            binding.txtEstatus.setText("Estado: " + (propiedad.getEstatus() == 1 ? "Activo" : "Inactivo"));

            // Imagen
            if (propiedad.getFoto() != null && !propiedad.getFoto().isEmpty()) {
                Glide.with(requireContext())
                        .load(propiedad.getFoto())
                        .into(binding.imgPropiedad);
            }

        } catch (Exception e) {
            mostrarError("Error al mostrar datos de propiedad");
            Log.e("GalleryError", "Error en actualizarUI", e);
        }
    }

    private void cargarTodasCiudadesYFiltrar(int idCiudadBuscada) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Globals.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CiudadApiService ciudadService = retrofit.create(CiudadApiService.class);
        Call<List<Ciudad>> callCiudades = ciudadService.obtenerTodosLasCiudadesEstados();

        callCiudades.enqueue(new Callback<List<Ciudad>>() {
            @Override
            public void onResponse(Call<List<Ciudad>> call, Response<List<Ciudad>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Ciudad> ciudades = response.body();
                    buscarCiudadEspecifica(ciudades, idCiudadBuscada);
                } else {
                    manejarErrorCiudad("No se pudieron obtener las ciudades");
                }
            }

            @Override
            public void onFailure(Call<List<Ciudad>> call, Throwable t) {
                manejarErrorCiudad("Error al cargar ciudades: " + t.getMessage());
            }
        });
    }

    private void buscarCiudadEspecifica(List<Ciudad> ciudades, int idCiudadBuscada) {
        for (Ciudad ciudad : ciudades) {
            if (ciudad.getIdCiudad() == idCiudadBuscada) {
                mostrarInformacionCiudad(ciudad);
                return;
            }
        }
        binding.txtCiudad.setText("Ciudad: No encontrada");
        binding.txtEstado.setText("Estado: No encontrado");
    }

    private void mostrarInformacionCiudad(Ciudad ciudad) {
        try {
            binding.txtCiudad.setText("Ciudad: " + ciudad.getNombreCiudad());

            if (ciudad.getIdEstado() != null) {
                binding.txtEstado.setText("Estado: " + ciudad.getIdEstado().getNombreEstado());
            } else {
                binding.txtEstado.setText("Estado: No especificado");
            }
        } catch (Exception e) {
            Log.e("GalleryError", "Error al mostrar información de ciudad", e);
            binding.txtCiudad.setText("Ciudad: Error al cargar");
            binding.txtEstado.setText("Estado: Error al cargar");
        }
    }

    private void manejarErrorCiudad(String mensajeError) {
        Log.e("GalleryError", mensajeError);
        binding.txtCiudad.setText("Ciudad: Error al cargar");
        binding.txtEstado.setText("Estado: Error al cargar");
    }

    private void mostrarError(String mensaje) {
        try {
            Toast.makeText(requireContext(), mensaje, Toast.LENGTH_SHORT).show();
            Log.e("GalleryError", mensaje);
        } catch (Exception e) {
            Log.e("GalleryError", "Error al mostrar mensaje: " + mensaje, e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}