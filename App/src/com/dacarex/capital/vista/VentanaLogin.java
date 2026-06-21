package com.dacarex.capital.vista;

import com.dacarex.capital.enums.TipoCuenta;
import com.dacarex.capital.exception.AutenticacionException;
import com.dacarex.capital.exception.ValidacionException;
import com.dacarex.capital.modelo.Usuario;
import com.dacarex.capital.service.AuthService;
import com.dacarex.capital.util.TemaManager;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class VentanaLogin {

    private final AuthService authService = new AuthService();
    private Stage stage;

    private TextField txtEmail;
    private PasswordField txtPassword;
    private Label lblEstado;

    public void mostrar(Stage stage) {
        this.stage = stage;
        stage.setTitle("Dacarex Capital — Acceso");

        StackPane fondo = new StackPane();
        fondo.getStyleClass().add("login-fondo");
        fondo.getChildren().add(construirTarjeta());

        Scene escena = new Scene(fondo);
        escena.getStylesheets().add(TemaManager.getHojaEstilos());

        stage.setScene(escena);
        stage.setMaximized(true);
        stage.show();
    }

    private VBox construirTarjeta() {
        VBox tarjeta = new VBox(14);
        tarjeta.getStyleClass().add("login-card");
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setMaxWidth(380);
        tarjeta.setMaxHeight(540);
        tarjeta.setPadding(new Insets(40));

        StackPane icono = crearIconoMoneda();

        Label lblTitulo = new Label("DACAREX CAPITAL");
        lblTitulo.getStyleClass().add("login-titulo");

        Label lblSub = new Label("Gestion financiera inteligente");
        lblSub.getStyleClass().add("login-subtitulo");

        VBox campoEmail = crearCampo("Email", false);
        txtEmail = (TextField) campoEmail.getChildren().get(1);

        VBox campoPass = crearCampo("Contrasenia", true);
        txtPassword = (PasswordField) campoPass.getChildren().get(1);

        lblEstado = new Label(" ");
        lblEstado.getStyleClass().add("login-error");

        Button btnLogin = new Button("Entrar");
        btnLogin.getStyleClass().addAll("btn", "btn-primario");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setOnAction(e -> intentarLogin());

        Hyperlink linkRegistro = new Hyperlink("Crear una cuenta nueva");
        linkRegistro.getStyleClass().add("login-link");
        linkRegistro.setOnAction(e -> abrirDialogoRegistro());

        txtPassword.setOnAction(e -> intentarLogin());

        tarjeta.getChildren().addAll(
            icono, lblTitulo, lblSub,
            campoEmail, campoPass,
            lblEstado, btnLogin, linkRegistro
        );

        return tarjeta;
    }

    private StackPane crearIconoMoneda() {
        Circle circulo = new Circle(26);
        circulo.setFill(Color.TRANSPARENT);
        circulo.setStroke(Color.web("#5e7cff"));
        circulo.setStrokeWidth(2.5);

        Text simbolo = new Text("$");
        simbolo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        simbolo.setFill(Color.web("#5e7cff"));

        StackPane stack = new StackPane(circulo, simbolo);
        stack.setAlignment(Pos.CENTER);
        return stack;
    }

    private VBox crearCampo(String etiqueta, boolean esPassword) {
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setMaxWidth(290);

        Label lbl = new Label(etiqueta);
        lbl.getStyleClass().add("login-campo-label");

        TextField campo = esPassword ? new PasswordField() : new TextField();
        campo.getStyleClass().add("login-campo");
        campo.setPrefWidth(290);

        box.getChildren().addAll(lbl, campo);
        return box;
    }

    private void intentarLogin() {
        String email = txtEmail.getText().trim();
        String pass  = txtPassword.getText();

        if (email.isEmpty() || pass.isEmpty()) {
            lblEstado.setText("Rellena todos los campos.");
            return;
        }

        try {
            Usuario usuario = authService.login(email, pass);
            new VentanaPrincipal(usuario, authService).mostrar(stage);
        } catch (AutenticacionException ex) {
            lblEstado.setText(ex.getMessage());
        }
    }

    private void abrirDialogoRegistro() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Crear cuenta");
        dialog.getDialogPane().getStylesheets().add(TemaManager.getHojaEstilos());
        dialog.getDialogPane().getStyleClass().add("dialogo");

        ButtonType btnCrearTipo = new ButtonType("Crear cuenta", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnCrearTipo, ButtonType.CANCEL);

        VBox contenido = new VBox(8);
        contenido.setPadding(new Insets(10));
        contenido.setPrefWidth(320);

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre completo");

        TextField txtEmailReg = new TextField();
        txtEmailReg.setPromptText("Email");

        PasswordField txtPassReg = new PasswordField();
        txtPassReg.setPromptText("Contrasenia (min. 6 caracteres)");

        ComboBox<TipoCuenta> cmbTipo = new ComboBox<>();
        cmbTipo.getItems().addAll(TipoCuenta.values());
        cmbTipo.setValue(TipoCuenta.PERSONAL);
        cmbTipo.setMaxWidth(Double.MAX_VALUE);

        TextField txtEmpresa = new TextField();
        txtEmpresa.setPromptText("Nombre de la empresa (si aplica)");

        Label lblError = new Label(" ");
        lblError.getStyleClass().add("login-error");

        contenido.getChildren().addAll(
            new Label("Nombre completo:"), txtNombre,
            new Label("Email:"), txtEmailReg,
            new Label("Contrasenia:"), txtPassReg,
            new Label("Tipo de cuenta:"), cmbTipo,
            new Label("Empresa (opcional):"), txtEmpresa,
            lblError
        );

        dialog.getDialogPane().setContent(contenido);

        Button btnCrear = (Button) dialog.getDialogPane().lookupButton(btnCrearTipo);
        btnCrear.addEventFilter(ActionEvent.ACTION, event -> {
            try {
                authService.registrar(
                    txtNombre.getText().trim(),
                    txtEmailReg.getText().trim(),
                    txtPassReg.getText(),
                    cmbTipo.getValue(),
                    txtEmpresa.getText().trim()
                );
                lblEstado.setText("Cuenta creada. Ya puedes iniciar sesion.");
            } catch (ValidacionException ex) {
                lblError.setText(String.join(" | ", ex.getErrores()));
                event.consume();
            }
        });

        dialog.showAndWait();
    }
}