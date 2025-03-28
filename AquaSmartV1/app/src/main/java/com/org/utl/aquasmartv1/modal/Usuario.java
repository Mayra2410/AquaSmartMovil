package com.org.utl.aquasmartv1.modal;

import java.sql.Timestamp;

public class Usuario {
    private  int idUsuario;
    private String nombre;
    private String contrasenia;
    private  String foto;
    private int estatus;
    private  int rol;
    private  String lastToken;
    private Timestamp dateLastToken;

    public Usuario() {
    }

    public Usuario(int idUsuario, String nombre, String contrasenia, String foto, int estatus, int rol, String lastToken, Timestamp dateLastToken) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.contrasenia = contrasenia;
        this.foto = foto;
        this.estatus = estatus;
        this.rol = rol;
        this.lastToken = lastToken;
        this.dateLastToken = dateLastToken;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
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

    public int getRol() {
        return rol;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }

    public String getLastToken() {
        return lastToken;
    }

    public void setLastToken(String lastToken) {
        this.lastToken = lastToken;
    }

    public Timestamp getDateLastToken() {
        return dateLastToken;
    }

    public void setDateLastToken(Timestamp dateLastToken) {
        this.dateLastToken = dateLastToken;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", contrasenia='" + contrasenia + '\'' +
                ", foto='" + foto + '\'' +
                ", estatus=" + estatus +
                ", rol=" + rol +
                ", lastToken='" + lastToken + '\'' +
                ", dateLastToken=" + dateLastToken +
                '}';
    }
}
