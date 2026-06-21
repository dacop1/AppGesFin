package com.dacarex.capital.vista;

import com.dacarex.capital.enums.TipoCuenta;
import com.dacarex.capital.exception.AutenticacionException;
import com.dacarex.capital.exception.ValidacionException;
import com.dacarex.capital.modelo.Usuario;
import com.dacarex.capital.service.AuthService;
import com.dacarex.capital.util.TemaManager;
import com.dacarex.capital.vista.componentes.IconoVectorial;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;

public class VentanaLogin extends JFrame {

    private final AuthService authService = new AuthService();

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JLabel lblEstado;

    private static final int ANCHO_CAMPO = 290;

    public VentanaLogin() {
        inicializarVentana();
        inicializarComponentes();
    }

    private void inicializarVentana() {
        setTitle("Dacarex Capital — Acceso");
        setSize(440, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void inicializarComponentes() {
        JPanel fondo = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint degradado = new GradientPaint(
                    0, 0, new Color(28, 28, 50),
                    0, getHeight(), new Color(60, 70, 130)
                );
                g2.setPaint(degradado);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        setContentPane(fondo);

        // Una unica tarjeta, sin doble envoltorio
        JPanel tarjeta = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 240));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 24, 24));
                g2.dispose();
            }
        };
        tarjeta.setOpaque(false);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(35, 35, 35, 35));
        Dimension tamTarjeta = new Dimension(360, 500);
        tarjeta.setPreferredSize(tamTarjeta);
        tarjeta.setMaximumSize(tamTarjeta);

        IconoVectorial icono = new IconoVectorial(IconoVectorial.Tipo.MONEDA, new Color(94, 124, 255), 38);
        JLabel lblIcono = new JLabel(icono);
        lblIcono.setAlignmentX(Component.CENTER_ALIGNMENT);
        tarjeta.add(lblIcono);

        JLabel lblTitulo = new JLabel("DACAREX CAPITAL");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(28, 28, 50));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setBorder(new EmptyBorder(10, 0, 2, 0));
        tarjeta.add(lblTitulo);

        JLabel lblSub = new JLabel("Gestion financiera inteligente");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(130, 130, 150));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSub.setBorder(new EmptyBorder(0, 0, 28, 0));
        tarjeta.add(lblSub);

        tarjeta.add(crearEtiquetaCampo("Email"));
        txtEmail = crearCampoTexto();
        tarjeta.add(txtEmail);
        tarjeta.add(Box.createVerticalStrut(16));

        tarjeta.add(crearEtiquetaCampo("Contrasenia"));
        txtPassword = new JPasswordField();
        estilizarCampo(txtPassword);
        tarjeta.add(txtPassword);
        tarjeta.add(Box.createVerticalStrut(22));

        lblEstado = new JLabel(" ", SwingConstants.CENTER);
        lblEstado.setForeground(TemaManager.peligro());
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblEstado.setAlignmentX(Component.CENTER_ALIGNMENT);
        tarjeta.add(lblEstado);
        tarjeta.add(Box.createVerticalStrut(8));

        JButton btnLogin = new JButton("Entrar");
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(ANCHO_CAMPO, 42));
        btnLogin.setPreferredSize(new Dimension(ANCHO_CAMPO, 42));
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(TemaManager.acento());
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> intentarLogin());
        tarjeta.add(btnLogin);
        tarjeta.add(Box.createVerticalStrut(14));

        JButton btnRegistrar = new JButton("Crear una cuenta nueva");
        btnRegistrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegistrar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRegistrar.setForeground(TemaManager.acento());
        btnRegistrar.setBorderPainted(false);
        btnRegistrar.setContentAreaFilled(false);
        btnRegistrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRegistrar.addActionListener(e -> abrirDialogoRegistro());
        tarjeta.add(btnRegistrar);

        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) intentarLogin();
            }
        });

        fondo.add(tarjeta); // GridBagLayout sin constraints = centrado automatico
    }

    private JLabel crearEtiquetaCampo(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(100, 100, 120));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setBorder(new EmptyBorder(0, 0, 4, 0));
        return lbl;
    }

    private JTextField crearCampoTexto() {
        JTextField campo = new JTextField();
        estilizarCampo(campo);
        return campo;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        Dimension tam = new Dimension(ANCHO_CAMPO, 40);
        campo.setMaximumSize(tam);
        campo.setPreferredSize(tam);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 222, 230), 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        campo.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void intentarLogin() {
        String email = txtEmail.getText().trim();
        String pass  = new String(txtPassword.getPassword());

        if (email.isEmpty() || pass.isEmpty()) {
            lblEstado.setText("Rellena todos los campos.");
            return;
        }

        try {
            Usuario usuario = authService.login(email, pass);
            dispose();
            new VentanaPrincipal(usuario, authService).setVisible(true);
        } catch (AutenticacionException ex) {
            lblEstado.setText(ex.getMessage());
        }
    }

    private void abrirDialogoRegistro() {
        JDialog dialog = new JDialog(this, "Crear cuenta", true);
        dialog.setSize(380, 440);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Color.WHITE);
        dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
        ((JPanel) dialog.getContentPane()).setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel titulo = new JLabel("Nueva cuenta");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        dialog.add(titulo);
        dialog.add(Box.createVerticalStrut(20));

        JTextField txtNombre = new JTextField();
        dialog.add(campoConEtiqueta("Nombre completo", txtNombre));
        dialog.add(Box.createVerticalStrut(12));

        JTextField txtEmailReg = new JTextField();
        dialog.add(campoConEtiqueta("Email", txtEmailReg));
        dialog.add(Box.createVerticalStrut(12));

        JLabel lblPass = new JLabel("Contrasenia (min. 6 caracteres)");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPass.setForeground(new Color(100, 100, 120));
        lblPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        dialog.add(lblPass);
        JPasswordField txtPassReg = new JPasswordField();
        txtPassReg.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtPassReg.setAlignmentX(Component.LEFT_ALIGNMENT);
        dialog.add(txtPassReg);
        dialog.add(Box.createVerticalStrut(12));

        JLabel lblTipo = new JLabel("Tipo de cuenta");
        lblTipo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTipo.setForeground(new Color(100, 100, 120));
        lblTipo.setAlignmentX(Component.LEFT_ALIGNMENT);
        dialog.add(lblTipo);
        JComboBox<TipoCuenta> cmbTipo = new JComboBox<>(TipoCuenta.values());
        cmbTipo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbTipo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        dialog.add(cmbTipo);
        dialog.add(Box.createVerticalStrut(16));

        JLabel lblErrorReg = new JLabel(" ");
        lblErrorReg.setForeground(TemaManager.peligro());
        lblErrorReg.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblErrorReg.setAlignmentX(Component.LEFT_ALIGNMENT);
        dialog.add(lblErrorReg);
        dialog.add(Box.createVerticalStrut(8));

        JButton btnCrear = new JButton("Crear cuenta");
        btnCrear.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCrear.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnCrear.setBackground(TemaManager.exito());
        btnCrear.setForeground(Color.WHITE);
        btnCrear.setFocusPainted(false);
        btnCrear.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dialog.add(btnCrear);

        btnCrear.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                String email  = txtEmailReg.getText().trim();
                String pass   = new String(txtPassReg.getPassword());
                TipoCuenta tipo = (TipoCuenta) cmbTipo.getSelectedItem();
                String empresa = tipo == TipoCuenta.EMPRESA
                    ? JOptionPane.showInputDialog(dialog, "Nombre de la empresa:")
                    : null;

                authService.registrar(nombre, email, pass, tipo, empresa);
                JOptionPane.showMessageDialog(dialog, "Cuenta creada. Ya puedes iniciar sesion.");
                dialog.dispose();

            } catch (ValidacionException ex) {
                lblErrorReg.setText(String.join(" | ", ex.getErrores()));
            } catch (Exception ex) {
                lblErrorReg.setText("Error: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }

    private JPanel campoConEtiqueta(String etiqueta, JTextField campo) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(100, 100, 120));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        campo.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lbl);
        panel.add(campo);
        return panel;
    }
}