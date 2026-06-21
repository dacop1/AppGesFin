package com.dacarex.capital.vista.paneles;

import com.dacarex.capital.modelo.Movimiento;
import com.dacarex.capital.service.InformeService;
import com.dacarex.capital.service.MovimientoService;
import com.dacarex.capital.util.ExportadorCSV;
import com.dacarex.capital.util.Formato;
import com.dacarex.capital.vista.componentes.IconoFX;
import com.dacarex.capital.vista.componentes.TarjetaKPI;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PanelInformes extends BorderPane {

    private final InformeService informeService = new InformeService();
    private final MovimientoService movimientoService = new MovimientoService();

    private ComboBox<String> cmbVista;
    private PieChart graficoTarta;

    private TarjetaKPI tarjetaMayorIngreso;
    private TarjetaKPI tarjetaMayorGasto;
    private TarjetaKPI tarjetaCategoria;
    private TarjetaKPI tarjetaPromedio;

    public PanelInformes() {
        setPadding(new Insets(25, 30, 25, 30));
        construirContenido();
    }

    private void construirContenido() {
        Label lblTitulo = new Label("Informes");
        lblTitulo.getStyleClass().add("titulo-pagina");

        cmbVista = new ComboBox<>(FXCollections.observableArrayList(
            "Gastos por categoria", "Ingresos por categoria"));
        cmbVista.setValue("Gastos por categoria");
        cmbVista.setPrefWidth(190);
        cmbVista.setOnAction(e -> recargar());

        Button btnExportar = new Button("Exportar CSV");
        btnExportar.getStyleClass().addAll("btn", "btn-aviso");
        btnExportar.setOnAction(e -> exportarCSV());

        HBox acciones = new HBox(10, cmbVista, btnExportar);
        acciones.setAlignment(Pos.CENTER_RIGHT);

        BorderPane cabecera = new BorderPane();
        cabecera.setLeft(lblTitulo);
        cabecera.setRight(acciones);
        cabecera.setPadding(new Insets(0, 0, 20, 0));

        setTop(cabecera);

        tarjetaMayorIngreso = new TarjetaKPI(
            IconoFX.crear(IconoFX.Tipo.FLECHA_ARRIBA, Color.web("#28c76f"), 22),
            "Mayor ingreso", "-");
        tarjetaMayorGasto = new TarjetaKPI(
            IconoFX.crear(IconoFX.Tipo.FLECHA_ABAJO, Color.web("#f05252"), 22),
            "Mayor gasto", "-");
        tarjetaCategoria = new TarjetaKPI(
            IconoFX.crear(IconoFX.Tipo.ETIQUETA, Color.web("#17a2b8"), 22),
            "Categoria mas activa", "-");
        tarjetaPromedio = new TarjetaKPI(
            IconoFX.crear(IconoFX.Tipo.GRAFICO, Color.web("#ff9f43"), 22),
            "Promedio gasto/mes", "-");

        HBox filaTarjetas = new HBox(18, tarjetaMayorIngreso, tarjetaMayorGasto, tarjetaCategoria, tarjetaPromedio);
        for (var tarjeta : filaTarjetas.getChildren()) {
            HBox.setHgrow(tarjeta, Priority.ALWAYS);
        }
        filaTarjetas.setPadding(new Insets(0, 0, 20, 0));

        graficoTarta = new PieChart();
        graficoTarta.getStyleClass().add("chart-card");
        graficoTarta.setAnimated(false);
        VBox.setVgrow(graficoTarta, Priority.ALWAYS);

        VBox centro = new VBox(0, filaTarjetas, graficoTarta);
        setCenter(centro);

        recargar();
    }

    public void recargar() {
        boolean esGastos = "Gastos por categoria".equals(cmbVista.getValue());

        Map<String, Double> datos = esGastos
            ? informeService.gastosPorCategoria()
            : informeService.ingresosPorCategoria();

        graficoTarta.getData().clear();
        datos.forEach((nombre, importe) ->
            graficoTarta.getData().add(
                new PieChart.Data(nombre + " (" + Formato.moneda(importe) + ")", importe)));

        Optional<Movimiento> mayorIngreso = informeService.mayorIngreso();
        Optional<Movimiento> mayorGasto    = informeService.mayorGasto();

        tarjetaMayorIngreso.actualizar(
            mayorIngreso.map(m -> Formato.moneda(m.getImporte())).orElse("-"), "#28c76f");

        tarjetaMayorGasto.actualizar(
            mayorGasto.map(m -> Formato.moneda(m.getImporte())).orElse("-"), "#f05252");

        tarjetaCategoria.actualizar(informeService.categoriaMasActiva(), "#17a2b8");

        tarjetaPromedio.actualizar(
            Formato.moneda(informeService.promedioGastoMensual()), "#ff9f43");
    }

    private void exportarCSV() {
        try {
            List<Movimiento> todos = movimientoService.obtenerTodos();
            if (todos.isEmpty()) {
                mostrarAviso("No hay movimientos para exportar.");
                return;
            }
            String ruta = ExportadorCSV.exportar(todos);
            mostrarAviso("Exportado en:\n" + ruta);
        } catch (IOException ex) {
            mostrarAviso("Error al exportar: " + ex.getMessage());
        }
    }

    private void mostrarAviso(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION, mensaje, ButtonType.OK);
        alerta.showAndWait();
    }
}