package com.dacarex.capital.vista.paneles;

import com.dacarex.capital.enums.TipoMovimiento;
import com.dacarex.capital.exception.ValidacionException;
import com.dacarex.capital.modelo.Categoria;
import com.dacarex.capital.modelo.Movimiento;
import com.dacarex.capital.service.CategoriaService;
import com.dacarex.capital.service.MovimientoService;
import com.dacarex.capital.util.ExportadorCSV;
import com.dacarex.capital.util.Formato;
import com.dacarex.capital.util.TemaManager;
import com.dacarex.capital.vista.componentes.IconoFX;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class PanelMovimientos extends BorderPane {

    private final MovimientoService movimientoService = new MovimientoService();
    private final CategoriaService categoriaService = new CategoriaService();

    private TableView<Movimiento> tabla;
    private TextField txtBuscar;
    private ComboBox<String> cmbTipo;
    private ComboBox<Categoria> cmbCategoriaFiltro;
    private DatePicker fechaDesde;
    private DatePicker fechaHasta;
    private Label lblSaldo;

    public PanelMovimientos() {
        setPadding(new Insets(25, 30, 25, 30));
        construirContenido();
    }

    private void construirContenido() {
        // ── CABECERA ──
        Label lblTitulo = new Label("Movimientos");
        lblTitulo.getStyleClass().add("titulo-pagina");

        lblSaldo = new Label("Saldo: 0,00 €");
        lblSaldo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        BorderPane cabecera = new BorderPane();
        cabecera.setLeft(lblTitulo);
        cabecera.setRight(lblSaldo);
        cabecera.setPadding(new Insets(0, 0, 15, 0));

        setTop(cabecera);

        // ── FILTROS (fila 1: texto + tipo + categoria) ──
        txtBuscar = new TextField();
        txtBuscar.setPromptText("Buscar por descripcion...");
        txtBuscar.getStyleClass().add("login-campo");
        txtBuscar.setPrefWidth(220);
        txtBuscar.setOnAction(e -> filtrar());

        cmbTipo = new ComboBox<>(FXCollections.observableArrayList("Todos", "INGRESO", "GASTO"));
        cmbTipo.setValue("Todos");

        cmbCategoriaFiltro = new ComboBox<>();
        actualizarComboCategoriaFiltro();
        cmbCategoriaFiltro.setConverter(new StringConverter<>() {
            @Override
            public String toString(Categoria c) {
                return (c == null || c.getId() == 0) ? "Todas las categorias" : c.getNombre();
            }
            @Override
            public Categoria fromString(String s) { return null; }
        });

        HBox filaFiltros1 = new HBox(10, txtBuscar, cmbTipo, cmbCategoriaFiltro);
        filaFiltros1.setAlignment(Pos.CENTER_LEFT);

        // ── FILTROS (fila 2: fechas + botones) ──
        fechaDesde = new DatePicker();
        fechaDesde.getEditor().setPromptText("Desde");
        fechaDesde.setPrefWidth(140);

        fechaHasta = new DatePicker();
        fechaHasta.getEditor().setPromptText("Hasta");
        fechaHasta.setPrefWidth(140);

        Button btnFiltrar = new Button("Filtrar");
        btnFiltrar.getStyleClass().addAll("btn", "btn-primario");
        btnFiltrar.setOnAction(e -> filtrar());

        Button btnLimpiar = new Button("Limpiar filtros");
        btnLimpiar.getStyleClass().addAll("btn");
        btnLimpiar.setStyle("-fx-background-color: #8a8aa0;");
        btnLimpiar.setOnAction(e -> limpiarFiltros());

        Button btnNuevo = new Button("+ Nuevo");
        btnNuevo.setGraphic(IconoFX.crear(IconoFX.Tipo.MAS, Color.WHITE, 12));
        btnNuevo.getStyleClass().addAll("btn", "btn-exito");
        btnNuevo.setOnAction(e -> abrirFormulario(null));

        Button btnExportar = new Button("Exportar CSV");
        btnExportar.getStyleClass().addAll("btn", "btn-aviso");
        btnExportar.setOnAction(e -> exportarCSV());

        HBox filaFiltros2 = new HBox(10, fechaDesde, fechaHasta, btnFiltrar, btnLimpiar, btnNuevo, btnExportar);
        filaFiltros2.setAlignment(Pos.CENTER_LEFT);
        filaFiltros2.setPadding(new Insets(8, 0, 12, 0));

        VBox panelFiltros = new VBox(8, filaFiltros1, filaFiltros2);

        // ── TABLA ──
        tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.setPlaceholder(new Label("No hay movimientos registrados"));

        TableColumn<Movimiento, LocalDate> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getFecha()));
        colFecha.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });

        TableColumn<Movimiento, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTipo().getValor()));

        TableColumn<Movimiento, String> colDesc = new TableColumn<>("Descripcion");
        colDesc.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDescripcion()));

        TableColumn<Movimiento, Double> colImporte = new TableColumn<>("Importe");
        colImporte.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().getImporte()).asObject());
        colImporte.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : Formato.moneda(item));
            }
        });

        TableColumn<Movimiento, String> colCategoria = new TableColumn<>("Categoria");
        colCategoria.setCellValueFactory(d -> new SimpleStringProperty(
            d.getValue().getCategoria() != null ? d.getValue().getCategoria().getNombre() : "-"));

        TableColumn<Movimiento, String> colNotas = new TableColumn<>("Notas");
        colNotas.setCellValueFactory(d -> new SimpleStringProperty(
            d.getValue().getNotas() != null ? d.getValue().getNotas() : ""));

        TableColumn<Movimiento, Void> colAcciones = new TableColumn<>("Acciones");
        colAcciones.setSortable(false);
        colAcciones.setMinWidth(90);
        colAcciones.setMaxWidth(90);
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button();
            private final Button btnEliminar = new Button();
            private final HBox caja = new HBox(8, btnEditar, btnEliminar);

            {
                btnEditar.setGraphic(IconoFX.crear(IconoFX.Tipo.LAPIZ, Color.web("#5e7cff"), 14));
                btnEditar.getStyleClass().add("accion-icono");
                btnEditar.setOnAction(e -> abrirFormulario(getTableView().getItems().get(getIndex())));

                btnEliminar.setGraphic(IconoFX.crear(IconoFX.Tipo.PAPELERA, Color.web("#f05252"), 14));
                btnEliminar.getStyleClass().add("accion-icono");
                btnEliminar.setOnAction(e -> eliminar(getTableView().getItems().get(getIndex())));

                caja.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : caja);
            }
        });

        tabla.getColumns().addAll(colFecha, colTipo, colDesc, colImporte, colCategoria, colNotas, colAcciones);

        VBox centro = new VBox(0, panelFiltros, tabla);
        VBox.setVgrow(tabla, Priority.ALWAYS);

        setCenter(centro);

        recargar();
    }

    private void actualizarComboCategoriaFiltro() {
        Categoria todas = new Categoria();
        todas.setNombre("Todas las categorias");

        ObservableList<Categoria> items = FXCollections.observableArrayList();
        items.add(todas);
        items.addAll(categoriaService.obtenerTodas());

        Categoria seleccionActual = cmbCategoriaFiltro.getValue();
        cmbCategoriaFiltro.setItems(items);
        cmbCategoriaFiltro.setValue(seleccionActual != null ? seleccionActual : todas);
    }

    public void recargar() {
        actualizarComboCategoriaFiltro();
        cargarTabla(movimientoService.obtenerTodos());
        actualizarSaldo();
    }

    private void cargarTabla(List<Movimiento> lista) {
        ObservableList<Movimiento> datos = FXCollections.observableArrayList(lista);
        tabla.setItems(datos);
    }

    private void actualizarSaldo() {
        double saldo = movimientoService.calcularSaldo();
        lblSaldo.setText("Saldo: " + Formato.moneda(saldo));
        lblSaldo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: "
            + (saldo >= 0 ? "#28c76f" : "#f05252") + ";");
    }

    private void filtrar() {
        String texto = txtBuscar.getText().trim();
        TipoMovimiento tipo = "Todos".equals(cmbTipo.getValue())
            ? null : TipoMovimiento.valueOf(cmbTipo.getValue());

        Categoria categoria = cmbCategoriaFiltro.getValue();
        if (categoria != null && categoria.getId() == 0) categoria = null;

        LocalDate desde = fechaDesde.getValue();
        LocalDate hasta = fechaHasta.getValue();

        List<Movimiento> resultado = movimientoService.filtrarAvanzado(tipo, categoria, desde, hasta, texto);
        cargarTabla(resultado);
    }

    private void limpiarFiltros() {
        txtBuscar.clear();
        cmbTipo.setValue("Todos");
        actualizarComboCategoriaFiltro();
        fechaDesde.setValue(null);
        fechaHasta.setValue(null);
        cargarTabla(movimientoService.obtenerTodos());
    }

    private void abrirFormulario(Movimiento existente) {
        boolean esEdicion = existente != null;

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(esEdicion ? "Editar Movimiento" : "Nuevo Movimiento");
        dialog.getDialogPane().getStylesheets().add(TemaManager.getHojaEstilos());
        dialog.getDialogPane().getStyleClass().add("dialogo");

        ButtonType btnGuardarTipo = new ButtonType(
            esEdicion ? "Guardar cambios" : "Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardarTipo, ButtonType.CANCEL);

        VBox contenido = new VBox(8);
        contenido.setPadding(new Insets(10));
        contenido.setPrefWidth(340);

        ComboBox<TipoMovimiento> cmbTipoMov = new ComboBox<>(
            FXCollections.observableArrayList(TipoMovimiento.values()));
        cmbTipoMov.setValue(esEdicion ? existente.getTipo() : TipoMovimiento.GASTO);
        cmbTipoMov.setMaxWidth(Double.MAX_VALUE);

        TextField txtDesc = new TextField();
        txtDesc.setPromptText("Descripcion");
        if (esEdicion) txtDesc.setText(existente.getDescripcion());

        TextField txtImporte = new TextField();
        txtImporte.setPromptText("Importe (ej: 49.99)");
        if (esEdicion) txtImporte.setText(String.valueOf(existente.getImporte()));

        List<Categoria> categorias = categoriaService.obtenerTodas();
        ComboBox<Categoria> cmbCat = new ComboBox<>(FXCollections.observableArrayList(categorias));
        cmbCat.setMaxWidth(Double.MAX_VALUE);
        cmbCat.setConverter(new StringConverter<>() {
            @Override public String toString(Categoria c) { return c == null ? "" : c.getNombre(); }
            @Override public Categoria fromString(String s) { return null; }
        });
        if (esEdicion) {
            categorias.stream()
                .filter(c -> c.getId() == existente.getCategoria().getId())
                .findFirst()
                .ifPresent(cmbCat::setValue);
        } else if (!categorias.isEmpty()) {
            cmbCat.setValue(categorias.get(0));
        }

        DatePicker fechaPicker = new DatePicker(esEdicion ? existente.getFecha() : LocalDate.now());
        fechaPicker.setMaxWidth(Double.MAX_VALUE);

        TextField txtNotas = new TextField();
        txtNotas.setPromptText("Notas (opcional)");
        if (esEdicion && existente.getNotas() != null) txtNotas.setText(existente.getNotas());

        Label lblError = new Label(" ");
        lblError.getStyleClass().add("login-error");

        contenido.getChildren().addAll(
            new Label("Tipo:"), cmbTipoMov,
            new Label("Descripcion:"), txtDesc,
            new Label("Importe (€):"), txtImporte,
            new Label("Categoria:"), cmbCat,
            new Label("Fecha:"), fechaPicker,
            new Label("Notas:"), txtNotas,
            lblError
        );

        dialog.getDialogPane().setContent(contenido);

        Button btnGuardar = (Button) dialog.getDialogPane().lookupButton(btnGuardarTipo);
        btnGuardar.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                TipoMovimiento tipo = cmbTipoMov.getValue();
                String desc = txtDesc.getText().trim();
                double importe = Double.parseDouble(txtImporte.getText().trim().replace(",", "."));
                Categoria cat = cmbCat.getValue();
                LocalDate fecha = fechaPicker.getValue();
                String notas = txtNotas.getText().trim();

                if (cat == null) {
                    lblError.setText("Debes seleccionar una categoria.");
                    event.consume();
                    return;
                }

                if (esEdicion) {
                    movimientoService.actualizar(existente, tipo, desc, importe, cat, fecha, notas);
                } else {
                    movimientoService.crear(tipo, desc, importe, cat, fecha, notas);
                }
                recargar();

            } catch (NumberFormatException ex) {
                lblError.setText("El importe debe ser un numero valido.");
                event.consume();
            } catch (ValidacionException ex) {
                lblError.setText(String.join(" | ", ex.getErrores()));
                event.consume();
            }
        });

        dialog.showAndWait();
    }

    private void eliminar(Movimiento movimiento) {
        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
            "¿Eliminar este movimiento?", ButtonType.YES, ButtonType.NO);
        confirmar.getDialogPane().getStylesheets().add(TemaManager.getHojaEstilos());
        confirmar.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.YES) {
                movimientoService.eliminar(movimiento.getId());
                recargar();
            }
        });
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
        alerta.getDialogPane().getStylesheets().add(TemaManager.getHojaEstilos());
        alerta.showAndWait();
    }
}