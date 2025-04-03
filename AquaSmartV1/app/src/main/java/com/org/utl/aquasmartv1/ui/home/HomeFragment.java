package com.org.utl.aquasmartv1.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.org.utl.aquasmartv1.CustomNombrePersona;
import com.org.utl.aquasmartv1.R;
import com.org.utl.aquasmartv1.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Configurar los listeners para los TextViews
        setupTextViewListeners(root);

        return root;
    }

    private void setupTextViewListeners(View root) {
        // Obtener referencias a los TextViews
        TextView txtNombrePersona = root.findViewById(R.id.txtNumeroExterior);
        TextView txtApePaterno = root.findViewById(R.id.txtCalleRegistro);
        TextView txtApeMaterno = root.findViewById(R.id.txtNumeroInterior);
        TextView txtEdad = root.findViewById(R.id.txtEdad);
        TextView txtEmail = root.findViewById(R.id.txtEmail);
        TextView txtTelefono = root.findViewById(R.id.txtTelefono);
        TextView txtCiudad = root.findViewById(R.id.txtCiudad);
        TextView txtUsuario = root.findViewById(R.id.txtUsuario);
        TextView txtContrasenia = root.findViewById(R.id.txtContrasenia);

        // Configurar el click listener para cada TextView
        setTextViewClickListener(txtNombrePersona);
        setTextViewClickListener(txtApePaterno);
        setTextViewClickListener(txtApeMaterno);
        setTextViewClickListener(txtEdad);
        setTextViewClickListener(txtEmail);
        setTextViewClickListener(txtTelefono);
        setTextViewClickListener(txtCiudad);
        setTextViewClickListener(txtUsuario);
        setTextViewClickListener(txtContrasenia);
    }

    private void setTextViewClickListener(TextView textView) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog(textView.getText().toString(), textView);
            }
        });
    }

    private void showCustomDialog(String selectedText, final TextView targetTextView) {
        if (getContext() == null) return;

        CustomNombrePersona dialog = new CustomNombrePersona(getContext(), selectedText, new CustomNombrePersona.OnDialogResultListener() {
            @Override
            public void onDialogResult(String result) {
                // Actualizar el TextView con el nuevo texto
                targetTextView.setText(result);
                Toast.makeText(getContext(), "Texto actualizado", Toast.LENGTH_SHORT).show();
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