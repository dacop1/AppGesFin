package com.dacarex.capital.vista;

import com.dacarex.capital.modelo.Usuario;
import com.dacarex.capital.service.AuthService;
import com.dacarex.capital.util.TemaManager;
import com.dacarex.capital.vista.componentes.IconoFX;
import com.dacarex.capital.vista.paneles.PanelCategorias;
import com.dacarex.capital.vista.paneles.PanelDashboard;
import com.dacarex.capital.vista.paneles.PanelInformes;
import com.dacarex.capital.vista.paneles.PanelMovimientos;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.Map;

public class VentanaPrincipal {

    private final Usuario usuarioActual;
    private final AuthService authService;
    private Stage stage;

    private final Map<String, Region> paneles = new LinkedHashMap<>();
    private final Map<String, Button> botonesNav = new LinkedHashMap<>();
    private final StackPane contenedorPaneles = new StackPane();

    private PanelDashboard panelDashboard;
    private PanelMovimientos panelMovimientos;
    private PanelCategorias panelCategorias;
    private PanelInformes panelInformes;

    private String panelActivo = "dashboard";
    private Button btnTema;
    private Scene escena;

    public VentanaPrincipal(Usuario usuario, AuthService authService) {
        this.usuarioActual = usuario;
        this.authService = authService;
    }

    public void mostrar(Stage stage) {
        this.stage = stage;
        stage.setTitle("Dacarex Capital — " + usuarioActual.getNombre());

        BorderPane raiz = new BorderPane();
        raiz.setLeft(crearSidebar());
        raiz.setCenter(construirContenido());

        escena = new Scene(raiz);
        aplicarHojaEstilos();

        stage.setScene(escena);
        stage.setMaximized(true);
        stage.show();

        mostrarPanel(panelActivo);
    }

    private StackPane construirContenido() {
        panelDashboard   = new PanelDashboard();
        panelMovimientos = new PanelMovimientos();
        panelCategorias  = new PanelCategorias();
        panelInformes    = new PanelInformes();

        paneles.put("dashboard", panelDashboard);
        paneles.put("movimientos", panelMovimientos);
        paneles.put("categorias", panelCategorias);
        paneles.put("informes", panelInformes);

        contenedorPaneles.getChildren().addAll(paneles.values());
        return contenedorPaneles;
    }

    private VBox crearSidebar() {
        VBox sb = new VBox(4);
        sb.getStyleClass().add("sidebar");
        sb.setPrefWidth(220);
        sb.setMinWidth(220);
        sb.setPadding(new Insets(20, 0, 20, 0));

        StackPane iconoLogo = crearIconoMoneda(16, 1.8);
        Label lblLogo = new Label("Dacarex");
        lblLogo.getStyleClass().add("sidebar-logo");
        HBox cajaLogo = new HBox(8, iconoLogo, lblLogo);
        cajaLogo.setAlignment(Pos.CENTER_LEFT);
        cajaLogo.setPadding(new Insets(0, 0, 4, 20));
        sb.getChildren().add(cajaLogo);

        Label lblUsuario = new Label(usuarioActual.getNombre());
        lblUsuario.getStyleClass().add("sidebar-usuario");
        lblUsuario.setPadding(new Insets(0, 0, 18, 22));
        sb.getChildren().add(lblUsuario);

        agregarBotonNav(sb, "dashboard",   "Dashboard",   IconoFX.Tipo.CASA);
        agregarBotonNav(sb, "movimientos", "Movimientos", IconoFX.Tipo.BILLETERA);
        agregarBotonNav(sb, "categorias",  "Categorias",  IconoFX.Tipo.ETIQUETA);
        agregarBotonNav(sb, "informes",    "Informes",    IconoFX.Tipo.GRAFICO);

        Region espaciador = new Region();
        VBox.setVgrow(espaciador, Priority.ALWAYS);
        sb.getChildren().add(espaciador);

        btnTema = new Button(TemaManager.isModoOscuro() ? "Modo claro" : "Modo oscuro");
        btnTema.setGraphic(IconoFX.crear(
            TemaManager.isModoOscuro() ? IconoFX.Tipo.SOL : IconoFX.Tipo.LUNA,
            Color.web("#bebed2"), 15));
        btnTema.getStyleClass().add("sidebar-link");
        btnTema.setMaxWidth(Double.MAX_VALUE);
        btnTema.setOnAction(e -> cambiarTema());
        sb.getChildren().add(btnTema);

        Button btnSalir = new Button("Cerrar sesion");
        btnSalir.setGraphic(IconoFX.crear(IconoFX.Tipo.SALIR, Color.web("#ff8282"), 15));
        btnSalir.getStyleClass().add("sidebar-link");
        btnSalir.setStyle("-fx-text-fill: #ff8282;");
        btnSalir.setMaxWidth(Double.MAX_VALUE);
        btnSalir.setOnAction(e -> {
            authService.logout();
            new VentanaLogin().mostrar(stage);
        });
        sb.getChildren().add(btnSalir);

        return sb;
    }

    private StackPane crearIconoMoneda(double radio, double grosor) {
        Circle circulo = new Circle(radio);
        circulo.setFill(Color.TRANSPARENT);
        circulo.setStroke(Color.web("#5e7cff"));
        circulo.setStrokeWidth(grosor);

        Text simbolo = new Text("$");
        simbolo.setFont(Font.font("Segoe UI", FontWeight.BOLD, radio * 1.1));
        simbolo.setFill(Color.web("#5e7cff"));

        StackPane stack = new StackPane(circulo, simbolo);
        stack.setPrefSize(radio * 2 + 4, radio * 2 + 4);
        return stack;
    }

    private void agregarBotonNav(VBox sb, String clave, String texto, IconoFX.Tipo tipoIcono) {
        Button btn = new Button(texto);
        btn.setGraphic(IconoFX.crear(tipoIcono, Color.WHITE, 16));
        btn.getStyleClass().add("nav-button");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> mostrarPanel(clave));
        botonesNav.put(clave, btn);
        sb.getChildren().add(btn);

        VBox.setMargin(btn, new Insets(0, 10, 0, 10));
    }

    private void resaltarBotonActivo() {
        botonesNav.forEach((clave, btn) -> {
            btn.getStyleClass().removeAll("nav-button", "nav-button-activo");
            btn.getStyleClass().add(clave.equals(panelActivo) ? "nav-button-activo" : "nav-button");
        });
    }

    public void mostrarPanel(String clave) {
        panelActivo = clave;

        paneles.forEach((k, nodo) -> {
            boolean activo = k.equals(clave);
            nodo.setVisible(activo);
            nodo.setManaged(activo);
        });

        switch (clave) {
            case "dashboard"   -> panelDashboard.recargar();
            case "movimientos" -> panelMovimientos.recargar();
            case "categorias"  -> panelCategorias.recargar();
            case "informes"    -> panelInformes.recargar();
        }

        resaltarBotonActivo();
    }

    private void cambiarTema() {
        TemaManager.toggle();
        btnTema.setText(TemaManager.isModoOscuro() ? "Modo claro" : "Modo oscuro");
        btnTema.setGraphic(IconoFX.crear(
            TemaManager.isModoOscuro() ? IconoFX.Tipo.SOL : IconoFX.Tipo.LUNA,
            Color.web("#bebed2"), 15));
        aplicarHojaEstilos();
    }

    private void aplicarHojaEstilos() {
        escena.getStylesheets().clear();
        escena.getStylesheets().add(TemaManager.getHojaEstilos());
    }
}