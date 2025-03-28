package com.org.utl.aquasmartv1;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CustomNombrePersona extends Dialog {

    private String selectedText;
    private EditText selectedTextEditText;
    private Button okButton,closeButton;
    private OnDialogResultListener listener;

    public interface OnDialogResultListener {
        void onDialogResult(String result);
    }

    public CustomNombrePersona(Context context, String selectedText, OnDialogResultListener listener) {
        super(context);
        this.selectedText = selectedText;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_custom_nombre_persona);

        TextView titleTextView = findViewById(R.id.titleTextView);
        selectedTextEditText = findViewById(R.id.txtNombrePersona);
        okButton = findViewById(R.id.okButton);
        closeButton = findViewById(R.id.okButton);
        // Mostrar el texto seleccionado
        if (selectedText != null) {
            selectedTextEditText.setText(selectedText);
        }

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onDialogResult(selectedTextEditText.getText().toString());
                }
                dismiss();
            }
        });
    }
}