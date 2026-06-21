package com.dacarex.capital.vista.componentes;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class TarjetaKPI extends VBox {

    private final Label lblValor;

    public TarjetaKPI(String icono, String titulo, String valorInicial) {
        getStyleClass().add("kpi-card");
        setSpacing(6);
        setAlignment(Pos.CENTER_LEFT);
        setPrefWidth(220);

        Label lblIcono = new Label(icono);
        lblIcono.setStyle("-fx-font-size: 20px;");

        Label lblTitulo = new Label(titulo);
        lblTitulo.getStyleClass().add("kpi-titulo");

        lblValor = new Label(valorInicial);
        lblValor.getStyleClass().add("kpi-valor");

        getChildren().addAll(lblIcono, lblTitulo, lblValor);
    }

    public void actualizar(String valor, String colorHex) {
        lblValor.setText(valor);
        lblValor.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");
    }
}