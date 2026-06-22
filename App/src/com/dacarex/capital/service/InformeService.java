package com.dacarex.capital.service;

import com.dacarex.capital.dao.MovimientoDAO;
import com.dacarex.capital.enums.PeriodoRango;
import com.dacarex.capital.enums.TipoMovimiento;
import com.dacarex.capital.modelo.Movimiento;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class InformeService {

    private final MovimientoDAO dao = new MovimientoDAO();

    private List<Movimiento> filtrarDesde(LocalDate desde) {
        List<Movimiento> todos = dao.buscarTodos();
        if (desde == null) return todos;
        return todos.stream()
                .filter(m -> !m.getFecha().isBefore(desde))
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────
    // GASTOS / INGRESOS POR CATEGORIA (grafico tarta)
    // ─────────────────────────────────────
    public Map<String, Double> gastosPorCategoria(LocalDate desde) {
        return filtrarDesde(desde).stream()
                .filter(m -> m.getTipo() == TipoMovimiento.GASTO)
                .collect(Collectors.groupingBy(
                    m -> m.getCategoria().getNombre(),
                    LinkedHashMap::new,
                    Collectors.summingDouble(Movimiento::getImporte)
                ));
    }

    public Map<String, Double> gastosPorCategoria() {
        return gastosPorCategoria(null);
    }

    public Map<String, Double> ingresosPorCategoria(LocalDate desde) {
        return filtrarDesde(desde).stream()
                .filter(m -> m.getTipo() == TipoMovimiento.INGRESO)
                .collect(Collectors.groupingBy(
                    m -> m.getCategoria().getNombre(),
                    LinkedHashMap::new,
                    Collectors.summingDouble(Movimiento::getImporte)
                ));
    }

    public Map<String, Double> ingresosPorCategoria() {
        return ingresosPorCategoria(null);
    }

    // ─────────────────────────────────────
    // EVOLUCION DEL SALDO ACUMULADO (grafico de lineas)
    // ─────────────────────────────────────
    public Map<String, Double> evolucionSaldoPorFecha(LocalDate desde) {
        List<Movimiento> movimientos = filtrarDesde(desde).stream()
                .sorted(Comparator.comparing(Movimiento::getFecha))
                .collect(Collectors.toList());

        Map<String, Double> resultado = new LinkedHashMap<>();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yy");
        double acumulado = 0;

        for (Movimiento m : movimientos) {
            acumulado += m.getTipo() == TipoMovimiento.INGRESO
                    ? m.getImporte()
                    : -m.getImporte();
            resultado.put(m.getFecha().format(formato), acumulado);
        }

        return resultado;
    }

    // ─────────────────────────────────────
    // INGRESOS VS GASTOS AGRUPADOS (grafico de barras)
    // La granularidad se adapta automaticamente al periodo elegido
    // ─────────────────────────────────────
    public Map<String, double[]> evolucionAgrupada(PeriodoRango periodo) {
        LocalDate hoy = LocalDate.now();
        LocalDate desde = periodo.fechaInicio();
        List<Movimiento> movimientos = filtrarDesde(desde);

        if (desde == null) {
            desde = movimientos.stream()
                    .map(Movimiento::getFecha)
                    .min(LocalDate::compareTo)
                    .orElse(hoy);
        }

        PeriodoRango.Granularidad gran = periodo.granularidad();
        DateTimeFormatter formato = switch (gran) {
            case DIA  -> DateTimeFormatter.ofPattern("dd MMM");
            case MES  -> DateTimeFormatter.ofPattern("MMM yy");
            case ANIO -> DateTimeFormatter.ofPattern("yyyy");
        };

        Map<String, double[]> resultado = new LinkedHashMap<>();

        LocalDate cursor = switch (gran) {
            case DIA  -> desde;
            case MES  -> desde.withDayOfMonth(1);
            case ANIO -> desde.withDayOfYear(1);
        };

        while (!cursor.isAfter(hoy)) {
            resultado.put(cursor.format(formato), new double[]{0, 0});
            cursor = switch (gran) {
                case DIA  -> cursor.plusDays(1);
                case MES  -> cursor.plusMonths(1);
                case ANIO -> cursor.plusYears(1);
            };
        }

        for (Movimiento m : movimientos) {
            String clave = m.getFecha().format(formato);
            double[] valores = resultado.computeIfAbsent(clave, k -> new double[]{0, 0});
            if (m.getTipo() == TipoMovimiento.INGRESO) {
                valores[0] += m.getImporte();
            } else {
                valores[1] += m.getImporte();
            }
        }

        return resultado;
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
        Map<String, double[]> evolucion = evolucionAgrupada(PeriodoRango.SEIS_MESES);
        return evolucion.values().stream()
                .mapToDouble(v -> v[1])
                .average()
                .orElse(0);
    }
    public Map<String, Map<String, Double>> gastosMensualesPorCategoria(PeriodoRango periodo) {
        LocalDate desde = periodo.fechaInicio();
        PeriodoRango.Granularidad gran = periodo.granularidad();

        DateTimeFormatter formato = switch (gran) {
            case DIA  -> DateTimeFormatter.ofPattern("dd MMM");
            case MES  -> DateTimeFormatter.ofPattern("MMM yy");
            case ANIO -> DateTimeFormatter.ofPattern("yyyy");
        };

        List<Movimiento> movimientos = filtrarDesde(desde).stream()
                .filter(m -> m.getTipo() == TipoMovimiento.GASTO)
                .collect(Collectors.toList());

        // Resultado: categoria -> (periodo -> importe)
        Map<String, Map<String, Double>> resultado = new LinkedHashMap<>();

        for (Movimiento m : movimientos) {
            if (m.getCategoria() == null) continue;
            String periodoStr = m.getFecha().format(formato);
            String cat = m.getCategoria().getNombre();
            resultado.computeIfAbsent(cat, k -> new LinkedHashMap<>())
                     .merge(periodoStr, m.getImporte(), Double::sum);
        }

        return resultado;
    }
}