package com.org.utl.aquasmartv1.modal;

public class Ciudad {
    private int idCiudad;
    private String nombreCiudad;
    private Estado idEstado;

    public Ciudad() {
    }

    public Ciudad(int idCiudad, String nombreCiudad, Estado idEstado) {
        this.idCiudad = idCiudad;
        this.nombreCiudad = nombreCiudad;
        this.idEstado = idEstado;
    }

    public int getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(int idCiudad) {
        this.idCiudad = idCiudad;
    }

    public String getNombreCiudad() {
        return nombreCiudad;
    }

    public void setNombreCiudad(String nombreCiudad) {
        this.nombreCiudad = nombreCiudad;
    }

    public Estado getIdEstado() {
        return idEstado;
    }

    public void setIdEstado(Estado idEstado) {
        this.idEstado = idEstado;
    }

    @Override
    public String toString() {
        return "Ciudad{" +
                "idCiudad=" + idCiudad +
                ", nombreCiudad='" + nombreCiudad + '\'' +
                ", idEstado=" + idEstado +
                '}';
    }
}
