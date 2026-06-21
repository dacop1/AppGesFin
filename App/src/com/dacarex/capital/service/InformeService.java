package com.dacarex.capital.service;

import com.dacarex.capital.dao.MovimientoDAO;
import com.dacarex.capital.enums.TipoMovimiento;
import com.dacarex.capital.modelo.Movimiento;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class InformeService {

    private final MovimientoDAO dao = new MovimientoDAO();

    // ─────────────────────────────────────
    // PARA GRAFICO DE TARTA (gastos por categoria)
    // ─────────────────────────────────────
    public Map<String, Double> gastosPorCategoria() {
        return dao.buscarTodos().stream()
                .filter(m -> m.getTipo() == TipoMovimiento.GASTO)
                .collect(Collectors.groupingBy(
                    m -> m.getCategoria().getNombre(),
                    LinkedHashMap::new,
                    Collectors.summingDouble(Movimiento::getImporte)
                ));
    }

    public Map<String, Double> ingresosPorCategoria() {
        return dao.buscarTodos().stream()
                .filter(m -> m.getTipo() == TipoMovimiento.INGRESO)
                .collect(Collectors.groupingBy(
                    m -> m.getCategoria().getNombre(),
                    LinkedHashMap::new,
                    Collectors.summingDouble(Movimiento::getImporte)
                ));
    }

    // ─────────────────────────────────────
    // PARA GRAFICO DE BARRAS (ingresos vs gastos por mes)
    // ─────────────────────────────────────
    public Map<String, double[]> evolucionMensual(int numeroMeses) {
        LocalDate hoy = LocalDate.now();
        Map<String, double[]> resultado = new LinkedHashMap<>();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("MMM yy");

        for (int i = numeroMeses - 1; i >= 0; i--) {
            LocalDate mes = hoy.minusMonths(i);
            String clave = mes.format(formato);
            resultado.put(clave, new double[]{0, 0}); // [ingresos, gastos]
        }

        List<Movimiento> movimientos = dao.buscarTodos();

        for (Movimiento m : movimientos) {
            String clave = m.getFecha().format(formato);
            if (resultado.containsKey(clave)) {
                double[] valores = resultado.get(clave);
                if (m.getTipo() == TipoMovimiento.INGRESO) {
                    valores[0] += m.getImporte();
                } else {
                    valores[1] += m.getImporte();
                }
            }
        }

        return resultado;
    }

    // ─────────────────────────────────────
    // PARA GRAFICO DE LINEAS (saldo acumulado en el tiempo)
    // ─────────────────────────────────────
    public List<double[]> evolucionSaldoAcumulado() {
        List<Movimiento> movimientos = dao.buscarTodos().stream()
                .sorted(Comparator.comparing(Movimiento::getFecha))
                .collect(Collectors.toList());

        List<double[]> puntos = new ArrayList<>();
        double acumulado = 0;
        int indice = 0;

        for (Movimiento m : movimientos) {
            acumulado += m.getTipo() == TipoMovimiento.INGRESO
                    ? m.getImporte()
                    : -m.getImporte();
            puntos.add(new double[]{indice++, acumulado});
        }

        return puntos;
    }

    // ─────────────────────────────────────
    // KPIs
    // ─────────────────────────────────────
    public String categoriaMasActiva() {
        return dao.buscarTodos().stream()
                .collect(Collectors.groupingBy(
                    m -> m.getCategoria().getNombre(),
                    Collectors.counting()
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Sin datos");
    }

    public Optional<Movimiento> mayorIngreso() {
        return dao.buscarTodos().stream()
                .filter(m -> m.getTipo() == TipoMovimiento.INGRESO)
                .max(Comparator.comparingDouble(Movimiento::getImporte));
    }

    public Optional<Movimiento> mayorGasto() {
        return dao.buscarTodos().stream()
                .filter(m -> m.getTipo() == TipoMovimiento.GASTO)
                .max(Comparator.comparingDouble(Movimiento::getImporte));
    }

    public double promedioGastoMensual() {
        Map<String, double[]> evolucion = evolucionMensual(6);
        return evolucion.values().stream()
                .mapToDouble(v -> v[1])
                .average()
                .orElse(0);
    }
}