package com.dacarex.capital.vista.paneles;

import com.dacarex.capital.enums.PeriodoRango;
import com.dacarex.capital.service.InformeService;
import com.dacarex.capital.service.MovimientoService;
import com.dacarex.capital.util.Formato;
import com.dacarex.capital.util.TemaManager;
import com.dacarex.capital.vista.componentes.IconoFX;
import com.dacarex.capital.vista.componentes.TarjetaKPI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Map;

public class PanelDashboard extends ScrollPane {

    private final MovimientoService movimientoService = new MovimientoService();
    private final InformeService informeService = new InformeService();

    private TarjetaKPI tarjetaSaldo;
    private TarjetaKPI tarjetaIngresos;
    private TarjetaKPI tarjetaGastos;
    private TarjetaKPI tarjetaCategoria;

    private ComboBox<PeriodoRango> cmbPeriodo;

    // Datos actuales (para duplicar en ventanas maximizadas)
    private Map<String, Double> datosLineaActuales;
    private Map<String, Double> datosTartaActuales;
    private Map<String, double[]> datosBarrasActuales;
    private Map<String, Map<String, Double>> datosApiladoActuales;

    // Referencias a los gráficos del dashboard
    private LineChart<String, Number>    graficoLineas;
    private PieChart                     graficoTarta;
    private BarChart<String, Number>     graficoBarras;
    private StackedBarChart<String, Number> graficoApilado;

    public PanelDashboard() {
        setFitToWidth(true);
        setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        setContent(construirContenido());
    }

    // ─────────────────────────────────────
    // LAYOUT
    // ─────────────────────────────────────
    private VBox construirContenido() {
        VBox raiz = new VBox(20);
        raiz.setPadding(new Insets(25, 30, 25, 30));

        // Cabecera
        Label lblTitulo = new Label("Dashboard");
        lblTitulo.getStyleClass().add("titulo-pagina");

        Label lblSub = new Label("Resumen general de tus finanzas");
        lblSub.getStyleClass().add("subtitulo-pagina");

        cmbPeriodo = new ComboBox<>();
        cmbPeriodo.getItems().addAll(PeriodoRango.values());
        cmbPeriodo.setValue(PeriodoRango.SEIS_MESES);
        cmbPeriodo.setPrefWidth(200);
        cmbPeriodo.setOnAction(e -> recargar());

        Label lblPeriodo = new Label("Periodo:");
        lblPeriodo.getStyleClass().add("subtitulo-pagina");
        HBox cajaPeriodo = new HBox(8, lblPeriodo, cmbPeriodo);
        cajaPeriodo.setAlignment(Pos.CENTER_RIGHT);

        BorderPane cabecera = new BorderPane();
        cabecera.setLeft(new VBox(2, lblTitulo, lblSub));
        cabecera.setRight(cajaPeriodo);

        // KPIs
        tarjetaSaldo = new TarjetaKPI(
            IconoFX.crear(IconoFX.Tipo.BILLETERA, Color.web("#5e7cff"), 22), "Saldo actual", "0,00 €");
        tarjetaIngresos = new TarjetaKPI(
            IconoFX.crear(IconoFX.Tipo.TENDENCIA_SUBE, Color.web("#28c76f"), 22), "Ingresos totales", "0,00 €");
        tarjetaGastos = new TarjetaKPI(
            IconoFX.crear(IconoFX.Tipo.TENDENCIA_BAJA, Color.web("#f05252"), 22), "Gastos totales", "0,00 €");
        tarjetaCategoria = new TarjetaKPI(
            IconoFX.crear(IconoFX.Tipo.ETIQUETA, Color.web("#17a2b8"), 22), "Categoria mas activa", "-");

        HBox filaTarjetas = new HBox(18, tarjetaSaldo, tarjetaIngresos, tarjetaGastos, tarjetaCategoria);
        filaTarjetas.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        // Gráfico 1: LineChart saldo (ancho completo)
        graficoLineas = crearLineChart();
        VBox card1 = crearCardGrafico("Evolucion del saldo acumulado", graficoLineas,
            () -> mostrarEnVentana("Evolucion del saldo acumulado", crearLineChartConDatos(datosLineaActuales)));

        // Gráfico 2 + 3: Tarta y Barras (en fila)
        graficoTarta = crearPieChart();
        VBox card2 = crearCardGrafico("Gastos por categoria", graficoTarta,
            () -> mostrarEnVentana("Gastos por categoria", crearPieChartConDatos(datosTartaActuales)));

        graficoBarras = crearBarChart();
        VBox card3 = crearCardGrafico("Ingresos vs Gastos por periodo", graficoBarras,
            () -> mostrarEnVentana("Ingresos vs Gastos", crearBarChartConDatos(datosBarrasActuales)));

        HBox fila23 = new HBox(18, card2, card3);
        fila23.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        // Gráfico 4: StackedBarChart gastos por categoría en el tiempo (ancho completo)
        graficoApilado = crearStackedBarChart();
        VBox card4 = crearCardGrafico("Desglose de gastos por categoria y periodo", graficoApilado,
            () -> mostrarEnVentana("Desglose de gastos", crearStackedBarChartConDatos(datosApiladoActuales)));

        raiz.getChildren().addAll(cabecera, filaTarjetas, card1, fila23, card4);
        return raiz;
    }

    // ─────────────────────────────────────
    // CARD CON BOTÓN MAXIMIZAR
    // ─────────────────────────────────────
    private VBox crearCardGrafico(String titulo, javafx.scene.Node grafico, Runnable accionMaximizar) {
        Label lblTitulo = new Label(titulo);
        lblTitulo.getStyleClass().add("titulo-seccion");

        Button btnMax = new Button("Ampliar");
        btnMax.setGraphic(IconoFX.crear(IconoFX.Tipo.EXPANDIR, Color.web("#9a9ab5"), 13));
        btnMax.getStyleClass().add("sidebar-link");
        btnMax.setStyle("-fx-font-size: 11px;");
        btnMax.setOnAction(e -> accionMaximizar.run());

        BorderPane header = new BorderPane();
        header.setLeft(lblTitulo);
        header.setRight(btnMax);
        header.setPadding(new Insets(0, 0, 8, 0));

        VBox card = new VBox(0, header, grafico);
        card.getStyleClass().add("chart-card");
        card.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    // ─────────────────────────────────────
    // VENTANA MAXIMIZADA
    // ─────────────────────────────────────
    private void mostrarEnVentana(String titulo, javafx.scene.Node grafico) {
        Stage ventana = new Stage();
        ventana.setTitle("Dacarex Capital — " + titulo);

        BorderPane root = new BorderPane();
        root.setCenter(grafico);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 1000, 650);
        scene.getStylesheets().add(TemaManager.getHojaEstilos());
        root.setStyle("-fx-background-color: " +
            (TemaManager.isModoOscuro() ? "#12121c" : "#f4f6fa") + ";");

        ventana.setScene(scene);
        ventana.setResizable(true);
        ventana.show();
    }

    // ─────────────────────────────────────
    // FACTORY METHODS — crean gráficos vacíos
    // ─────────────────────────────────────
    private LineChart<String, Number> crearLineChart() {
        LineChart<String, Number> chart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        chart.setLegendVisible(false);
        chart.setPrefHeight(240);
        chart.setAnimated(false);
        chart.setCreateSymbols(true);
        return chart;
    }

    private PieChart crearPieChart() {
        PieChart chart = new PieChart();
        chart.setPrefHeight(260);
        chart.setAnimated(false);
        chart.setLegendVisible(true);
        return chart;
    }

    private BarChart<String, Number> crearBarChart() {
        BarChart<String, Number> chart = new BarChart<>(new CategoryAxis(), new NumberAxis());
        chart.setPrefHeight(260);
        chart.setAnimated(false);
        chart.setBarGap(2);
        chart.setCategoryGap(12);
        return chart;
    }

    private StackedBarChart<String, Number> crearStackedBarChart() {
        StackedBarChart<String, Number> chart = new StackedBarChart<>(new CategoryAxis(), new NumberAxis());
        chart.setPrefHeight(260);
        chart.setAnimated(false);
        chart.setCategoryGap(8);
        return chart;
    }

    // ─────────────────────────────────────
    // FACTORY METHODS — crean gráficos CON datos (para ventanas)
    // ─────────────────────────────────────
    private LineChart<String, Number> crearLineChartConDatos(Map<String, Double> datos) {
        LineChart<String, Number> chart = crearLineChart();
        chart.setPrefSize(900, 600);
        if (datos != null) poblarLineChart(chart, datos);
        return chart;
    }

    private PieChart crearPieChartConDatos(Map<String, Double> datos) {
        PieChart chart = crearPieChart();
        chart.setPrefSize(900, 600);
        if (datos != null) poblarPieChart(chart, datos);
        return chart;
    }

    private BarChart<String, Number> crearBarChartConDatos(Map<String, double[]> datos) {
        BarChart<String, Number> chart = crearBarChart();
        chart.setPrefSize(900, 600);
        if (datos != null) poblarBarChart(chart, datos);
        return chart;
    }

    private StackedBarChart<String, Number> crearStackedBarChartConDatos(
            Map<String, Map<String, Double>> datos) {
        StackedBarChart<String, Number> chart = crearStackedBarChart();
        chart.setPrefSize(900, 600);
        if (datos != null) poblarStackedBarChart(chart, datos);
        return chart;
    }

    // ─────────────────────────────────────
    // POBLAR GRÁFICOS CON DATOS
    // ─────────────────────────────────────
    private void poblarLineChart(LineChart<String, Number> chart, Map<String, Double> datos) {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("Saldo");
        datos.forEach((fecha, valor) -> serie.getData().add(new XYChart.Data<>(fecha, valor)));
        chart.getData().clear();
        chart.getData().add(serie);
    }

    private void poblarPieChart(PieChart chart, Map<String, Double> datos) {
        chart.getData().clear();
        datos.forEach((nombre, importe) ->
            chart.getData().add(new PieChart.Data(
                nombre + " (" + Formato.moneda(importe) + ")", importe)));
    }

    private void poblarBarChart(BarChart<String, Number> chart, Map<String, double[]> datos) {
        XYChart.Series<String, Number> serieIngresos = new XYChart.Series<>();
        serieIngresos.setName("Ingresos");
        XYChart.Series<String, Number> serieGastos = new XYChart.Series<>();
        serieGastos.setName("Gastos");
        datos.forEach((clave, valores) -> {
            serieIngresos.getData().add(new XYChart.Data<>(clave, valores[0]));
            serieGastos.getData().add(new XYChart.Data<>(clave, valores[1]));
        });
        chart.getData().clear();
        chart.getData().addAll(serieIngresos, serieGastos);
    }

    private void poblarStackedBarChart(StackedBarChart<String, Number> chart,
                                       Map<String, Map<String, Double>> datos) {
        chart.getData().clear();
        datos.forEach((cat, meses) -> {
            XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.setName(cat);
            meses.forEach((mes, importe) ->
                serie.getData().add(new XYChart.Data<>(mes, importe)));
            chart.getData().add(serie);
        });
    }

    // ─────────────────────────────────────
    // RECARGAR
    // ─────────────────────────────────────
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

        // Guardar datos actuales para maximizar
        datosLineaActuales   = informeService.evolucionSaldoPorFecha(desde);
        datosTartaActuales   = informeService.gastosPorCategoria(desde);
        datosBarrasActuales  = informeService.evolucionAgrupada(periodo);
        datosApiladoActuales = informeService.gastosMensualesPorCategoria(periodo);

        // Poblar gráficos del dashboard
        poblarLineChart(graficoLineas, datosLineaActuales);
        poblarPieChart(graficoTarta, datosTartaActuales);
        poblarBarChart(graficoBarras, datosBarrasActuales);
        poblarStackedBarChart(graficoApilado, datosApiladoActuales);
    }
}