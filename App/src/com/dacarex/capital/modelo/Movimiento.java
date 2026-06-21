package com.dacarex.capital.modelo;

import com.dacarex.capital.enums.TipoMovimiento;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Movimiento extends EntidadBase implements IValidable, IExportable {

    @Enumerated(EnumType.STRING)
    private TipoMovimiento tipo;

    private String descripcion;
    private double importe;
    private LocalDate fecha;
    private String notas;

    @ManyToOne
    private Categoria categoria;

    public Movimiento() {
        super();
    }

    public Movimiento(TipoMovimiento tipo, String descripcion, double importe,
                      Categoria categoria, LocalDate fecha, String notas) {
        super();
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.importe = importe;
        this.categoria = categoria;
        this.fecha = fecha;
        this.notas = notas;
    }

    @Override
    public List<String> validar() {
        List<String> errores = new ArrayList<>();
        if (descripcion == null || descripcion.isBlank())
            errores.add("La descripcion es obligatoria.");
        if (importe <= 0)
            errores.add("El importe debe ser mayor que 0.");
        if (categoria == null)
            errores.add("La categoria es obligatoria.");
        if (fecha == null)
            errores.add("La fecha es obligatoria.");
        return errores;
    }

    @Override
    public String toCsv() {
        return new StringBuilder()
                .append(fecha).append(",")
                .append(tipo).append(",")
                .append(descripcion).append(",")
                .append(importe).append(",")
                .append(categoria != null ? categoria.getNombre() : "").append(",")
                .append(notas != null ? notas : "")
                .toString();
    }

    @Override
    public String toTexto() {
        return new StringBuilder()
                .append(fecha).append(" | ")
                .append(tipo).append(" | ")
                .append(descripcion).append(" | ")
                .append(String.format("%.2f", importe)).append(" EUR")
                .toString();
    }

    @Override
    public String toResumen() {
        return toTexto();
    }

    // Getters y Setters
    public TipoMovimiento getTipo() { return tipo; }
    public void setTipo(TipoMovimiento tipo) { this.tipo = tipo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getImporte() { return importe; }
    public void setImporte(double importe) { this.importe = importe; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
}