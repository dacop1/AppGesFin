package com.dacarex.capital.modelo;

import com.dacarex.capital.enums.TipoCuenta;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;

@Entity
public class Usuario extends EntidadBase implements IValidable {

    private String nombre;
    private String email;
    private String contrasenia;

    @Enumerated(EnumType.STRING)
    private TipoCuenta tipoCuenta;

    private String nombreEmpresa;

    public Usuario() {
        super();
    }

    public Usuario(String nombre, String email, String contrasenia,
                   TipoCuenta tipoCuenta, String nombreEmpresa) {
        super();
        this.nombre = nombre;
        this.email = email;
        this.contrasenia = contrasenia;
        this.tipoCuenta = tipoCuenta;
        this.nombreEmpresa = nombreEmpresa;
    }

    @Override
    public java.util.List<String> validar() {
        java.util.List<String> errores = new java.util.ArrayList<>();
        if (nombre == null || nombre.isBlank())
            errores.add("El nombre es obligatorio.");
        if (email == null || !email.contains("@"))
            errores.add("El email no es valido.");
        if (contrasenia == null || contrasenia.length() < 6)
            errores.add("La contrasenia debe tener al menos 6 caracteres.");
        if (TipoCuenta.EMPRESA.equals(tipoCuenta) && (nombreEmpresa == null || nombreEmpresa.isBlank()))
            errores.add("El nombre de empresa es obligatorio para cuentas de empresa.");
        return errores;
    }

    @Override
    public String toResumen() {
        return nombre + " (" + email + ")";
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContrasenia() { return contrasenia; }
    public void setContrasenia(String contrasenia) { this.contrasenia = contrasenia; }

    public TipoCuenta getTipoCuenta() { return tipoCuenta; }
    public void setTipoCuenta(TipoCuenta tipoCuenta) { this.tipoCuenta = tipoCuenta; }

    public String getNombreEmpresa() { return nombreEmpresa; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }
}