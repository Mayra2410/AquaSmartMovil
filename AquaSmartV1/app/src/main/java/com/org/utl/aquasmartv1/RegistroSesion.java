package com.org.utl.aquasmartv1;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.org.utl.aquasmartv1.api.CiudadApiService;
import com.org.utl.aquasmartv1.api.ClienteApiService;
import com.org.utl.aquasmartv1.api.EstadoApiService;
import com.org.utl.aquasmartv1.api.Globals;
import com.org.utl.aquasmartv1.modal.Ciudad;
import com.org.utl.aquasmartv1.modal.Cliente;
import com.org.utl.aquasmartv1.modal.Estado;
import com.org.utl.aquasmartv1.modal.Persona;
import com.org.utl.aquasmartv1.modal.Usuario;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegistroSesion extends AppCompatActivity {
    Spinner spinnerCiudad;
    private pl.droidsonroids.gif.GifImageView imageView;
    private Button btnOpenGallery;
    private TextView tvImagePath;
    private Uri uri = null;
    EditText txtNombrePersona, txtApeMaterno, txtApePaterno, txtEdad,txtEmail, txtTelefono,
            txtUsuario,txtContrasenia,txtRutaFoto;
    Button btnRegistrar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_sesion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar vistas
        txtNombrePersona = findViewById(R.id.txtNombrePersona);
        txtApeMaterno = findViewById(R.id.txtApeMaterno);
        txtApePaterno = findViewById(R.id.txtApePaterno);
        txtEmail = findViewById(R.id.txtEmail);
        txtEdad = findViewById(R.id.txtEdad); // Campo problemático
        txtTelefono = findViewById(R.id.txtTelefono);
        txtUsuario = findViewById(R.id.txtUsuario);
        txtContrasenia = findViewById(R.id.txtContrasenia);
        spinnerCiudad = findViewById(R.id.spinnerCiudad);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        imageView = findViewById(R.id.imageView);
        btnOpenGallery = findViewById(R.id.btnOpenGallery);
        tvImagePath = findViewById(R.id.tvImagePath);

        // Configurar el botón para abrir la galería
        btnOpenGallery.setOnClickListener(v -> AbrirGaleria());
        // Cargar ciudades y estados
        obtenerTodosLasCiudadesEstados();

        // Configurar el botón de registro
        btnRegistrar.setOnClickListener(v -> {
            try {
                // Mover la lógica de conversión y registro aquí
                String edades = txtEdad.getText().toString().trim();
                int edad = edades.isEmpty() ? 0 : Integer.parseInt(edades); // Valor por defecto si está vacío

                Integer rol = 1;
                String nombrePersona = txtNombrePersona.getText().toString();
                String apeMaterno = txtApeMaterno.getText().toString();
                String apePaterno = txtApePaterno.getText().toString();
                String email = txtEmail.getText().toString();
                String telefono = txtTelefono.getText().toString();
                String usuario = txtUsuario.getText().toString();
                String contrasenia = txtContrasenia.getText().toString();
                String foto = (uri != null) ? uri.toString() : "foto.png";

                // Validar que se haya seleccionado una ciudad
                List<Ciudad> ciudades = (List<Ciudad>) spinnerCiudad.getTag();
                if (ciudades == null || spinnerCiudad.getSelectedItemPosition() == -1) {
                    Toast.makeText(this, "Seleccione una ciudad", Toast.LENGTH_SHORT).show();
                    return;                }
                Ciudad ciudadSeleccionada = ciudades.get(spinnerCiudad.getSelectedItemPosition());

                // Crear objetos y enviar a la API
                Usuario u = new Usuario();
                u.setNombre(usuario);
                u.setContrasenia(contrasenia);
                u.setFoto(foto);
                u.setRol(rol);

                Persona p = new Persona();
                p.setNombre(nombrePersona);
                p.setApellidoM(apeMaterno);
                p.setApellidoP(apePaterno);
                p.setEdad(edad);
                p.setEmail(email);
                p.setTelefono(telefono);
                p.setCiudad(ciudadSeleccionada);
                p.setUsuario(u);

                Cliente c = new Cliente();
                c.setPersona(p);

                Gson gson = new Gson();
                String datosCliente = gson.toJson(c);
                insertarCliente(datosCliente);

            } catch (NumberFormatException e) {
                Toast.makeText(this, "La edad debe ser un número válido", Toast.LENGTH_SHORT).show();
            }
            limpiar();
        });
    }

    private void insertarCliente(String datosCliente) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Globals.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ClienteApiService service = retrofit.create(ClienteApiService.class);
        Call<JsonObject> call = service.insertCliente(datosCliente);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Toast.makeText(RegistroSesion.this, "Cliente registrado correctamente", Toast.LENGTH_SHORT).show();
                    // Opcional: Navegar a otra actividad o actualizar la interfaz
                } else {
                    Toast.makeText(RegistroSesion.this, "Error en el registro: " + response.message(), Toast.LENGTH_SHORT).show();
                    System.out.println("Código de error: " + response.code());
                    System.out.println("Mensaje de error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(RegistroSesion.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    //CIUDADESESTADOS
    public void obtenerTodosLasCiudadesEstados() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Globals.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Primero obtén los estados
        EstadoApiService estadoService = retrofit.create(EstadoApiService.class);
        Call<List<Estado>> callEstados = estadoService.obtenerTodosLosEstados();

        callEstados.enqueue(new Callback<List<Estado>>() {
            @Override
            public void onResponse(Call<List<Estado>> call, Response<List<Estado>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Estado> estados = response.body();
                    // Luego obtén las ciudades
                    obtenerCiudadesYCombinarConEstados(estados);
                }
            }

            @Override
            public void onFailure(Call<List<Estado>> call, Throwable t) {
                System.out.println("Error al obtener estados: " + t.getMessage());
            }
        });
    }
    private void obtenerCiudadesYCombinarConEstados(List<Estado> estados) {
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
                    // Combinar ciudades con estados
                    for (Ciudad ciudad : ciudades) {
                        for (Estado estado : estados) {
                            if (ciudad.getIdEstado().getIdEstado() == estado.getIdEstado()) {
                                ciudad.getIdEstado().setNombreEstado(estado.getNombreEstado());
                                break;
                            }
                        }
                    }
                    mostrarCiudadesEstadoEnSpinner(ciudades);
                }
            }

            @Override
            public void onFailure(Call<List<Ciudad>> call, Throwable t) {
                System.out.println("Error al obtener ciudades: " + t.getMessage());
            }
        });
    }
    private void mostrarCiudadesEstadoEnSpinner(List<Ciudad> ciudades) {
        List<String> nombresCiudades = new ArrayList<>();
        for (Ciudad ciudad : ciudades) {
            nombresCiudades.add(ciudad.getNombreCiudad() + " - " + ciudad.getIdEstado().getNombreEstado()); // Solo muestra el nombre de la ciudad
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, nombresCiudades);
        spinnerCiudad.setAdapter(adapter);
        spinnerCiudad.setTag(ciudades);
    }
    private void AbrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galeriaARL.launch(intent);
    }

    private final ActivityResultLauncher<Intent> galeriaARL = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            uri = data.getData();
                            imageView.setImageURI(uri);
                            // Guardar la ruta de la imagen
                            String imagePath = uri.toString();
                            tvImagePath.setText(imagePath);
                        }
                    } else {
                        Toast.makeText(RegistroSesion.this, "Cancelado", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    public void limpiar(){
        txtNombrePersona.setText("");
        txtApePaterno.setText("");
        txtApeMaterno.setText("");
        txtEdad.setText("");
        txtEmail.setText("");
        txtTelefono.setText("");
        txtUsuario.setText("");
        txtContrasenia.setText("");
        tvImagePath.setText("");
    }
}