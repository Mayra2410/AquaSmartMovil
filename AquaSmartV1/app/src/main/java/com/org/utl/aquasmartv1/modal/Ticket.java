package com.org.utl.aquasmartv1.modal;

import java.util.Date;

public class Ticket {
    private int idTicket;
    private Date fecha;
    private Double total;
    private Double subtotal;
    private Cliente cliente;
    private Empleado empleado;
    private Tarjeta numTarjeta;

    public Ticket() {
    }

    public Ticket(int idTicket, Date fecha, Double total, Double subtotal, Cliente cliente, Empleado empleado, Tarjeta numTarjeta) {
        this.idTicket = idTicket;
        this.fecha = fecha;
        this.total = total;
        this.subtotal = subtotal;
        this.cliente = cliente;
        this.empleado = empleado;
        this.numTarjeta = numTarjeta;
    }

    public int getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(int idTicket) {
        this.idTicket = idTicket;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public Tarjeta getNumTarjeta() {
        return numTarjeta;
    }

    public void setNumTarjeta(Tarjeta numTarjeta) {
        this.numTarjeta = numTarjeta;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "idTicket=" + idTicket +
                ", fecha=" + fecha +
                ", total=" + total +
                ", subtotal=" + subtotal +
                ", cliente=" + cliente +
                ", empleado=" + empleado +
                ", numTarjeta=" + numTarjeta +
                '}';
    }
}
