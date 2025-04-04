package com.org.utl.aquasmartv1.ui.gallery;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import com.org.utl.aquasmartv1.R;
import com.org.utl.aquasmartv1.RegistroPropiedad;
import com.org.utl.aquasmartv1.custom_registro_propiedad;
import com.org.utl.aquasmartv1.databinding.FragmentGalleryBinding;
import com.org.utl.aquasmartv1.api.CiudadApiService;
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

        binding.btnRegistrarPropiedad.setOnClickListener(v -> {
            Log.d("GalleryFragment", "Botón registrar propiedad presionado");
            Intent intent = new Intent(requireActivity(), RegistroPropiedad.class);
            startActivity(intent);
        });

        // Observadores
        galleryViewModel.getPropiedadesLiveData().observe(getViewLifecycleOwner(), propiedades -> {
            if (propiedades == null || propiedades.isEmpty()) {
                mostrarSinPropiedades();
            } else {
                actualizarUI(propiedades);
            }
        });

        galleryViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), this::mostrarError);

        // Cargar propiedades
        String usuario = obtenerUsuarioLogueado();
        if (usuario != null && !usuario.isEmpty()) {
            galleryViewModel.cargarPropiedades(usuario);
        } else {
            mostrarError("Usuario no identificado");
            mostrarSinPropiedades();
        }
    }

    private void mostrarSinPropiedades() {
        // Ocultar TODOS los elementos de la propiedad
        binding.textView4.setVisibility(View.GONE);
        binding.imgPropiedad.setVisibility(View.GONE);
        binding.btnModificarPropiedad.setVisibility(View.GONE);
        binding.tvImagePath.setVisibility(View.GONE);

        // Ocultar todos los TextViews de datos
        int[] textViewIds = {
                R.id.txtDireccionCompleta, R.id.txtNumExt, R.id.txtNumInt,
                R.id.txtCalle, R.id.txtColonia, R.id.txtCodigoPostal,
                R.id.txtLatitud, R.id.txtLongitud, R.id.txtCiudad,
                R.id.txtEstado, R.id.txtIdMedidor, R.id.txtNombreMedidor,
                R.id.txtModeloMedidor, R.id.txtEstatus
        };

        for (int id : textViewIds) {
            binding.getRoot().findViewById(id).setVisibility(View.GONE);
        }

        // Mostrar elementos de "sin propiedades"
        binding.txtSinPropiedades.setVisibility(View.VISIBLE);
        binding.btnRegistrarPropiedad.setVisibility(View.VISIBLE);
    }

    private void actualizarUI(List<Propiedad> propiedades) {
        // Ocultar elementos de "sin propiedades"
        binding.txtSinPropiedades.setVisibility(View.GONE);
        binding.btnRegistrarPropiedad.setVisibility(View.GONE);

        // Mostrar TODOS los elementos de la propiedad
        binding.textView4.setVisibility(View.VISIBLE);
        binding.imgPropiedad.setVisibility(View.VISIBLE);
        binding.btnModificarPropiedad.setVisibility(View.VISIBLE);
        binding.tvImagePath.setVisibility(View.VISIBLE);

        // Mostrar todos los TextViews de datos
        int[] textViewIds = {
                R.id.txtDireccionCompleta, R.id.txtNumExt, R.id.txtNumInt,
                R.id.txtCalle, R.id.txtColonia, R.id.txtCodigoPostal,
                R.id.txtLatitud, R.id.txtLongitud, R.id.txtCiudad,
                R.id.txtEstado, R.id.txtIdMedidor, R.id.txtNombreMedidor,
                R.id.txtModeloMedidor, R.id.txtEstatus
        };

        for (int id : textViewIds) {
            binding.getRoot().findViewById(id).setVisibility(View.VISIBLE);
        }
        Propiedad propiedad = propiedades.get(0);
        try {
            // Mostrar datos de la propiedad
            String direccionCompleta = propiedad.getCalle() + " " + propiedad.getNumExt() +
                    (propiedad.getNumInt() != null && !propiedad.getNumInt().isEmpty() ?
                            " Int. " + propiedad.getNumInt() : "") + ", " + propiedad.getColonia();
            binding.txtDireccionCompleta.setText(direccionCompleta);

            binding.txtNumExt.setText("Número Exterior: " + propiedad.getNumExt());
            binding.txtNumInt.setText("Número Interior: " + propiedad.getNumInt());
            binding.txtNumInt.setVisibility(propiedad.getNumInt() != null &&
                    !propiedad.getNumInt().isEmpty() ? View.VISIBLE : View.GONE);

            binding.txtCalle.setText("Calle: " + propiedad.getCalle());
            binding.txtColonia.setText("Colonia: " + propiedad.getColonia());
            binding.txtCodigoPostal.setText("Código Postal: " + propiedad.getCodigoP());

            binding.txtLatitud.setText("Latitud: " + propiedad.getLatitud());
            binding.txtLongitud.setText("Longitud: " + propiedad.getLongitud());

            if (propiedad.getCiudad() != null && propiedad.getCiudad().getIdCiudad() != 0) {
                cargarTodasCiudadesYFiltrar(propiedad.getCiudad().getIdCiudad());
            } else {
                binding.txtCiudad.setText("Ciudad: No especificada");
                binding.txtEstado.setText("Estado: No especificado");
            }

            // Mostrar datos del medidor
            Medidor medidor = propiedad.getMedidor();
            if (medidor != null) {
                binding.txtIdMedidor.setText("ID Medidor: " + medidor.getIdMedidor());
                binding.txtNombreMedidor.setText("Medidor: " + medidor.getNombre());
                binding.txtModeloMedidor.setText("Modelo: " + medidor.getModelo());
            }

            binding.txtEstatus.setText("Estado: " + (propiedad.getEstatus() == 1 ? "Activo" : "Inactivo"));

            // Mostrar imagen
            if (propiedad.getFoto() != null && !propiedad.getFoto().isEmpty()) {
                Glide.with(requireContext())
                        .load(propiedad.getFoto())
                        .error(R.drawable.placeholder)
                        .into(binding.imgPropiedad);
            } else {
                binding.imgPropiedad.setImageResource(R.drawable.placeholder);
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
                    buscarCiudadEspecifica(response.body(), idCiudadBuscada);
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
            binding.txtCiudad.setText("Ciudad: " + ciudad.getNombre());
            binding.txtEstado.setText("Estado: " + (ciudad.getEstado() != null ? ciudad.getEstado().getNombre() : "No especificado"));
        } catch (Exception e) {
            Log.e("GalleryError", "Error al mostrar ciudad", e);
            binding.txtCiudad.setText("Ciudad: Error al cargar");
            binding.txtEstado.setText("Estado: Error al cargar");
        }
    }

    private String obtenerUsuarioLogueado() {
        try {
            SharedPreferences sharedPref = requireContext().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
            return sharedPref.getString("username", null);
        } catch (Exception e) {
            Log.e("GalleryError", "Error al obtener usuario", e);
            return null;
        }
    }

    private void verificarArchivoPrefs() {
        try {
            File prefsFile = new File(requireContext().getApplicationInfo().dataDir + "/shared_prefs/login_prefs.xml");
            Log.d("GalleryPrefs", "Archivo existe: " + prefsFile.exists());
        } catch (Exception e) {
            Log.e("GalleryError", "Error al verificar prefs", e);
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
            Log.e("GalleryError", "Error al mostrar error", e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}