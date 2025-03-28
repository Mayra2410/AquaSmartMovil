package com.org.utl.aquasmartv1.modal;

public class Tarjeta {
    private String numTarjeta;
    private String nombreTitular;
    private String mes;
    private Cliente cliente;
    private int estatus;
    private String cvv;
    private String anio;

    public Tarjeta() {
    }

    public Tarjeta(String numTarjeta, String nombreTitular, String mes, Cliente cliente, int estatus, String cvv, String anio) {
        this.numTarjeta = numTarjeta;
        this.nombreTitular = nombreTitular;
        this.mes = mes;
        this.cliente = cliente;
        this.estatus = estatus;
        this.cvv = cvv;
        this.anio = anio;
    }

    public String getNumTarjeta() {
        return numTarjeta;
    }

    public void setNumTarjeta(String numTarjeta) {
        this.numTarjeta = numTarjeta;
    }

    public String getNombreTitular() {
        return nombreTitular;
    }

    public void setNombreTitular(String nombreTitular) {
        this.nombreTitular = nombreTitular;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public int getEstatus() {
        return estatus;
    }

    public void setEstatus(int estatus) {
        this.estatus = estatus;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getAnio() {
        return anio;
    }

    public void setAnio(String anio) {
        this.anio = anio;
    }

    @Override
    public String toString() {
        return "Tarjeta{" +
                "numTarjeta='" + numTarjeta + '\'' +
                ", nombreTitular='" + nombreTitular + '\'' +
                ", mes='" + mes + '\'' +
                ", cliente=" + cliente +
                ", estatus=" + estatus +
                ", cvv='" + cvv + '\'' +
                ", anio='" + anio + '\'' +
                '}';
    }
}
