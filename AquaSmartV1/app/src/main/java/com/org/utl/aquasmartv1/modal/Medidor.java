package com.org.utl.aquasmartv1.modal;

public class Medidor {
    private int idMedidor;
    private String nombre;
    private String modelo;
    private  int cantidad;
    private int estatus;
    private  Double precio;

    public Medidor() {
    }

    public Medidor(int idMedidor, String nombre, String modelo, int cantidad, int estatus, Double precio) {
        this.idMedidor = idMedidor;
        this.nombre = nombre;
        this.modelo = modelo;
        this.cantidad = cantidad;
        this.estatus = estatus;
        this.precio = precio;
    }

    public int getIdMedidor() {
        return idMedidor;
    }

    public void setIdMedidor(int idMedidor) {
        this.idMedidor = idMedidor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getEstatus() {
        return estatus;
    }

    public void setEstatus(int estatus) {
        this.estatus = estatus;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "Medidor{" +
                "idMedidor=" + idMedidor +
                ", nombre='" + nombre + '\'' +
                ", modelo='" + modelo + '\'' +
                ", cantidad=" + cantidad +
                ", estatus=" + estatus +
                ", precio=" + precio +
                '}';
    }
}
