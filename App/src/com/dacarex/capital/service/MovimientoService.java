package com.dacarex.capital.service;

import com.dacarex.capital.dao.MovimientoDAO;
import com.dacarex.capital.enums.TipoMovimiento;
import com.dacarex.capital.exception.ValidacionException;
import com.dacarex.capital.modelo.Categoria;
import com.dacarex.capital.modelo.Movimiento;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MovimientoService {

    private final MovimientoDAO dao = new MovimientoDAO();

    public Movimiento crear(TipoMovimiento tipo, String descripcion, double importe,
                            Categoria categoria, LocalDate fecha, String notas) {

        Movimiento nuevo = new Movimiento(tipo, descripcion, importe, categoria, fecha, notas);

        if (!nuevo.esValido())
            throw new ValidacionException(nuevo.validar());

        dao.guardar(nuevo);
        return nuevo;
    }

    public void actualizar(Movimiento movimiento, TipoMovimiento tipo, String descripcion, double importe,
                           Categoria categoria, LocalDate fecha, String notas) {

        TipoMovimiento tipoAnterior = movimiento.getTipo();
        String descAnterior = movimiento.getDescripcion();
        double importeAnterior = movimiento.getImporte();
        Categoria catAnterior = movimiento.getCategoria();
        LocalDate fechaAnterior = movimiento.getFecha();
        String notasAnteriores = movimiento.getNotas();

        movimiento.setTipo(tipo);
        movimiento.setDescripcion(descripcion);
        movimiento.setImporte(importe);
        movimiento.setCategoria(categoria);
        movimiento.setFecha(fecha);
        movimiento.setNotas(notas);

        if (!movimiento.esValido()) {
            movimiento.setTipo(tipoAnterior);
            movimiento.setDescripcion(descAnterior);
            movimiento.setImporte(importeAnterior);
            movimiento.setCategoria(catAnterior);
            movimiento.setFecha(fechaAnterior);
            movimiento.setNotas(notasAnteriores);
            throw new ValidacionException(movimiento.validar());
        }

        dao.actualizar(movimiento);
    }

    public void eliminar(long id) {
        dao.eliminar(id);
    }

    public List<Movimiento> obtenerTodos() {
        return dao.buscarTodos().stream()
                .sorted(Comparator.comparing(Movimiento::getFecha).reversed())
                .collect(Collectors.toList());
    }

    public List<Movimiento> filtrarPorTipo(TipoMovimiento tipo) {
        return dao.buscarPorTipo(tipo);
    }

    public List<Movimiento> filtrarPorTexto(String texto) {
        if (texto == null || texto.isBlank()) return obtenerTodos();
        return dao.buscarPorTexto(texto);
    }

    public List<Movimiento> filtrarPorRango(LocalDate desde, LocalDate hasta) {
        return dao.buscarPorRangoFechas(desde, hasta);
    }

    public List<Movimiento> filtrarAvanzado(TipoMovimiento tipo, Categoria categoria,
                                            LocalDate desde, LocalDate hasta, String texto) {
        return obtenerTodos().stream()
                .filter(m -> tipo == null || m.getTipo() == tipo)
                .filter(m -> categoria == null || (m.getCategoria() != null && m.getCategoria().getId() == categoria.getId()))
                .filter(m -> desde == null || !m.getFecha().isBefore(desde))
                .filter(m -> hasta == null || !m.getFecha().isAfter(hasta))
                .filter(m -> texto == null || texto.isBlank()
                        || m.getDescripcion().toLowerCase().contains(texto.toLowerCase())
                        || (m.getNotas() != null && m.getNotas().toLowerCase().contains(texto.toLowerCase())))
                .collect(Collectors.toList());
    }

    public boolean categoriaEnUso(long categoriaId) {
        return obtenerTodos().stream()
                .anyMatch(m -> m.getCategoria() != null && m.getCategoria().getId() == categoriaId);
    }

    public double calcularTotalIngresos() {
        return dao.buscarTodos().stream()
                .filter(m -> m.getTipo() == TipoMovimiento.INGRESO)
                .mapToDouble(Movimiento::getImporte)
                .sum();
    }

    public double calcularTotalGastos() {
        return dao.buscarTodos().stream()
                .filter(m -> m.getTipo() == TipoMovimiento.GASTO)
                .mapToDouble(Movimiento::getImporte)
                .sum();
    }

    public double calcularSaldo() {
        return calcularTotalIngresos() - calcularTotalGastos();
    }
}