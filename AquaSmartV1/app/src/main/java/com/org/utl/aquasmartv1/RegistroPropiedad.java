package com.org.utl.aquasmartv1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
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
import com.org.utl.aquasmartv1.api.Globals;

import org.json.JSONException;
import org.json.JSONObject;

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
    private static final String TAG = "RegistroPropiedad";

    private Spinner spinnerCiudad, spinnerMedidor;
    private TextView tvImagePath;
    private Uri imageUri;
    private List<Ciudad> listaCiudades = new ArrayList<>();
    private List<Medidor> listaMedidores = new ArrayList<>();
    private PropiedadApiService propiedadApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_registro_propiedad);
        Log.d(TAG, "Activity creada");

        // Inicializar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Globals.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        propiedadApiService = retrofit.create(PropiedadApiService.class);

        // Inicializar vistas
        initViews();

        // Cargar datos
        cargarCiudades();
        cargarMedidores();
    }

    private void initViews() {
        spinnerCiudad = findViewById(R.id.spinnerCiudad);
        spinnerMedidor = findViewById(R.id.spinnerMedidor);
        tvImagePath = findViewById(R.id.tvImagePath);

        Button btnSeleccionarFoto = findViewById(R.id.btnSeleccionarFoto);
        Button btnRegistrar = findViewById(R.id.btnRegistrarPropiedad);
        Button btnCancelar = findViewById(R.id.btnCancelarPropiedad);

        EditText txtNumExt = findViewById(R.id.txtNumExt);
        EditText txtNumInt = findViewById(R.id.txtNumInt);
        EditText txtCalle = findViewById(R.id.txtCalle);
        EditText txtColonia = findViewById(R.id.txtColonia);
        EditText txtCodigoP = findViewById(R.id.txtCodigoPostal);
        EditText txtLatitud = findViewById(R.id.txtLatitud);
        EditText txtLongitud = findViewById(R.id.txtLongitud);

        btnSeleccionarFoto.setOnClickListener(v -> {
            Log.d(TAG, "Botón seleccionar foto clickeado");
            seleccionarFoto();
        });

        btnRegistrar.setOnClickListener(v -> {
            Log.d(TAG, "Botón registrar clickeado");
            try {
                String latitudStr = txtLatitud.getText().toString();
                String longitudStr = txtLongitud.getText().toString();

                double latitud = latitudStr.isEmpty() ? 0.0 : Double.parseDouble(latitudStr);
                double longitud = longitudStr.isEmpty() ? 0.0 : Double.parseDouble(longitudStr);

                registrarPropiedad(
                        txtNumExt.getText().toString(),
                        txtNumInt.getText().toString(),
                        txtCalle.getText().toString(),
                        txtColonia.getText().toString(),
                        txtCodigoP.getText().toString(),
                        latitud,
                        longitud
                );
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Formato incorrecto en coordenadas", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error en formato de coordenadas", e);
            }
        });

        btnCancelar.setOnClickListener(v -> {
            Log.d(TAG, "Botón cancelar clickeado");
            finish();
        });
    }

    private void cargarCiudades() {
        Log.d(TAG, "Cargando ciudades...");
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
                    Log.d(TAG, "Ciudades cargadas: " + listaCiudades.size());

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
                    String error = "Error al cargar ciudades: " + response.code();
                    Toast.makeText(RegistroPropiedad.this, error, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, error);
                }
            }

            @Override
            public void onFailure(Call<List<Ciudad>> call, Throwable t) {
                String error = "Error de conexión: " + t.getMessage();
                Toast.makeText(RegistroPropiedad.this, error, Toast.LENGTH_SHORT).show();
                Log.e(TAG, error, t);
            }
        });
    }

    private void cargarMedidores() {
        Log.d(TAG, "Cargando medidores...");
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
                    Log.d(TAG, "Medidores cargados: " + listaMedidores.size());

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
                    String error = "Error al cargar medidores: " + response.code();
                    Toast.makeText(RegistroPropiedad.this, error, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, error);
                }
            }

            @Override
            public void onFailure(Call<List<Medidor>> call, Throwable t) {
                String error = "Error de conexión: " + t.getMessage();
                Toast.makeText(RegistroPropiedad.this, error, Toast.LENGTH_SHORT).show();
                Log.e(TAG, error, t);
            }
        });
    }

    private void seleccionarFoto() {
        Log.d(TAG, "Seleccionando foto...");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Log.d(TAG, "Imagen seleccionada: " + imageUri);
            tvImagePath.setText(imageUri.getLastPathSegment());
        }
    }

    private String convertirImagenABase64(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (IOException e) {
            Log.e(TAG, "Error al convertir imagen", e);
            Toast.makeText(this, "Error al procesar la imagen", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void registrarPropiedad(String numExt, String numInt, String calle,
                                    String colonia, String codigoP, double latitud, double longitud) {
        Log.d(TAG, "Registrando propiedad...");

        // Validaciones
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

            // Crear objeto JSON como lo espera el backend
            JSONObject propiedadJson = new JSONObject();
            propiedadJson.put("numExt", numExt);
            propiedadJson.put("numInt", numInt.isEmpty() ? JSONObject.NULL : numInt);
            propiedadJson.put("calle", calle);
            propiedadJson.put("colonia", colonia);
            propiedadJson.put("latitud", latitud);
            propiedadJson.put("longitud", longitud);
            propiedadJson.put("codigoP", codigoP);
            propiedadJson.put("foto", fotoBase64.isEmpty() ? JSONObject.NULL : fotoBase64);
            propiedadJson.put("estatus", 1);
            propiedadJson.put("ciudad", new JSONObject().put("idCiudad", ciudadSeleccionada.getIdCiudad()));
            propiedadJson.put("medidor", new JSONObject().put("idMedidor", medidorSeleccionado.getIdMedidor()));
            propiedadJson.put("cliente", new JSONObject().put("idCliente", 1)); // Ajusta este ID según tu lógica

            Log.d(TAG, "JSON a enviar: " + propiedadJson.toString());

            // Llamar al servicio
            Call<Void> call = propiedadApiService.insertarPropiedad(propiedadJson.toString());
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RegistroPropiedad.this, "Propiedad registrada exitosamente", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String errorMsg = "Error del servidor: " + response.code();
                        try {
                            if (response.errorBody() != null) {
                                errorMsg += " - " + response.errorBody().string();
                            }
                        } catch (IOException e) {
                            Log.e(TAG, "Error al leer errorBody", e);
                        }
                        Toast.makeText(RegistroPropiedad.this, errorMsg, Toast.LENGTH_LONG).show();
                        Log.e(TAG, errorMsg);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    String errorMsg = "Error de conexión: " + t.getMessage();
                    Toast.makeText(RegistroPropiedad.this, errorMsg, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, errorMsg, t);
                }
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "El código postal debe ser numérico", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error en formato de código postal", e);
        } catch (JSONException e) {
            Toast.makeText(this, "Error al crear JSON", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error JSON", e);
        } catch (Exception e) {
            Toast.makeText(this, "Error inesperado: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error inesperado", e);
        }
    }

}