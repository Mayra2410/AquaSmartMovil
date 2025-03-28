package com.org.utl.aquasmartv1.modal;

public class Propiedad {
    private int idPropiedad;
    private String numExt;
    private String numInt;
    private String calle;
    private String colonia;
    private Double latitud;
    private Double longitud;
    private int codigoP;
    private String foto;
    private int estatus;
    private Cliente cliente;
    private Ciudad ciudad;
    private Medidor medidor;

    public Propiedad() {
    }

    public Propiedad(int idPropiedad, String numExt, String numInt, String calle, String colonia, Double latitud, Double longitud, int codigoP, String foto, int estatus, Cliente cliente, Ciudad ciudad, Medidor medidor) {
        this.idPropiedad = idPropiedad;
        this.numExt = numExt;
        this.numInt = numInt;
        this.calle = calle;
        this.colonia = colonia;
        this.latitud = latitud;
        this.longitud = longitud;
        this.codigoP = codigoP;
        this.foto = foto;
        this.estatus = estatus;
        this.cliente = cliente;
        this.ciudad = ciudad;
        this.medidor = medidor;
    }

    public int getIdPropiedad() {
        return idPropiedad;
    }

    public void setIdPropiedad(int idPropiedad) {
        this.idPropiedad = idPropiedad;
    }

    public String getNumExt() {
        return numExt;
    }

    public void setNumExt(String numExt) {
        this.numExt = numExt;
    }

    public String getNumInt() {
        return numInt;
    }

    public void setNumInt(String numInt) {
        this.numInt = numInt;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public int getCodigoP() {
        return codigoP;
    }

    public void setCodigoP(int codigoP) {
        this.codigoP = codigoP;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public int getEstatus() {
        return estatus;
    }

    public void setEstatus(int estatus) {
        this.estatus = estatus;
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

    public Medidor getMedidor() {
        return medidor;
    }

    public void setMedidor(Medidor medidor) {
        this.medidor = medidor;
    }

    @Override
    public String toString() {
        return "Propiedad{" +
                "idPropiedad=" + idPropiedad +
                ", numExt='" + numExt + '\'' +
                ", numInt='" + numInt + '\'' +
                ", calle='" + calle + '\'' +
                ", colonia='" + colonia + '\'' +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", codigoP=" + codigoP +
                ", foto='" + foto + '\'' +
                ", estatus=" + estatus +
                ", cliente=" + cliente +
                ", ciudad=" + ciudad +
                ", medidor=" + medidor +
                '}';
    }
}
