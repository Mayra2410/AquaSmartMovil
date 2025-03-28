package com.org.utl.aquasmartv1;

import com.org.utl.aquasmartv1.modal.Ciudad;
import com.org.utl.aquasmartv1.modal.Estado;

import java.util.List;

public class ApiResponse {
    private List<Ciudad> Ciudad;
    private  List<Estado> Estado;

    public ApiResponse() {
    }

    public ApiResponse(List<com.org.utl.aquasmartv1.modal.Ciudad> ciudad, List<com.org.utl.aquasmartv1.modal.Estado> estado) {
        Ciudad = ciudad;
        Estado = estado;
    }

    public List<com.org.utl.aquasmartv1.modal.Ciudad> getCiudad() {
        return Ciudad;
    }

    public void setCiudad(List<com.org.utl.aquasmartv1.modal.Ciudad> ciudad) {
        Ciudad = ciudad;
    }

    public List<com.org.utl.aquasmartv1.modal.Estado> getEstado() {
        return Estado;
    }

    public void setEstado(List<com.org.utl.aquasmartv1.modal.Estado> estado) {
        Estado = estado;
    }
}
