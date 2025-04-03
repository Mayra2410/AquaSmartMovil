package com.org.utl.aquasmartv1;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.org.utl.aquasmartv1.api.CiudadApiService;
import com.org.utl.aquasmartv1.api.EstadoApiService;
import com.org.utl.aquasmartv1.api.Globals;
import com.org.utl.aquasmartv1.api.ImageUtils;
import com.org.utl.aquasmartv1.modal.Ciudad;
import com.org.utl.aquasmartv1.modal.Estado;
import com.org.utl.aquasmartv1.R;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CustomNombrePersona extends Dialog {

    private String nombre, apellidoP, apellidoM, edad, telefono, email, ciudadEstado, nombreUsuario, contrasenia;
    private EditText editNombre, editApellidoP, editApellidoM, editEdad, editCorreo, editTelefono, editNombreUsuario, editContrasenia;
    private Spinner spinnerCiudad;
    private Button btnOk, btnCancel, btnOpenGallery, btnTogglePassword;
    private GifImageView imageView;
    private TextView tvImagePath;
    private OnDialogResultListener listener;
    private List<Ciudad> ciudades = new ArrayList<>();
    private String selectedCiudadId;
    private String imageBase64;
    private FragmentActivity fragmentActivity;
    private boolean isPasswordVisible = false;

    public interface OnDialogResultListener {
        void onDialogResult(
                String nombre,
                String apellidoP,
                String apellidoM,
                String edad,
                String email,
                String telefono,
                String ciudadId,
                String nombreUsuario,
                String contrasenia,
                String imagenBase64
        );
    }

    public CustomNombrePersona(
            @NonNull FragmentActivity activity,
            String nombre,
            String apellidoP,
            String apellidoM,
            String edad,
            String email,
            String telefono,
            String ciudadEstado,
            String nombreUsuario,
            String contrasenia,
            String imagenBase64,
            OnDialogResultListener listener
    ) {
        super(activity);
        this.fragmentActivity = activity;
        this.nombre = nombre;
        this.apellidoP = apellidoP;
        this.apellidoM = apellidoM;
        this.edad = edad;
        this.email = email;
        this.telefono = telefono;
        this.ciudadEstado = ciudadEstado;
        this.nombreUsuario = nombreUsuario;
        this.contrasenia = contrasenia;
        this.imageBase64 = imagenBase64;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_custom_nombre_persona);

        initViews();
        setupSpinner();
        setupActionButtons();
        setupPasswordToggle();

        if (imageBase64 != null && !imageBase64.isEmpty()) {
            cargarImagen(imageBase64);
        } else {
            imageView.setImageResource(R.drawable.usuario);
            tvImagePath.setText("Sin imagen de perfil");
        }
    }

    private void initViews() {
        editNombre = findViewById(R.id.editNombre);
        editApellidoP = findViewById(R.id.editApellidoP);
        editApellidoM = findViewById(R.id.editApellidoM);
        editEdad = findViewById(R.id.editEdad);
        editTelefono = findViewById(R.id.editTelefono);
        editCorreo = findViewById(R.id.editCorreo);
        editNombreUsuario = findViewById(R.id.editNombreUsuario);
        editContrasenia = findViewById(R.id.editContrasenia);
        spinnerCiudad = findViewById(R.id.spinnerCiudad);
        btnOk = findViewById(R.id.btnOk);
        btnCancel = findViewById(R.id.btnCancel);
        btnOpenGallery = findViewById(R.id.btnOpenGallery);
        btnTogglePassword = findViewById(R.id.btnTogglePassword);
        tvImagePath = findViewById(R.id.tvImagePath);
        imageView = findViewById(R.id.imageView);

        editNombre.setText(nombre);
        editApellidoP.setText(apellidoP);
        editApellidoM.setText(apellidoM);
        editEdad.setText(edad);
        editCorreo.setText(email);
        editTelefono.setText(telefono);
        editNombreUsuario.setText(nombreUsuario);
        editContrasenia.setText(contrasenia);
    }

    private void setupPasswordToggle() {
        btnTogglePassword.setOnClickListener(v -> togglePasswordVisibility());
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Ocultar contraseña
            editContrasenia.setTransformationMethod(new PasswordTransformationMethod());
            btnTogglePassword.setText("Mostrar");
        } else {
            // Mostrar contraseña
            editContrasenia.setTransformationMethod(null);
            btnTogglePassword.setText("Ocultar");
        }
        isPasswordVisible = !isPasswordVisible;
        editContrasenia.setSelection(editContrasenia.getText().length());
    }

    private void setupActionButtons() {
        btnOpenGallery.setOnClickListener(v -> openGallery());
        btnOk.setOnClickListener(v -> validateAndSubmit());
        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void openGallery() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            fragmentActivity.startActivityForResult(intent, 100);
        } catch (Exception e) {
            Log.e("CustomNombrePersona", "Error al abrir galería", e);
            Toast.makeText(getContext(), "Error al abrir galería", Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == FragmentActivity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                try {
                    Bitmap bitmap = ImageUtils.getBitmapFromUri(getContext(), selectedImageUri);
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                        imageBase64 = ImageUtils.bitmapToBase64(bitmap);
                        tvImagePath.setText("Imagen seleccionada");
                    }
                } catch (Exception e) {
                    Log.e("CustomNombrePersona", "Error al procesar imagen", e);
                    Toast.makeText(getContext(), "Error al procesar imagen", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void cargarImagen(String imageData) {
        try {
            Bitmap bitmap = ImageUtils.loadImage(getContext(), imageData);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                tvImagePath.setText("Imagen actual del perfil");
            } else {
                imageView.setImageResource(R.drawable.usuario);
                tvImagePath.setText("Error al cargar imagen");
            }
        } catch (Exception e) {
            Log.e("CustomNombrePersona", "Error al cargar imagen", e);
            imageView.setImageResource(R.drawable.usuario);
            tvImagePath.setText("Error al cargar imagen");
        }
    }

    private void validateAndSubmit() {
        String nombre = editNombre.getText().toString().trim();
        if (nombre.isEmpty()) {
            editNombre.setError("El nombre es obligatorio");
            return;
        }

        if (listener != null) {
            listener.onDialogResult(
                    nombre,
                    editApellidoP.getText().toString(),
                    editApellidoM.getText().toString(),
                    editEdad.getText().toString(),
                    editCorreo.getText().toString(),
                    editTelefono.getText().toString(),
                    selectedCiudadId,
                    editNombreUsuario.getText().toString(),
                    editContrasenia.getText().toString(),
                    imageBase64
            );
        }
        dismiss();
    }

    // Método para generar hash MD5 (si necesitas generar uno nuevo)
    private String generateMD5(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(text.getBytes());
            byte[] messageDigest = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String h = Integer.toHexString(0xFF & b);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    private void setupSpinner() {
        obtenerTodosLasCiudadesEstados();
        spinnerCiudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < ciudades.size()) {
                    selectedCiudadId = String.valueOf(ciudades.get(position).getIdCiudad());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCiudadId = null;
            }
        });
    }

    private void obtenerTodosLasCiudadesEstados() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Globals.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EstadoApiService estadoService = retrofit.create(EstadoApiService.class);
        estadoService.obtenerTodosLosEstados().enqueue(new Callback<List<Estado>>() {
            @Override
            public void onResponse(Call<List<Estado>> call, Response<List<Estado>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    obtenerCiudadesYCombinarConEstados(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Estado>> call, Throwable t) {
                mostrarError("Error de conexión: " + t.getMessage());
            }
        });
    }

    private void obtenerCiudadesYCombinarConEstados(List<Estado> estados) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Globals.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CiudadApiService ciudadService = retrofit.create(CiudadApiService.class);
        ciudadService.obtenerTodosLasCiudadesEstados().enqueue(new Callback<List<Ciudad>>() {
            @Override
            public void onResponse(Call<List<Ciudad>> call, Response<List<Ciudad>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Ciudad> ciudades = response.body();
                    for (Ciudad ciudad : ciudades) {
                        for (Estado estado : estados) {
                            if (ciudad.getEstado().getIdEstado() == estado.getIdEstado()) {
                                ciudad.setEstado(estado);
                                break;
                            }
                        }
                    }
                    mostrarCiudadesEstadoEnSpinner(ciudades);
                }
            }

            @Override
            public void onFailure(Call<List<Ciudad>> call, Throwable t) {
                mostrarError("Error de conexión: " + t.getMessage());
            }
        });
    }

    private void mostrarCiudadesEstadoEnSpinner(List<Ciudad> ciudades) {
        this.ciudades = ciudades;
        List<String> nombresCiudades = new ArrayList<>();
        for (Ciudad ciudad : ciudades) {
            nombresCiudades.add(ciudad.getNombre() + " - " + ciudad.getEstado().getNombre());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, nombresCiudades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCiudad.setAdapter(adapter);

        if (ciudadEstado != null && !ciudadEstado.isEmpty()) {
            for (int i = 0; i < ciudades.size(); i++) {
                if ((ciudades.get(i).getNombre() + " - " + ciudades.get(i).getEstado().getNombre()).equals(ciudadEstado)) {
                    spinnerCiudad.setSelection(i);
                    selectedCiudadId = String.valueOf(ciudades.get(i).getIdCiudad());
                    break;
                }
            }
        }
    }

    private void mostrarError(String mensaje) {
        Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
        Log.e("CustomNombrePersona", mensaje);
    }
}