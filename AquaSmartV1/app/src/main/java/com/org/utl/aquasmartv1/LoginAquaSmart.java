package com.org.utl.aquasmartv1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.gson.JsonObject;
import com.org.utl.aquasmartv1.api.Globals;
import com.org.utl.aquasmartv1.api.LoginApiService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginAquaSmart extends AppCompatActivity {
    TextView txtRegistrar;
    Button btnSesion;
    EditText txtUsuario, txtPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_aqua_smart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        txtUsuario = findViewById(R.id.txtUsuario);
        txtPassword = findViewById(R.id.txtPassword);
        txtRegistrar = findViewById(R.id.txtRegistrar);
        txtRegistrar.setOnClickListener(view -> {
            Registrar();
        });
        btnSesion = findViewById(R.id.btnSesion);
        btnSesion.setOnClickListener(view -> {
            String nombre = txtUsuario.getText().toString().trim();
            String contrasenia = txtPassword.getText().toString().trim();

            if (nombre.isEmpty() || contrasenia.isEmpty()) {
                Toast.makeText(LoginAquaSmart.this, "Por favor, ingresa usuario y contraseña", Toast.LENGTH_SHORT).show();
            } else {
                validarLogin(nombre, contrasenia);
            }
            limpiar();
        });
    }

    private void IniciarSesion(String nombre) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("USERNAME", nombre); // Asegúrate que la clave sea "USERNAME"
        startActivity(intent);
        finish();
    }

    private void Registrar() {
        Intent goRegistroUsuario = new Intent(this, RegistroSesion.class);
        startActivity(goRegistroUsuario);
    }
    // Método para validar el login con el servidor
    public void validarLogin(String user, String password) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Globals.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LoginApiService service = retrofit.create(LoginApiService.class);
        Call<JsonObject> call = service.validarLogin(user, password);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonResponse = response.body();
                    if (jsonResponse.has("status")) {
                        String respuesta = jsonResponse.get("status").getAsString();
                        if (respuesta.equals("fail")) {
                            Toast.makeText(LoginAquaSmart.this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                        } else {
                            // GUARDAR USUARIO EN SHAREDPREFERENCES
                            SharedPreferences sharedPref = getSharedPreferences("login_prefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("username", user);
                            editor.apply();

                            // Verificación
                            Log.d("LoginDebug", "Usuario guardado: " + user);

                            IniciarSesion(user);
                            Toast.makeText(LoginAquaSmart.this, "Login correcto", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }


            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (t instanceof IOException) {
                    Toast.makeText(LoginAquaSmart.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginAquaSmart.this, "Error inesperado: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
                Log.e("LoginActivity", "Error en la llamada: " + t.getMessage());
            }
        });

    }

    public void limpiar(){
        txtUsuario.setText("");
        txtPassword.setText("");
    }
}