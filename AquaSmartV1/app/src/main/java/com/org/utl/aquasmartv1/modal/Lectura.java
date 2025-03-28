package com.org.utl.aquasmartv1.modal;

import java.util.Date;

public class Lectura {
    private int idLectura;
    private  int estatus;
    private Double flujo;
    private Double pulsaciones;
    private Double tiempo;
    private Date fecha;
    private Medidor medidor;

    public Lectura() {
    }

    public Lectura(int idLectura, int estatus, Double flujo, Double pulsaciones, Double tiempo, Date fecha, Medidor medidor) {
        this.idLectura = idLectura;
        this.estatus = estatus;
        this.flujo = flujo;
        this.pulsaciones = pulsaciones;
        this.tiempo = tiempo;
        this.fecha = fecha;
        this.medidor = medidor;
    }

    public int getIdLectura() {

        return idLectura;
    }

    public void setIdLectura(int idLectura) {
        this.idLectura = idLectura;
    }

    public int getEstatus() {
        return estatus;
    }

    public void setEstatus(int estatus) {
        this.estatus = estatus;
    }

    public Double getFlujo() {
        return flujo;
    }

    public void setFlujo(Double flujo) {
        this.flujo = flujo;
    }

    public Double getPulsaciones() {
        return pulsaciones;
    }

    public void setPulsaciones(Double pulsaciones) {
        this.pulsaciones = pulsaciones;
    }

    public Double getTiempo() {
        return tiempo;
    }

    public void setTiempo(Double tiempo) {
        this.tiempo = tiempo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Medidor getMedidor() {
        return medidor;
    }

    public void setMedidor(Medidor medidor) {
        this.medidor = medidor;
    }

    @Override
    public String toString() {
        return "Lectura{" +
                "idLectura=" + idLectura +
                ", estatus=" + estatus +
                ", flujo=" + flujo +
                ", pulsaciones=" + pulsaciones +
                ", tiempo=" + tiempo +
                ", fecha=" + fecha +
                ", medidor=" + medidor +
                '}';
    }
}
