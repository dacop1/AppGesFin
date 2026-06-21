package com.dacarex.capital.vista.paneles;

import com.dacarex.capital.enums.PeriodoRango;
import com.dacarex.capital.service.InformeService;
import com.dacarex.capital.service.MovimientoService;
import com.dacarex.capital.util.Formato;
import com.dacarex.capital.vista.componentes.TarjetaKPI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.Map;

public class PanelDashboard extends ScrollPane {

    private final MovimientoService movimientoService = new MovimientoService();
    private final InformeService informeService = new InformeService();

    private TarjetaKPI tarjetaSaldo;
    private TarjetaKPI tarjetaIngresos;
    private TarjetaKPI tarjetaGastos;
    private TarjetaKPI tarjetaCategoria;

    private LineChart<String, Number> graficoLineas;
    private PieChart graficoTarta;
    private BarChart<String, Number> graficoBarras;

    private ComboBox<PeriodoRango> cmbPeriodo;

    public PanelDashboard() {
        setFitToWidth(true);
        setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        setContent(construirContenido());
    }

    private VBox construirContenido() {
        VBox raiz = new VBox(20);
        raiz.setPadding(new Insets(25, 30, 25, 30));

        // ── CABECERA ──
        Label lblTitulo = new Label("Dashboard");
        lblTitulo.getStyleClass().add("titulo-pagina");

        Label lblSub = new Label("Resumen general de tus finanzas");
        lblSub.getStyleClass().add("subtitulo-pagina");

        VBox textos = new VBox(2, lblTitulo, lblSub);

        cmbPeriodo = new ComboBox<>();
        cmbPeriodo.getItems().addAll(PeriodoRango.values());
        cmbPeriodo.setValue(PeriodoRango.SEIS_MESES);
        cmbPeriodo.setOnAction(e -> recargar());

        Label lblPeriodo = new Label("Periodo:");
        HBox cajaPeriodo = new HBox(8, lblPeriodo, cmbPeriodo);
        cajaPeriodo.setAlignment(Pos.CENTER_RIGHT);

        BorderPane cabecera = new BorderPane();
        cabecera.setLeft(textos);
        cabecera.setRight(cajaPeriodo);
        BorderPane.setAlignment(cajaPeriodo, Pos.CENTER_RIGHT);

        // ── TARJETAS KPI ──
        tarjetaSaldo     = new TarjetaKPI("💰", "Saldo actual", "0,00 €");
        tarjetaIngresos  = new TarjetaKPI("📈", "Ingresos totales", "0,00 €");
        tarjetaGastos    = new TarjetaKPI("📉", "Gastos totales", "0,00 €");
        tarjetaCategoria = new TarjetaKPI("🏷️", "Categoria mas activa", "-");

        HBox filaTarjetas = new HBox(18, tarjetaSaldo, tarjetaIngresos, tarjetaGastos, tarjetaCategoria);
        for (var tarjeta : filaTarjetas.getChildren()) {
            HBox.setHgrow(tarjeta, Priority.ALWAYS);
        }

        // ── GRAFICO DE LINEAS ──
        Label lblEvolucion = new Label("Evolucion del saldo acumulado");
        lblEvolucion.getStyleClass().add("titulo-seccion");

        graficoLineas = new LineChart<>(new CategoryAxis(), new NumberAxis());
        graficoLineas.setLegendVisible(false);
        graficoLineas.setPrefHeight(260);
        graficoLineas.getStyleClass().add("chart-card");
        graficoLineas.setAnimated(false);

        // ── FILA INFERIOR: TARTA + BARRAS ──
        Label lblGastos = new Label("Gastos por categoria");
        lblGastos.getStyleClass().add("titulo-seccion");
        graficoTarta = new PieChart();
        graficoTarta.setPrefHeight(280);
        graficoTarta.getStyleClass().add("chart-card");
        graficoTarta.setAnimated(false);
        VBox colTarta = new VBox(8, lblGastos, graficoTarta);
        HBox.setHgrow(colTarta, Priority.ALWAYS);

        Label lblBarras = new Label("Ingresos vs Gastos");
        lblBarras.getStyleClass().add("titulo-seccion");
        graficoBarras = new BarChart<>(new CategoryAxis(), new NumberAxis());
        graficoBarras.setPrefHeight(280);
        graficoBarras.getStyleClass().add("chart-card");
        graficoBarras.setAnimated(false);
        VBox colBarras = new VBox(8, lblBarras, graficoBarras);
        HBox.setHgrow(colBarras, Priority.ALWAYS);

        HBox filaGraficos = new HBox(18, colTarta, colBarras);

        raiz.getChildren().addAll(cabecera, filaTarjetas, lblEvolucion, graficoLineas, filaGraficos);
        return raiz;
    }

    public void recargar() {
        PeriodoRango periodo = cmbPeriodo.getValue();
        LocalDate desde = periodo.fechaInicio();

        double saldo    = movimientoService.calcularSaldo();
        double ingresos = movimientoService.calcularTotalIngresos();
        double gastos   = movimientoService.calcularTotalGastos();
        String catTop   = informeService.categoriaMasActiva();

        tarjetaSaldo.actualizar(Formato.moneda(saldo), saldo >= 0 ? "#28c76f" : "#f05252");
        tarjetaIngresos.actualizar(Formato.moneda(ingresos), "#28c76f");
        tarjetaGastos.actualizar(Formato.moneda(gastos), "#f05252");
        tarjetaCategoria.actualizar(catTop, "#17a2b8");

        // Linea: saldo acumulado
        Map<String, Double> evolucionSaldo = informeService.evolucionSaldoPorFecha(desde);
        XYChart.Series<String, Number> serieSaldo = new XYChart.Series<>();
        evolucionSaldo.forEach((fecha, valor) ->
            serieSaldo.getData().add(new XYChart.Data<>(fecha, valor)));
        graficoLineas.getData().clear();
        graficoLineas.getData().add(serieSaldo);

        // Tarta: gastos por categoria
        Map<String, Double> gastosCat = informeService.gastosPorCategoria(desde);
        graficoTarta.getData().clear();
        gastosCat.forEach((nombre, importe) ->
            graficoTarta.getData().add(
                new PieChart.Data(nombre + " (" + Formato.moneda(importe) + ")", importe)));

        // Barras: ingresos vs gastos agrupado segun granularidad del periodo
        Map<String, double[]> agrupado = informeService.evolucionAgrupada(periodo);

        XYChart.Series<String, Number> serieIngresos = new XYChart.Series<>();
        serieIngresos.setName("Ingresos");
        XYChart.Series<String, Number> serieGastos = new XYChart.Series<>();
        serieGastos.setName("Gastos");

        agrupado.forEach((clave, valores) -> {
            serieIngresos.getData().add(new XYChart.Data<>(clave, valores[0]));
            serieGastos.getData().add(new XYChart.Data<>(clave, valores[1]));
        });

        graficoBarras.getData().clear();
        graficoBarras.getData().addAll(serieIngresos, serieGastos);
    }
}