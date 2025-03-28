package com.org.utl.aquasmartv1.modal;

public class Estado {
    private int idEstado;
    private String nombre;

    public Estado() {
    }

    public Estado(int idEstado, String nombre) {
        this.idEstado = idEstado;
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

    @Override
    public String toString() {
        return "Estado{" +
                "idEstado=" + idEstado +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
