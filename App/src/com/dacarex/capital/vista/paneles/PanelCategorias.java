package com.dacarex.capital.vista.paneles;

import com.dacarex.capital.enums.TipoMovimiento;
import com.dacarex.capital.exception.ValidacionException;
import com.dacarex.capital.modelo.Categoria;
import com.dacarex.capital.service.CategoriaService;
import com.dacarex.capital.util.TemaManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PanelCategorias extends JPanel {

    private final CategoriaService categoriaService = new CategoriaService();

    private List<Categoria> categoriasActuales;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JLabel lblTitulo;

    public PanelCategorias() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(0, 15));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);

        lblTitulo = new JLabel("Categorias");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(TemaManager.textoTitulo());
        cabecera.add(lblTitulo, BorderLayout.WEST);

        add(cabecera, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 12));
        centro.setOpaque(false);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botones.setOpaque(false);

        JButton btnNueva = botonEstilo("+ Nueva categoria", TemaManager.exito());
        btnNueva.addActionListener(e -> abrirFormularioNueva());
        botones.add(btnNueva);

        JButton btnEliminar = botonEstilo("Eliminar", TemaManager.peligro());
        btnEliminar.addActionListener(e -> eliminarSeleccionada());
        botones.add(btnEliminar);

        centro.add(botones, BorderLayout.NORTH);

        String[] columnas = {"Nombre", "Tipo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        tabla = new JTable(modeloTabla);
        estilizarTabla(tabla);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(TemaManager.borde()));
        scroll.getViewport().setBackground(TemaManager.fondoTarjeta());

        centro.add(scroll, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);

        recargar();
    }

    private void estilizarTabla(JTable t) {
        t.setRowHeight(34);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setShowGrid(false);
        t.setSelectionBackground(TemaManager.acento());
        t.setSelectionForeground(Color.WHITE);
        t.setBackground(TemaManager.fondoTarjeta());
        t.setForeground(TemaManager.textoNormal());
        t.setFillsViewportHeight(true);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.getTableHeader().setBackground(TemaManager.fondoSidebar());
        t.getTableHeader().setForeground(Color.WHITE);
        t.getTableHeader().setPreferredSize(new Dimension(0, 38));
        t.setAutoCreateRowSorter(true);
    }

    private JButton botonEstilo(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        return btn;
    }

    public void recargar() {
        categoriasActuales = categoriaService.obtenerTodas();
        modeloTabla.setRowCount(0);
        for (Categoria c : categoriasActuales) {
            modeloTabla.addRow(new Object[]{c.getNombre(), c.getTipo().getValor()});
        }
    }

    private void abrirFormularioNueva() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Nueva Categoria", true);
        dialog.setSize(340, 240);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(TemaManager.fondoTarjeta());
        dialog.setLayout(new GridLayout(4, 2, 10, 12));
        ((JPanel) dialog.getContentPane()).setBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20));

        dialog.add(etiqueta("Nombre:"));
        JTextField txtNombre = new JTextField();
        dialog.add(txtNombre);

        dialog.add(etiqueta("Tipo:"));
        JComboBox<TipoMovimiento> cmbTipo = new JComboBox<>(TipoMovimiento.values());
        dialog.add(cmbTipo);

        JLabel lblError = new JLabel(" ", SwingConstants.CENTER);
        lblError.setForeground(TemaManager.peligro());
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dialog.add(lblError);

        JButton btnGuardar = botonEstilo("Guardar", TemaManager.exito());
        dialog.add(btnGuardar);

        Color[] paleta = TemaManager.paletaGrafico();

        btnGuardar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                TipoMovimiento tipo = (TipoMovimiento) cmbTipo.getSelectedItem();
                Color colorAsignado = paleta[categoriasActuales.size() % paleta.length];
                String colorHex = String.format("#%02X%02X%02X",
                    colorAsignado.getRed(), colorAsignado.getGreen(), colorAsignado.getBlue());

                categoriaService.crear(nombre, tipo, colorHex);
                recargar();
                dialog.dispose();

            } catch (ValidacionException ex) {
                lblError.setText(String.join(" | ", ex.getErrores()));
            } catch (Exception ex) {
                lblError.setText("Error: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }

    private void eliminarSeleccionada() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una categoria para eliminar.");
            return;
        }
        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        Categoria c = categoriasActuales.get(filaModelo);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Eliminar esta categoria?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            categoriaService.eliminar(c.getId());
            recargar();
        }
    }

    private JLabel etiqueta(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(TemaManager.textoNormal());
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }

    public void aplicarTema() {
        lblTitulo.setForeground(TemaManager.textoTitulo());
        estilizarTabla(tabla);
        recargar();
        repaint();
    }
}