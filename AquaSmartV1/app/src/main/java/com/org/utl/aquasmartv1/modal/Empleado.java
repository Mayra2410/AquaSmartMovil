package com.org.utl.aquasmartv1.modal;

public class Empleado {
    private int idEmpleado;
    private String rfc;
    private Cargo cargo;
    private  Persona persona;

    public Empleado() {
    }

    public Empleado(int idEmpleado, String rfc, Cargo cargo, Persona persona) {
        this.idEmpleado = idEmpleado;
        this.rfc = rfc;
        this.cargo = cargo;
        this.persona = persona;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "idEmpleado=" + idEmpleado +
                ", rfc='" + rfc + '\'' +
                ", cargo=" + cargo +
                ", persona=" + persona +
                '}';
    }
}
