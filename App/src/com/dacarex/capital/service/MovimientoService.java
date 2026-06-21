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