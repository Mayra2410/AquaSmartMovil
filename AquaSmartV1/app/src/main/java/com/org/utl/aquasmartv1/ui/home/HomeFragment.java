package com.org.utl.aquasmartv1.ui.home;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.org.utl.aquasmartv1.CustomNombrePersona;
import com.org.utl.aquasmartv1.R;
import com.org.utl.aquasmartv1.api.ImageUtils;
import com.org.utl.aquasmartv1.databinding.FragmentHomeBinding;
import com.org.utl.aquasmartv1.modal.Cliente;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private static final int REQUEST_IMAGE_OPEN = 1;
    private static final int REQUEST_CODE_IMAGE = 1001;
    private static final int REQUEST_PERMISSION_READ_STORAGE = 1002;
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
        binding.imgPerfil.setOnClickListener(v -> abrirSelectorImagen());
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
    private void abrirSelectorImagen() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
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

                if (cliente.getPersona().getUsuario() != null) {
                    binding.txtUsuario.setText(cliente.getPersona().getUsuario().getNombre() != null ?
                            cliente.getPersona().getUsuario().getNombre() : "");
                    binding.txtContrasenia.setText(cliente.getPersona().getUsuario().getContrasenia() != null ?
                            "••••••••" : "");

                    String fotoBase64 = cliente.getPersona().getUsuario().getFoto();
                    if (fotoBase64 != null && !fotoBase64.isEmpty()) {
                        cargarImagen(fotoBase64);
                    } else {
                        binding.imgPerfil.setImageResource(R.drawable.usuario);
                    }

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

    private void cargarImagen(String imageUri) {
        if (imageUri == null || imageUri.isEmpty()) {
            binding.imgPerfil.setImageResource(R.drawable.usuario);
            return;
        }

        // Verifica permisos primero
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_READ_STORAGE);
            return;
        }

        Bitmap bitmap = ImageUtils.loadImage(requireContext(), imageUri);

        if (bitmap != null) {
            binding.imgPerfil.setImageBitmap(bitmap);
        } else {
            binding.imgPerfil.setImageResource(R.drawable.usuario);
            Toast.makeText(getContext(), "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show();
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
        if (cliente == null || cliente.getPersona() == null || getActivity() == null) {
            return;
        }

        String imagenBase64 = cliente.getPersona().getUsuario() != null ?
                cliente.getPersona().getUsuario().getFoto() : "";

        CustomNombrePersona dialog = new CustomNombrePersona(
                requireActivity(),
                cliente.getPersona().getNombre(),
                cliente.getPersona().getApellidoP(),
                cliente.getPersona().getApellidoM(),
                String.valueOf(cliente.getPersona().getEdad()),
                cliente.getPersona().getEmail(),
                cliente.getPersona().getTelefono(),
                cliente.getPersona().getCiudad() != null ?
                        cliente.getPersona().getCiudad().getNombre() + " - " +
                                cliente.getPersona().getCiudad().getEstado().getNombre() : "",
                cliente.getPersona().getUsuario() != null ?
                        cliente.getPersona().getUsuario().getNombre() : "",
                cliente.getPersona().getUsuario() != null ?
                        cliente.getPersona().getUsuario().getContrasenia() : "",
                imagenBase64,
                (nuevoNombre, nuevoApellidoP, nuevoApellidoM, nuevoEdad, nuevoEmail,
                 nuevoTelefono, ciudadId, nuevoNombreUsuario, nuevaContrasenia, nuevaImagenBase64) -> {

                    binding.imgPerfil.setImageDrawable(null);

                    cliente.getPersona().setNombre(nuevoNombre);
                    cliente.getPersona().setApellidoP(nuevoApellidoP);
                    cliente.getPersona().setApellidoM(nuevoApellidoM);
                    cliente.getPersona().setEdad(Integer.parseInt(nuevoEdad));
                    cliente.getPersona().setEmail(nuevoEmail);
                    cliente.getPersona().setTelefono(nuevoTelefono);

                    if (cliente.getPersona().getUsuario() != null) {
                        cliente.getPersona().getUsuario().setNombre(nuevoNombreUsuario);
                        cliente.getPersona().getUsuario().setContrasenia(nuevaContrasenia);
                        cliente.getPersona().getUsuario().setFoto(nuevaImagenBase64);
                    }

                    mostrarDatosCliente(List.of(cliente));
                    /*viewModel.actualizarCliente(cliente).observe(getViewLifecycleOwner(), success -> {
                        if (!success) {
                            mostrarError("Error al actualizar los datos");
                        }
                    });*/
                });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}