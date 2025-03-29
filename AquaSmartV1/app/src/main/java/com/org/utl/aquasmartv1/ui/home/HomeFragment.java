package com.org.utl.aquasmartv1.ui.home;

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

import com.org.utl.aquasmartv1.CustomNombrePersona;
import com.org.utl.aquasmartv1.R;
import com.org.utl.aquasmartv1.databinding.FragmentHomeBinding;
import com.org.utl.aquasmartv1.modal.Cliente;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupObservers();
        loadClienteData();
    }

    private void setupObservers() {
        viewModel.getClientesLiveData().observe(getViewLifecycleOwner(), this::mostrarDatosCliente);
        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), this::mostrarError);
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), this::mostrarCarga);
    }

    private void loadClienteData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("login_prefs", Context.MODE_PRIVATE);
        String username = prefs.getString("username", null);

        if (username != null && !username.isEmpty()) {
            viewModel.cargarCliente(username);
        } else {
            mostrarError("Usuario no identificado");
        }
    }

    private void mostrarDatosCliente(List<Cliente> clientes) {
        if (clientes == null || clientes.isEmpty()) {
            mostrarError("No hay datos del cliente");
            return;
        }

        Cliente cliente = clientes.get(0);
        try {
            // Verificar objeto Persona
            if (cliente.getPersona() != null) {
                // Datos básicos
                binding.txtNombrePersona.setText(cliente.getPersona().getNombre() != null ?
                        cliente.getPersona().getNombre() : "");

                binding.txtApePaterno.setText(cliente.getPersona().getApellidoP() != null ?
                        cliente.getPersona().getApellidoP() : "");
                binding.txtApeMaterno.setText(cliente.getPersona().getApellidoM() != null ?
                        cliente.getPersona().getApellidoM() : "");

                // Edad (convertir int a String)
                binding.txtEdad.setText(String.valueOf(cliente.getPersona().getEdad()));

                binding.txtEmail.setText(cliente.getPersona().getEmail() != null ?
                        cliente.getPersona().getEmail() : "");

                binding.txtTelefono.setText(cliente.getPersona().getTelefono() != null ?
                        cliente.getPersona().getTelefono() : "");

                // Verificar Ciudad
                if (cliente.getPersona().getCiudad() != null) {
                    binding.txtCiudad.setText(cliente.getPersona().getCiudad().getNombre() != null ?
                            cliente.getPersona().getCiudad().getNombre() + " - " + cliente.getPersona().getCiudad().getEstado().getNombre() : "");
                } else {
                    binding.txtCiudad.setText("");
                }

                // Verificar Usuario
                if (cliente.getPersona().getUsuario() != null) {
                    binding.txtUsuario.setText(cliente.getPersona().getUsuario().getNombre() != null ?
                            cliente.getPersona().getUsuario().getNombre() : "");

                    // Manejo seguro de contraseña (considera enmascararla)
                    String contrasenia = cliente.getPersona().getUsuario().getContrasenia();
                    binding.txtContrasenia.setText(contrasenia != null ?
                            "••••••••" : ""); // Enmascarar contraseña
                    if (cliente.getPersona().getUsuario().getFoto() != null &&
                            !cliente.getPersona().getUsuario().getFoto().isEmpty()) {
                        cargarImagenBase64(cliente.getPersona().getUsuario().getFoto());
                    } else {
                        binding.imgPerfil.setImageResource(R.drawable.usuario);
                    }
                } else {
                    binding.txtUsuario.setText("");
                    binding.txtContrasenia.setText("");
                }
            }

            // Manejo del Timestamp (como en tu código anterior)
            if (cliente.getPersona() != null &&
                    cliente.getPersona().getUsuario() != null &&
                    cliente.getPersona().getUsuario().getDateLastToken() != null) {

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                String fechaFormateada = sdf.format(cliente.getPersona().getUsuario().getDateLastToken());
                binding.txtUltimaConexion.setText(fechaFormateada);
            }

        } catch (Exception e) {
            mostrarError("Error al mostrar datos");
            Log.e("HomeFragment", "Error en mostrarDatosCliente", e);
        }
        // Agrega al final del try, después de cargar todos los datos:
        binding.contentLayout.setOnClickListener(v -> {
            mostrarDialogoEdicionNombre(cliente);
        });

    }
    private void cargarImagenBase64(String base64String) {
        try {
            // Eliminar el prefijo si existe (ej: "data:image/jpeg;base64,")
            String base64Image = base64String.split(",")[1];

            // Decodificar la cadena Base64
            byte[] decodedString = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT);

            // Convertir a Bitmap
            android.graphics.Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(
                    decodedString, 0, decodedString.length);

            // Cargar la imagen en el ImageView
            binding.imgPerfil.setImageBitmap(decodedByte);

        } catch (Exception e) {
            Log.e("HomeFragment", "Error al cargar imagen Base64", e);
            binding.imgPerfil.setImageResource(R.drawable.usuario);
        }
    }
    private void mostrarError(String mensaje) {
        Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
    }

    private void mostrarCarga(Boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }
    private void mostrarDialogoEdicionNombre(Cliente cliente) {
        if (cliente == null || cliente.getPersona() == null) {
            return;
        }

        // Obtener datos actuales
        String nombreActual = cliente.getPersona().getNombre() != null ?
                cliente.getPersona().getNombre() : "";
        String apellidoPActual = cliente.getPersona().getApellidoP() != null ?
                cliente.getPersona().getApellidoP() : "";
        String apellidoMActual = cliente.getPersona().getApellidoM() != null ?
                cliente.getPersona().getApellidoM() : "";

        String edadActual = String.valueOf(cliente.getPersona().getEdad());
        String emailActual =  cliente.getPersona().getEmail() != null ?
                cliente.getPersona().getEmail() : "";
        String telefonoActual = cliente.getPersona().getTelefono() != null ?
                cliente.getPersona().getTelefono() : "";


        // Crear diálogo
        CustomNombrePersona dialog = new CustomNombrePersona(
                requireContext(),
                nombreActual,
                apellidoPActual,
                apellidoMActual,
                edadActual,
                emailActual,
                telefonoActual,
                new CustomNombrePersona.OnDialogResultListener() {
                    @Override
                    public void onDialogResult(String nuevoNombre, String nuevoApellidoP, String nuevoApellidoM,
                                               String nuevoEdad, String nuevoEmail, String nuevoTelefono) {
                        // Actualizar UI
                        binding.txtNombrePersona.setText(nuevoNombre != null ? nuevoNombre : "");
                        binding.txtApePaterno.setText(nuevoApellidoP != null ? nuevoApellidoP : "");
                        binding.txtApeMaterno.setText(nuevoApellidoM != null ? nuevoApellidoM : "");
                        binding.txtEdad.setText(nuevoEdad != null ? nuevoEdad : "");
                        binding.txtEmail.setText(nuevoEmail != null ? nuevoEmail : "");
                        binding.txtTelefono.setText(nuevoTelefono != null ? nuevoTelefono : "");
                    }
                }
        );
        dialog.show();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}