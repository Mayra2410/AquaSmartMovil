package com.org.utl.aquasmartv1.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
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
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
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

        binding.contentLayout.setOnClickListener(v -> {
            if (viewModel.getClientesLiveData().getValue() != null &&
                    !viewModel.getClientesLiveData().getValue().isEmpty()) {

                mostrarDialogoEdicionNombre(viewModel.getClientesLiveData().getValue().get(0));
            } else {
                Toast.makeText(getContext(), "Cargando datos del cliente...", Toast.LENGTH_SHORT).show();
                loadClienteData();
            }
        });
    }

    private void setupObservers() {
        viewModel.getClientesLiveData().observe(getViewLifecycleOwner(), clientes -> {
            if (clientes != null && !clientes.isEmpty()) {
                mostrarDatosCliente(clientes);
            } else {
                mostrarError("No se encontraron datos del cliente");
            }
        });

        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                mostrarError(error);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                mostrarCarga(isLoading);
            }
        });
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
            if (cliente.getPersona() != null) {
                // Mostrar información básica
                binding.txtNombrePersona.setText(cliente.getPersona().getNombre() != null ?
                        cliente.getPersona().getNombre() : "");

                binding.txtApePaterno.setText(cliente.getPersona().getApellidoP() != null ?
                        cliente.getPersona().getApellidoP() : "");

                binding.txtApeMaterno.setText(cliente.getPersona().getApellidoM() != null ?
                        cliente.getPersona().getApellidoM() : "");

                binding.txtEdad.setText(cliente.getPersona().getEdad() > 0 ?
                        String.valueOf(cliente.getPersona().getEdad()) : "");

                binding.txtEmail.setText(cliente.getPersona().getEmail() != null ?
                        cliente.getPersona().getEmail() : "");

                binding.txtTelefono.setText(cliente.getPersona().getTelefono() != null ?
                        cliente.getPersona().getTelefono() : "");

                // Mostrar ciudad y estado
                if (cliente.getPersona().getCiudad() != null) {
                    String ciudadEstado = "";
                    if (cliente.getPersona().getCiudad().getNombre() != null) {
                        ciudadEstado = cliente.getPersona().getCiudad().getNombre();
                    }
                    if (cliente.getPersona().getCiudad().getEstado() != null &&
                            cliente.getPersona().getCiudad().getEstado().getNombre() != null) {
                        ciudadEstado += " - " + cliente.getPersona().getCiudad().getEstado().getNombre();
                    }
                    binding.txtCiudad.setText(ciudadEstado);
                }

                // Mostrar información de usuario
                if (cliente.getPersona().getUsuario() != null) {
                    binding.txtUsuario.setText(cliente.getPersona().getUsuario().getNombre() != null ?
                            cliente.getPersona().getUsuario().getNombre() : "");

                    binding.txtContrasenia.setText(cliente.getPersona().getUsuario().getContrasenia() != null ?
                            "••••••••" : "");

                    // Cargar imagen de perfil
                    if (cliente.getPersona().getUsuario().getFoto() != null &&
                            !cliente.getPersona().getUsuario().getFoto().isEmpty()) {
                        cargarImagenBase64(cliente.getPersona().getUsuario().getFoto());
                    } else {
                        binding.imgPerfil.setImageResource(R.drawable.usuario);
                    }

                    // Mostrar última conexión
                    if (cliente.getPersona().getUsuario().getDateLastToken() != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                        String fechaFormateada = sdf.format(cliente.getPersona().getUsuario().getDateLastToken());
                        binding.txtUltimaConexion.setText(fechaFormateada);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("HomeFragment", "Error al mostrar datos del cliente", e);
            mostrarError("Error al mostrar datos del cliente");
        }
    }

    private void cargarImagenBase64(String base64String) {
        try {
            String base64Image = base64String.split(",").length > 1 ?
                    base64String.split(",")[1] : base64String;

            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            if (decodedByte != null) {
                binding.imgPerfil.setImageBitmap(decodedByte);
            } else {
                binding.imgPerfil.setImageResource(R.drawable.usuario);
            }
        } catch (Exception e) {
            Log.e("HomeFragment", "Error al cargar imagen Base64", e);
            binding.imgPerfil.setImageResource(R.drawable.usuario);
        }
    }

    private void mostrarError(String mensaje) {
        if (getContext() != null) {
            Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
        }
    }

    private void mostrarCarga(Boolean isLoading) {
        if (binding != null) {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void mostrarDialogoEdicionNombre(Cliente cliente) {
        if (getActivity() == null || cliente == null || cliente.getPersona() == null) {
            return;
        }

        String nombre = cliente.getPersona().getNombre() != null ? cliente.getPersona().getNombre() : "";
        String apellidoP = cliente.getPersona().getApellidoP() != null ? cliente.getPersona().getApellidoP() : "";
        String apellidoM = cliente.getPersona().getApellidoM() != null ? cliente.getPersona().getApellidoM() : "";
        String edad = cliente.getPersona().getEdad() > 0 ? String.valueOf(cliente.getPersona().getEdad()) : "";
        String email = cliente.getPersona().getEmail() != null ? cliente.getPersona().getEmail() : "";
        String telefono = cliente.getPersona().getTelefono() != null ? cliente.getPersona().getTelefono() : "";

        String ciudadEstado = "";
        if (cliente.getPersona().getCiudad() != null) {
            if (cliente.getPersona().getCiudad().getNombre() != null) {
                ciudadEstado = cliente.getPersona().getCiudad().getNombre();
            }
            if (cliente.getPersona().getCiudad().getEstado() != null &&
                    cliente.getPersona().getCiudad().getEstado().getNombre() != null) {
                ciudadEstado += " - " + cliente.getPersona().getCiudad().getEstado().getNombre();
            }
        }

        String nombreUsuario = cliente.getPersona().getUsuario() != null ?
                cliente.getPersona().getUsuario().getNombre() : "";
        String contrasenia = cliente.getPersona().getUsuario() != null ?
                cliente.getPersona().getUsuario().getContrasenia() : "";
        String imagenBase64 = cliente.getPersona().getUsuario() != null ?
                cliente.getPersona().getUsuario().getFoto() : "";

        CustomNombrePersona dialog = new CustomNombrePersona(
                (FragmentActivity) getActivity(),
                nombre,
                apellidoP,
                apellidoM,
                edad,
                email,
                telefono,
                ciudadEstado,
                nombreUsuario,
                contrasenia,
                imagenBase64,
                (nuevoNombre, nuevoApellidoP, nuevoApellidoM, nuevoEdad, nuevoEmail,
                 nuevoTelefono, ciudadId, nuevoNombreUsuario, nuevaContrasenia, nuevaImagenBase64) -> {

                    if (cliente.getPersona() != null) {
                        // Actualizar datos de la persona
                        cliente.getPersona().setNombre(nuevoNombre);
                        cliente.getPersona().setApellidoP(nuevoApellidoP);
                        cliente.getPersona().setApellidoM(nuevoApellidoM);

                        try {
                            cliente.getPersona().setEdad(Integer.parseInt(nuevoEdad));
                        } catch (NumberFormatException e) {
                            cliente.getPersona().setEdad(0);
                        }

                        cliente.getPersona().setEmail(nuevoEmail);
                        cliente.getPersona().setTelefono(nuevoTelefono);

                        // Actualizar datos de usuario si existen
                        if (cliente.getPersona().getUsuario() != null) {
                            cliente.getPersona().getUsuario().setNombre(nuevoNombreUsuario);
                            cliente.getPersona().getUsuario().setContrasenia(nuevaContrasenia);
                            cliente.getPersona().getUsuario().setFoto(nuevaImagenBase64);
                        }

                        // Refrescar la vista
                        mostrarDatosCliente(List.of(cliente));
                        // TODO: Descomentar cuando esté listo el método de actualización
                        // viewModel.actualizarCliente(cliente);
                    }
                });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}