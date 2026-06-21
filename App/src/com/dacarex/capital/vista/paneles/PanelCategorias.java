package com.dacarex.capital.vista.paneles;

import com.dacarex.capital.enums.TipoMovimiento;
import com.dacarex.capital.exception.ValidacionException;
import com.dacarex.capital.modelo.Categoria;
import com.dacarex.capital.service.CategoriaService;
import com.dacarex.capital.service.MovimientoService;
import com.dacarex.capital.util.TemaManager;
import com.dacarex.capital.vista.componentes.IconoFX;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.List;

public class PanelCategorias extends BorderPane {

    private final CategoriaService categoriaService = new CategoriaService();
    private final MovimientoService movimientoService = new MovimientoService();

    private final String[] paletaHex = {
        "#5e7cff", "#28c76f", "#ff9f43", "#f05252",
        "#17a2b8", "#9b59b6", "#f1c40f", "#3498db"
    };

    private TableView<Categoria> tabla;

    public PanelCategorias() {
        setPadding(new Insets(25, 30, 25, 30));
        construirContenido();
    }

    private void construirContenido() {
        Label lblTitulo = new Label("Categorias");
        lblTitulo.getStyleClass().add("titulo-pagina");
        BorderPane.setMargin(lblTitulo, new Insets(0, 0, 15, 0));
        setTop(lblTitulo);

        Button btnNueva = new Button("+ Nueva categoria");
        btnNueva.setGraphic(IconoFX.crear(IconoFX.Tipo.MAS, Color.WHITE, 12));
        btnNueva.getStyleClass().addAll("btn", "btn-exito");
        btnNueva.setOnAction(e -> abrirFormulario(null));

        HBox botones = new HBox(10, btnNueva);
        botones.setAlignment(Pos.CENTER_LEFT);
        botones.setPadding(new Insets(0, 0, 12, 0));

        tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.setPlaceholder(new Label("No hay categorias registradas"));

        TableColumn<Categoria, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombre()));

        TableColumn<Categoria, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTipo().getValor()));

        TableColumn<Categoria, Void> colAcciones = new TableColumn<>("Acciones");
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

        tabla.getColumns().addAll(colNombre, colTipo, colAcciones);

        VBox centro = new VBox(0, botones, tabla);
        VBox.setVgrow(tabla, Priority.ALWAYS);
        setCenter(centro);

        recargar();
    }

    public void recargar() {
        List<Categoria> lista = categoriaService.obtenerTodas();
        ObservableList<Categoria> datos = FXCollections.observableArrayList(lista);
        tabla.setItems(datos);
    }

    private void abrirFormulario(Categoria existente) {
        boolean esEdicion = existente != null;

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(esEdicion ? "Editar Categoria" : "Nueva Categoria");
        dialog.getDialogPane().getStylesheets().add(TemaManager.getHojaEstilos());
        dialog.getDialogPane().getStyleClass().add("dialogo");

        ButtonType btnGuardarTipo = new ButtonType(
            esEdicion ? "Guardar cambios" : "Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnGuardarTipo, ButtonType.CANCEL);

        VBox contenido = new VBox(8);
        contenido.setPadding(new Insets(10));
        contenido.setPrefWidth(300);

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre de la categoria");
        if (esEdicion) txtNombre.setText(existente.getNombre());

        ComboBox<TipoMovimiento> cmbTipo = new ComboBox<>(
            FXCollections.observableArrayList(TipoMovimiento.values()));
        cmbTipo.setValue(esEdicion ? existente.getTipo() : TipoMovimiento.GASTO);
        cmbTipo.setMaxWidth(Double.MAX_VALUE);

        Label lblError = new Label(" ");
        lblError.getStyleClass().add("login-error");

        contenido.getChildren().addAll(
            new Label("Nombre:"), txtNombre,
            new Label("Tipo:"), cmbTipo,
            lblError
        );

        dialog.getDialogPane().setContent(contenido);

        Button btnGuardar = (Button) dialog.getDialogPane().lookupButton(btnGuardarTipo);
        btnGuardar.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                String nombre = txtNombre.getText().trim();
                TipoMovimiento tipo = cmbTipo.getValue();

                if (esEdicion) {
                    categoriaService.actualizar(existente, nombre, tipo);
                } else {
                    int totalActual = categoriaService.obtenerTodas().size();
                    String colorHex = paletaHex[totalActual % paletaHex.length];
                    categoriaService.crear(nombre, tipo, colorHex);
                }
                recargar();

            } catch (ValidacionException ex) {
                lblError.setText(String.join(" | ", ex.getErrores()));
                event.consume();
            }
        });

        dialog.showAndWait();
    }

    private void eliminar(Categoria categoria) {
        if (movimientoService.categoriaEnUso(categoria.getId())) {
            Alert aviso = new Alert(Alert.AlertType.WARNING,
                "No se puede eliminar esta categoria porque tiene movimientos asociados.",
                ButtonType.OK);
            aviso.getDialogPane().getStylesheets().add(TemaManager.getHojaEstilos());
            aviso.showAndWait();
            return;
        }

        Alert confirmar = new Alert(Alert.AlertType.CONFIRMATION,
            "¿Eliminar esta categoria?", ButtonType.YES, ButtonType.NO);
        confirmar.getDialogPane().getStylesheets().add(TemaManager.getHojaEstilos());
        confirmar.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.YES) {
                categoriaService.eliminar(categoria.getId());
                recargar();
            }
        });
    }
}