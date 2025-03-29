package com.org.utl.aquasmartv1;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class CustomNombrePersona extends Dialog {

    private String nombre,apellidoP,apellidoM,edad,telefono,email;
    private EditText editNombre,editApellidoP,editApellidoM,editEdad,editCorreo,editTelefono;
    private Button btnOk;
    private Button btnCancel;
    private OnDialogResultListener listener;

    public interface OnDialogResultListener {
        void onDialogResult(
                String nombre,
                String apellidoP,
                String apellidoM,
                String edad,
                String email,
                String telefono  // Añade este parámetro si es necesario
        );
    }

    public CustomNombrePersona(
            Context context,
            String nombre,
            String apellidoP,
            String apellidoM,
            String edad,
            String email,
            String telefono,  // Añade este parámetro
            OnDialogResultListener listener
    ) {
        super(context);
        this.nombre = nombre;
        this.apellidoP = apellidoP;
        this.apellidoM = apellidoM;
        this.edad = edad;
        this.email = email;
        this.telefono = telefono;  // Inicializa el campo
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_custom_nombre_persona);

        editNombre = findViewById(R.id.editNombre);
        editApellidoP = findViewById(R.id.editApellidoP);
        editApellidoM = findViewById(R.id.editApellidoM);
        editEdad = findViewById(R.id.editEdad);
        editTelefono = findViewById(R.id.editTelefono);
        editCorreo = findViewById(R.id.editCorreo);
        btnOk = findViewById(R.id.btnOk);
        btnCancel = findViewById(R.id.btnCancel);

        // Setear valores actuales
        editNombre.setText(nombre);
        editApellidoP.setText(apellidoP);
        editApellidoM.setText(apellidoM);
        editEdad.setText(edad);
        editCorreo.setText(email);
        editTelefono.setText(telefono);

        btnOk.setOnClickListener(v -> {
            String nombre = editNombre.getText().toString().trim();
            if (nombre.isEmpty()) {
                editNombre.setError("El nombre es obligatorio");
                return;
            }
            // Validar otros campos si es necesario...

            if (listener != null) {
                listener.onDialogResult(
                        nombre,
                        editApellidoP.getText().toString(),
                        editApellidoM.getText().toString(),
                        editEdad.getText().toString(),
                        editCorreo.getText().toString(),
                        editTelefono.getText().toString()
                );
            }
            dismiss();
        });

        btnCancel.setOnClickListener(v -> dismiss());
    }
}