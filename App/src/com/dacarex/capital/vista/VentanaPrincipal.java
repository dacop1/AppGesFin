package com.dacarex.capital.vista;

import com.dacarex.capital.modelo.Usuario;
import com.dacarex.capital.service.AuthService;
import com.dacarex.capital.util.TemaManager;
import com.dacarex.capital.vista.componentes.IconoVectorial;
import com.dacarex.capital.vista.paneles.PanelCategorias;
import com.dacarex.capital.vista.paneles.PanelDashboard;
import com.dacarex.capital.vista.paneles.PanelInformes;
import com.dacarex.capital.vista.paneles.PanelMovimientos;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class VentanaPrincipal extends JFrame {

    private final Usuario usuarioActual;
    private final AuthService authService;

    private JPanel sidebar;
    private JPanel panelContenido;
    private CardLayout cardLayout;

    private PanelDashboard panelDashboard;
    private PanelMovimientos panelMovimientos;
    private PanelCategorias panelCategorias;
    private PanelInformes panelInformes;

    private final Map<String, JButton> botonesNav = new LinkedHashMap<>();
    private String panelActivo = "dashboard";
    private JButton btnTema;

    public VentanaPrincipal(Usuario usuario, AuthService authService) {
        this.usuarioActual = usuario;
        this.authService = authService;
        inicializarVentana();
        construirInterfaz();
    }

    private void inicializarVentana() {
        setTitle("Dacarex Capital — " + usuarioActual.getNombre());
        setSize(1180, 720);
        setMinimumSize(new Dimension(1000, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // Abre maximizada como una app normal, pero sigue siendo redimensionable
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void construirInterfaz() {
        getContentPane().removeAll();
        getContentPane().setBackground(TemaManager.fondoApp());
        setLayout(new BorderLayout());

        sidebar = crearSidebar();
        add(sidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.setBackground(TemaManager.fondoApp());

        panelDashboard   = new PanelDashboard();
        panelMovimientos = new PanelMovimientos();
        panelCategorias  = new PanelCategorias();
        panelInformes    = new PanelInformes();

        panelContenido.add(envolver(panelDashboard),   "dashboard");
        panelContenido.add(envolver(panelMovimientos), "movimientos");
        panelContenido.add(envolver(panelCategorias),  "categorias");
        panelContenido.add(envolver(panelInformes),    "informes");

        add(panelContenido, BorderLayout.CENTER);

        mostrarPanel(panelActivo);

        revalidate();
        repaint();
    }

    private JPanel envolver(JPanel panel) {
        panel.setBackground(TemaManager.fondoApp());
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(TemaManager.fondoApp());
        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel crearSidebar() {
        JPanel sb = new JPanel();
        sb.setPreferredSize(new Dimension(220, 0));
        sb.setBackground(TemaManager.fondoSidebar());
        sb.setLayout(new BoxLayout(sb, BoxLayout.Y_AXIS));
        sb.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 6, 0));
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 4, 0));
        JLabel lblIcono = new JLabel(new IconoVectorial(IconoVectorial.Tipo.MONEDA, TemaManager.acento(), 20));
        JLabel lblLogo = new JLabel("Dacarex");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 19));
        lblLogo.setForeground(Color.WHITE);
        logoPanel.add(lblIcono);
        logoPanel.add(lblLogo);
        sb.add(logoPanel);

        JLabel lblUsuario = new JLabel("  " + usuarioActual.getNombre());
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUsuario.setForeground(new Color(150, 150, 175));
        lblUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblUsuario.setBorder(BorderFactory.createEmptyBorder(0, 22, 25, 0));
        sb.add(lblUsuario);

        agregarBotonNav(sb, "dashboard",   "Dashboard",   IconoVectorial.Tipo.CASA);
        agregarBotonNav(sb, "movimientos", "Movimientos", IconoVectorial.Tipo.MONEDA);
        agregarBotonNav(sb, "categorias",  "Categorias",  IconoVectorial.Tipo.ETIQUETA);
        agregarBotonNav(sb, "informes",    "Informes",    IconoVectorial.Tipo.GRAFICO);

        sb.add(Box.createVerticalGlue());

        IconoVectorial.Tipo iconoTema = TemaManager.isModoOscuro()
            ? IconoVectorial.Tipo.SOL : IconoVectorial.Tipo.LUNA;
        String textoTema = TemaManager.isModoOscuro() ? "Modo claro" : "Modo oscuro";
        btnTema = crearBotonSimple(textoTema, iconoTema);
        btnTema.addActionListener(e -> cambiarTema());
        sb.add(btnTema);
        sb.add(Box.createVerticalStrut(8));

        JButton btnSalir = crearBotonSimple("Cerrar sesion", IconoVectorial.Tipo.SALIR);
        btnSalir.setForeground(new Color(255, 130, 130));
        btnSalir.addActionListener(e -> {
            authService.logout();
            dispose();
            new VentanaLogin().setVisible(true);
        });
        sb.add(btnSalir);
        sb.add(Box.createVerticalStrut(10));

        return sb;
    }

    private void agregarBotonNav(JPanel sb, String clave, String texto, IconoVectorial.Tipo tipoIcono) {
        JButton btn = new JButton(texto, new IconoVectorial(tipoIcono, Color.WHITE, 16));
        btn.setIconTextGap(12);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(TemaManager.fondoSidebar());
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 10));
        btn.addActionListener(e -> mostrarPanel(clave));
        botonesNav.put(clave, btn);
        sb.add(btn);
    }

    private JButton crearBotonSimple(String texto, IconoVectorial.Tipo tipoIcono) {
        JButton btn = new JButton(texto, new IconoVectorial(tipoIcono, new Color(190, 190, 210), 15));
        btn.setIconTextGap(10);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(new Color(190, 190, 210));
        btn.setBackground(TemaManager.fondoSidebar());
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 22, 8, 10));
        return btn;
    }

    private void resaltarBotonActivo() {
        botonesNav.forEach((clave, btn) -> {
            if (clave.equals(panelActivo)) {
                btn.setBackground(TemaManager.acento());
                btn.setOpaque(true);
                btn.setContentAreaFilled(true);
            } else {
                btn.setBackground(TemaManager.fondoSidebar());
                btn.setContentAreaFilled(false);
            }
        });
    }

    public void mostrarPanel(String clave) {
        panelActivo = clave;

        switch (clave) {
            case "dashboard"   -> panelDashboard.recargar();
            case "movimientos" -> panelMovimientos.recargar();
            case "categorias"  -> panelCategorias.recargar();
            case "informes"    -> panelInformes.recargar();
        }

        cardLayout.show(panelContenido, clave);
        resaltarBotonActivo();
    }

    private void cambiarTema() {
        TemaManager.toggle();
        construirInterfaz();
    }
}