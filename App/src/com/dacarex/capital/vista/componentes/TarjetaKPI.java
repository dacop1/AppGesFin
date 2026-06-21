package com.dacarex.capital.vista.componentes;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class TarjetaKPI extends VBox {

    private final Label lblValor;

    public TarjetaKPI(Node icono, String titulo, String valorInicial) {
        getStyleClass().add("kpi-card");
        setSpacing(8);
        setAlignment(Pos.CENTER_LEFT);
        setPrefWidth(220);

        StackPane cajaIcono = new StackPane(icono);
        cajaIcono.setPrefSize(26, 26);
        cajaIcono.setAlignment(Pos.CENTER_LEFT);

        Label lblTitulo = new Label(titulo);
        lblTitulo.getStyleClass().add("kpi-titulo");

        lblValor = new Label(valorInicial);
        lblValor.getStyleClass().add("kpi-valor");

        getChildren().addAll(cajaIcono, lblTitulo, lblValor);
    }

    public void actualizar(String valor, String colorHex) {
        lblValor.setText(valor);
        lblValor.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");
    }
}