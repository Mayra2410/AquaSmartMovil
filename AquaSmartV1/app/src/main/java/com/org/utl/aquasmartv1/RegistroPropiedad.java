package com.org.utl.aquasmartv1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.org.utl.aquasmartv1.api.CiudadApiService;
import com.org.utl.aquasmartv1.api.MedidorApiService;
import com.org.utl.aquasmartv1.api.PropiedadApiService;
import com.org.utl.aquasmartv1.modal.Ciudad;
import com.org.utl.aquasmartv1.modal.Medidor;
import com.org.utl.aquasmartv1.modal.Propiedad;
import com.org.utl.aquasmartv1.api.Globals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroPropiedad extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;

    private Spinner spinnerCiudad, spinnerMedidor;
    private TextView tvImagePath;
    private Uri imageUri;
    private List<Ciudad> listaCiudades = new ArrayList<>();
    private List<Medidor> listaMedidores = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_registro_propiedad);

        // Inicializar vistas
        spinnerCiudad = findViewById(R.id.spinnerCiudad);
        spinnerMedidor = findViewById(R.id.spinnerMedidor);
        tvImagePath = findViewById(R.id.tvImagePath);
        Button btnSeleccionarFoto = findViewById(R.id.btnSeleccionarFoto);
        Button btnRegistrar = findViewById(R.id.btnRegistrar);
        EditText txtNumExt = findViewById(R.id.txtNumExt);
        EditText txtNumInt = findViewById(R.id.txtNumInt);
        EditText txtCalle = findViewById(R.id.txtCalle);
        EditText txtColonia = findViewById(R.id.txtColonia);
        EditText txtCodigoP = findViewById(R.id.txtCodigoPostal);

        // Cargar datos en los spinners
        cargarCiudades();
        cargarMedidores();

        // Configurar listeners
        btnSeleccionarFoto.setOnClickListener(v -> seleccionarFoto());
        btnRegistrar.setOnClickListener(v -> registrarPropiedad(
                txtNumExt.getText().toString(),
                txtNumInt.getText().toString(),
                txtCalle.getText().toString(),
                txtColonia.getText().toString(),
                txtCodigoP.getText().toString()
        ));
    }

    private void cargarCiudades() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Globals.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CiudadApiService service = retrofit.create(CiudadApiService.class);
        Call<List<Ciudad>> call = service.obtenerTodosLasCiudadesEstados();

        call.enqueue(new Callback<List<Ciudad>>() {
            @Override
            public void onResponse(Call<List<Ciudad>> call, Response<List<Ciudad>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaCiudades = response.body();
                    List<String> nombresCiudades = new ArrayList<>();
                    for (Ciudad ciudad : listaCiudades) {
                        nombresCiudades.add(ciudad.getNombre());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            RegistroPropiedad.this,
                            android.R.layout.simple_spinner_item,
                            nombresCiudades
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCiudad.setAdapter(adapter);
                } else {
                    Toast.makeText(RegistroPropiedad.this, "Error al cargar ciudades", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Ciudad>> call, Throwable t) {
                Toast.makeText(RegistroPropiedad.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarMedidores() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Globals.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MedidorApiService service = retrofit.create(MedidorApiService.class);
        Call<List<Medidor>> call = service.obtenerTodosLosMedidores();

        call.enqueue(new Callback<List<Medidor>>() {
            @Override
            public void onResponse(Call<List<Medidor>> call, Response<List<Medidor>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaMedidores = response.body();
                    List<String> nombresMedidores = new ArrayList<>();
                    for (Medidor medidor : listaMedidores) {
                        nombresMedidores.add(medidor.getNombre());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            RegistroPropiedad.this,
                            android.R.layout.simple_spinner_item,
                            nombresMedidores
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerMedidor.setAdapter(adapter);
                } else {
                    Toast.makeText(RegistroPropiedad.this, "Error al cargar medidores", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Medidor>> call, Throwable t) {
                Toast.makeText(RegistroPropiedad.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void seleccionarFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            tvImagePath.setText(imageUri.toString());
        }
    }

    private String convertirImagenABase64(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void registrarPropiedad(String numExt, String numInt, String calle,
                                    String colonia, String codigoP) {
        // Validaciones previas (se mantienen igual)
        if (numExt.isEmpty() || calle.isEmpty() || colonia.isEmpty() || codigoP.isEmpty()) {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerCiudad.getSelectedItemPosition() < 0 || spinnerMedidor.getSelectedItemPosition() < 0) {
            Toast.makeText(this, "Seleccione una ciudad y un medidor", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Ciudad ciudadSeleccionada = listaCiudades.get(spinnerCiudad.getSelectedItemPosition());
            Medidor medidorSeleccionado = listaMedidores.get(spinnerMedidor.getSelectedItemPosition());
            String fotoBase64 = imageUri != null ? convertirImagenABase64(imageUri) : "";

            // Crear objeto Propiedad
            Propiedad propiedad = new Propiedad();
            propiedad.setNumExt(numExt);
            propiedad.setNumInt(numInt.isEmpty() ? null : numInt);
            propiedad.setCalle(calle);
            propiedad.setColonia(colonia);
            propiedad.setCodigoP(Integer.parseInt(codigoP));
            propiedad.setFoto(fotoBase64);
            propiedad.setCiudad(ciudadSeleccionada);
            propiedad.setMedidor(medidorSeleccionado);
            propiedad.setEstatus(1);
            propiedad.setLatitud(0.0);
            propiedad.setLongitud(0.0);

            // Usar el nuevo método getApiService
            PropiedadApiService service = Globals.getApiService(PropiedadApiService.class);

            // Usar la versión con @Body que es más limpia
            Call<Propiedad> call = service.insertarPropiedad(propiedad);

            call.enqueue(new Callback<Propiedad>() {
                @Override
                public void onResponse(Call<Propiedad> call, Response<Propiedad> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Propiedad propiedadRegistrada = response.body();
                        Toast.makeText(RegistroPropiedad.this,
                                "Propiedad registrada exitosamente. ID: " + propiedadRegistrada.getIdPropiedad(),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String errorMsg = "Error del servidor: " + response.code();
                        try {
                            if (response.errorBody() != null) {
                                errorMsg += " - " + response.errorBody().string();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(RegistroPropiedad.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<Propiedad> call, Throwable t) {
                    Toast.makeText(RegistroPropiedad.this,
                            "Error de conexión: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "El código postal debe ser numérico", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error inesperado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}