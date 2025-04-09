package com.org.utl.aquasmartv1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.org.utl.aquasmartv1.api.CiudadApiService;
import com.org.utl.aquasmartv1.api.MedidorApiService;
import com.org.utl.aquasmartv1.api.PropiedadApiService;
import com.org.utl.aquasmartv1.api.ClienteApiService;
import com.org.utl.aquasmartv1.modal.Ciudad;
import com.org.utl.aquasmartv1.modal.Medidor;
import com.org.utl.aquasmartv1.modal.Cliente;
import com.org.utl.aquasmartv1.api.Globals;
import com.org.utl.aquasmartv1.modal.Persona;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroPropiedad extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private static final String TAG = "RegistroPropiedad";

    private Spinner spinnerCiudad, spinnerMedidor, spinnerCliente;
    private TextView tvImagePath;
    private ImageView imgPropiedadPreview;
    private Uri imageUri;
    private List<Ciudad> listaCiudades = new ArrayList<>();
    private List<Medidor> listaMedidores = new ArrayList<>();
    private List<Cliente> listaClientes = new ArrayList<>();
    private PropiedadApiService propiedadApiService;
    private ClienteApiService clienteApiService;
    private String fotoBase64 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_registro_propiedad);
        Log.d(TAG, "Activity creada");

        // Inicializar Retrofit para Propiedad
        Retrofit retrofitPropiedad = new Retrofit.Builder()
                .baseUrl(Globals.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        propiedadApiService = retrofitPropiedad.create(PropiedadApiService.class);

        // Inicializar Retrofit para Cliente
        Retrofit retrofitCliente = new Retrofit.Builder()
                .baseUrl(Globals.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        clienteApiService = retrofitCliente.create(ClienteApiService.class);

        // Inicializar vistas
        initViews();

        // Cargar datos
        cargarCiudades();
        cargarMedidores();
        cargarClientes();
    }

    private void initViews() {
        spinnerCiudad = findViewById(R.id.spinnerCiudad);
        spinnerMedidor = findViewById(R.id.spinnerMedidor);
        spinnerCliente = findViewById(R.id.spinnerCliente);
        tvImagePath = findViewById(R.id.tvImagePath);
        imgPropiedadPreview = findViewById(R.id.imgPropiedad);

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

                validarYRegistrarPropiedad(
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

    // Método para convertir Base64 a Bitmap
    private Bitmap base64ToBitmap(String base64String) {
        try {
            byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e) {
            Log.e(TAG, "Error al convertir Base64 a Bitmap", e);
            return null;
        }
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
                    nombresCiudades.add("Seleccione una ciudad");

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
                    nombresMedidores.add("Seleccione un medidor");

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

    private void cargarClientes() {
        Log.d(TAG, "Cargando clientes...");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Globals.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(createLenientGson()))
                .build();

        ClienteApiService service = retrofit.create(ClienteApiService.class);
        Call<List<Cliente>> call = service.obtenerTodosClientes();

        call.enqueue(new Callback<List<Cliente>>() {
            @Override
            public void onResponse(Call<List<Cliente>> call, Response<List<Cliente>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaClientes = response.body();
                    Log.d(TAG, "Clientes cargados: " + listaClientes.size());

                    List<String> nombresClientes = new ArrayList<>();
                    nombresClientes.add("Seleccione un cliente");

                    for (Cliente cliente : listaClientes) {
                        Persona persona = cliente.getPersona();
                        if (persona != null) {
                            String nombreCompleto = persona.getNombre() + " " + persona.getApellidoP();
                            nombresClientes.add(nombreCompleto);
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            RegistroPropiedad.this,
                            android.R.layout.simple_spinner_item,
                            nombresClientes
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCliente.setAdapter(adapter);
                } else {
                    String error = "Error al cargar clientes: " + response.code();
                    Toast.makeText(RegistroPropiedad.this, error, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, error);
                }
            }

            @Override
            public void onFailure(Call<List<Cliente>> call, Throwable t) {
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

            try {
                fotoBase64 = convertirImagenABase64(imageUri);
                if (fotoBase64 != null && !fotoBase64.isEmpty()) {
                    Bitmap bitmap = base64ToBitmap(fotoBase64);
                    Glide.with(this)
                            .load(bitmap)
                            .into(imgPropiedadPreview);
                    tvImagePath.setText("Imagen seleccionada");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error al cargar imagen", e);
                tvImagePath.setText("Error al cargar imagen");
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String convertirImagenABase64(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

            // Redimensionar la imagen si es muy grande
            int maxWidth = 1024;
            int maxHeight = 1024;
            if (bitmap.getWidth() > maxWidth || bitmap.getHeight() > maxHeight) {
                float aspectRatio = (float) bitmap.getWidth() / (float) bitmap.getHeight();
                if (aspectRatio > 1) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, maxWidth, (int) (maxWidth / aspectRatio), true);
                } else {
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int) (maxHeight * aspectRatio), maxHeight, true);
                }
            }

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

    private void validarYRegistrarPropiedad(String numExt, String numInt, String calle,
                                            String colonia, String codigoP,
                                            double latitud, double longitud) {
        if (numExt.trim().isEmpty() || calle.trim().isEmpty() || colonia.trim().isEmpty() || codigoP.trim().isEmpty()) {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        int ciudadPos = spinnerCiudad.getSelectedItemPosition();
        int medidorPos = spinnerMedidor.getSelectedItemPosition();
        int clientePos = spinnerCliente.getSelectedItemPosition();

        if (ciudadPos <= 0) {
            Toast.makeText(this, "Seleccione una ciudad válida", Toast.LENGTH_SHORT).show();
            spinnerCiudad.requestFocus();
            return;
        }

        if (medidorPos <= 0) {
            Toast.makeText(this, "Seleccione un medidor válido", Toast.LENGTH_SHORT).show();
            spinnerMedidor.requestFocus();
            return;
        }

        if (clientePos <= 0) {
            Toast.makeText(this, "Seleccione un cliente válido", Toast.LENGTH_SHORT).show();
            spinnerCliente.requestFocus();
            return;
        }

        try {
            Integer.parseInt(codigoP.trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Código postal debe ser numérico", Toast.LENGTH_SHORT).show();
            return;
        }

        Ciudad ciudadSeleccionada = listaCiudades.get(ciudadPos - 1);
        Medidor medidorSeleccionado = listaMedidores.get(medidorPos - 1);
        Cliente clienteSeleccionado = listaClientes.get(clientePos - 1);

        registrarPropiedadCompleta(
                numExt,
                numInt,
                calle,
                colonia,
                codigoP,
                latitud,
                longitud,
                clienteSeleccionado.getIdCliente(),
                ciudadSeleccionada.getIdCiudad(),
                medidorSeleccionado.getIdMedidor()
        );
    }

    private void registrarPropiedadCompleta(String numExt, String numInt, String calle,
                                            String colonia, String codigoP,
                                            double latitud, double longitud,
                                            int idCliente, int idCiudad, int idMedidor) {
        try {
            if (numExt == null || numExt.isEmpty() || calle == null || calle.isEmpty() ||
                    colonia == null || colonia.isEmpty() || codigoP == null || codigoP.isEmpty()) {
                Toast.makeText(this, "Complete todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convertir imagen a Base64 si existe
            if (imageUri != null) {
                fotoBase64 = convertirImagenABase64(imageUri);
            }

            // Crear el JSON de la propiedad
            JsonObject propiedadJson = new JsonObject();
            propiedadJson.addProperty("numExt", numExt);
            propiedadJson.addProperty("calle", calle);
            propiedadJson.addProperty("colonia", colonia);
            propiedadJson.addProperty("latitud", latitud);
            propiedadJson.addProperty("longitud", longitud);
            propiedadJson.addProperty("codigoP", Integer.parseInt(codigoP));
            propiedadJson.addProperty("estatus", 1);

            // Campos opcionales
            if (!numInt.isEmpty()) {
                propiedadJson.addProperty("numInt", numInt);
            }

            if (fotoBase64 != null && !fotoBase64.isEmpty()) {
                propiedadJson.addProperty("foto", "data:image/jpeg;base64," + fotoBase64);
            }

            // Objetos relacionados
            JsonObject clienteJson = new JsonObject();
            clienteJson.addProperty("idCliente", idCliente);
            propiedadJson.add("cliente", clienteJson);

            JsonObject ciudadJson = new JsonObject();
            ciudadJson.addProperty("idCiudad", idCiudad);
            propiedadJson.add("ciudad", ciudadJson);

            JsonObject medidorJson = new JsonObject();
            medidorJson.addProperty("idMedidor", idMedidor);
            propiedadJson.add("medidor", medidorJson);

            // Convertir a JSON string
            String jsonString = new Gson().toJson(propiedadJson);
            Log.d(TAG, "JSON a enviar: " + jsonString);

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Registrando propiedad...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            Call<JsonObject> call = propiedadApiService.insertarPropiedad(jsonString);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful() && response.body() != null) {
                        mostrarNotificacionExito();
                        prepararResultadoParaActualizacion();
                        finish();
                    } else {
                        manejarErrorRespuesta(response);
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    progressDialog.dismiss();
                    manejarErrorConexion(t);
                }
            });
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Código postal debe ser numérico", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Error en formato numérico", e);
        } catch (Exception e) {
            manejarErrorInesperado(e);
        }
    }

    private void mostrarNotificacionExito() {
        Toast.makeText(RegistroPropiedad.this,
                "Propiedad registrada exitosamente",
                Toast.LENGTH_SHORT).show();
    }

    private void prepararResultadoParaActualizacion() {
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        String nombreUsuario = sharedPreferences.getString("username", "");

        Intent resultIntent = new Intent();
        resultIntent.putExtra("refresh", true);
        resultIntent.putExtra("username", nombreUsuario);
        setResult(RESULT_OK, resultIntent);
    }

    private void manejarErrorRespuesta(Response<JsonObject> response) {
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Error desconocido";
            Log.e(TAG, "Error en la respuesta: " + errorBody);
            Toast.makeText(RegistroPropiedad.this,
                    "Error en el servidor: " + errorBody,
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e(TAG, "Error al procesar errorBody", e);
            Toast.makeText(RegistroPropiedad.this,
                    "Error al procesar la respuesta del servidor",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void manejarErrorConexion(Throwable t) {
        Log.e(TAG, "Error de conexión", t);
        Toast.makeText(RegistroPropiedad.this,
                "Error de conexión: " + t.getMessage(),
                Toast.LENGTH_SHORT).show();
    }

    private void manejarErrorInesperado(Exception e) {
        Log.e(TAG, "Error inesperado", e);
        Toast.makeText(this,
                "Error inesperado: " + e.getMessage(),
                Toast.LENGTH_SHORT).show();
    }

    private Gson createLenientGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    @Override
                    public Date deserialize(JsonElement json, Type typeOfT,
                                            JsonDeserializationContext context) {
                        return null;
                    }
                })
                .create();
    }
}