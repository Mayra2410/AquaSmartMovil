package com.org.utl.aquasmartv1.modal;

import java.util.Date;

public class DetalleServicio {
    private int idDetalle;
    private String descripcion;
    private Date fecha;
    private Cliente cliente;
    private  Lectura lectura;
    private  Servicio servicio;
    private Ticket ticket;

    public DetalleServicio() {
    }

    public DetalleServicio(int idDetalle, String descripcion, Date fecha, Cliente cliente, Lectura lectura, Servicio servicio, Ticket ticket) {
        this.idDetalle = idDetalle;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.cliente = cliente;
        this.lectura = lectura;
        this.servicio = servicio;
        this.ticket = ticket;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Lectura getLectura() {
        return lectura;
    }

    public void setLectura(Lectura lectura) {
        this.lectura = lectura;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    @Override
    public String toString() {
        return "DetalleServicio{" +
                "idDetalle=" + idDetalle +
                ", descripcion='" + descripcion + '\'' +
                ", fecha=" + fecha +
                ", cliente=" + cliente +
                ", lectura=" + lectura +
                ", servicio=" + servicio +
                ", ticket=" + ticket +
                '}';
    }
}
