package com.dacarex.capital.modelo;

import com.dacarex.capital.enums.TipoMovimiento;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Categoria extends EntidadBase implements IValidable {

    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoMovimiento tipo;

    private String colorHex; // Para distinguir en los graficos

    public Categoria() {
        super();
    }

    public Categoria(String nombre, TipoMovimiento tipo, String colorHex) {
        super();
        this.nombre = nombre;
        this.tipo = tipo;
        this.colorHex = colorHex;
    }

    @Override
    public List<String> validar() {
        List<String> errores = new ArrayList<>();
        if (nombre == null || nombre.isBlank())
            errores.add("El nombre de la categoria es obligatorio.");
        if (tipo == null)
            errores.add("El tipo es obligatorio.");
        return errores;
    }

    @Override
    public String toResumen() {
        return nombre + " (" + tipo + ")";
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public TipoMovimiento getTipo() { return tipo; }
    public void setTipo(TipoMovimiento tipo) { this.tipo = tipo; }

    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }
}