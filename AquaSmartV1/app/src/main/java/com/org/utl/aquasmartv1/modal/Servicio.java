package com.org.utl.aquasmartv1.modal;

public class Servicio {
    private int idServicio;
    private int estatus;
    private Propiedad propiedad;
    private Cliente cliente;
    private Ciudad ciudad;
    private Categoria categoria;

    public Servicio() {
    }

    public Servicio(int idServicio, int estatus, Propiedad propiedad, Cliente cliente, Ciudad ciudad, Categoria categoria) {
        this.idServicio = idServicio;
        this.estatus = estatus;
        this.propiedad = propiedad;
        this.cliente = cliente;
        this.ciudad = ciudad;
        this.categoria = categoria;
    }

    public int getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }

    public int getEstatus() {
        return estatus;
    }

    public void setEstatus(int estatus) {
        this.estatus = estatus;
    }

    public Propiedad getPropiedad() {
        return propiedad;
    }

    public void setPropiedad(Propiedad propiedad) {
        this.propiedad = propiedad;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Ciudad getCiudad() {
        return ciudad;
    }

    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "Servicio{" +
                "idServicio=" + idServicio +
                ", estatus=" + estatus +
                ", propiedad=" + propiedad +
                ", cliente=" + cliente +
                ", ciudad=" + ciudad +
                ", categoria=" + categoria +
                '}';
    }
}

