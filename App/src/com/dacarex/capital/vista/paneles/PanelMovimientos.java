package com.dacarex.capital.vista.paneles;

import com.dacarex.capital.enums.PeriodoDashboard;
import com.dacarex.capital.enums.TipoMovimiento;
import com.dacarex.capital.exception.ValidacionException;
import com.dacarex.capital.modelo.Categoria;
import com.dacarex.capital.modelo.Movimiento;
import com.dacarex.capital.service.CategoriaService;
import com.dacarex.capital.service.MovimientoService;
import com.dacarex.capital.util.ExportadorCSV;
import com.dacarex.capital.util.TemaManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class PanelMovimientos extends JPanel {

    private final MovimientoService movimientoService = new MovimientoService();
    private final CategoriaService categoriaService = new CategoriaService();

    private List<Movimiento> movimientosActuales;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private JComboBox<String> cmbTipo;
    private JComboBox<PeriodoDashboard> cmbPeriodo;
    private JLabel lblSaldo;
    private JLabel lblTitulo;

    public PanelMovimientos() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(0, 15));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // ── CABECERA ──
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);

        lblTitulo = new JLabel("Movimientos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(TemaManager.textoTitulo());
        cabecera.add(lblTitulo, BorderLayout.WEST);

        lblSaldo = new JLabel("Saldo: 0.00 EUR");
        lblSaldo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cabecera.add(lblSaldo, BorderLayout.EAST);

        add(cabecera, BorderLayout.NORTH);

        // ── PANEL CENTRAL (filtros + tabla) ──
        JPanel centro = new JPanel(new BorderLayout(0, 12));
        centro.setOpaque(false);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filtros.setOpaque(false);

        txtBuscar = new JTextField(16);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBuscar.setToolTipText("Buscar por descripcion");
        txtBuscar.addActionListener(e -> filtrar());

        cmbTipo = new JComboBox<>(new String[]{"Todos", "INGRESO", "GASTO"});
        cmbPeriodo = new JComboBox<>(PeriodoDashboard.values());

        JButton btnFiltrar = botonEstilo("Filtrar", TemaManager.acento());
        btnFiltrar.addActionListener(e -> filtrar());

        JButton btnNuevo = botonEstilo("+ Nuevo", TemaManager.exito());
        btnNuevo.addActionListener(e -> abrirFormularioNuevo());

        JButton btnEliminar = botonEstilo("Eliminar", TemaManager.peligro());
        btnEliminar.addActionListener(e -> eliminarSeleccionado());

        JButton btnExportar = botonEstilo("Exportar CSV", TemaManager.aviso());
        btnExportar.addActionListener(e -> exportarCSV());

        filtros.add(txtBuscar);
        filtros.add(cmbTipo);
        filtros.add(cmbPeriodo);
        filtros.add(btnFiltrar);
        filtros.add(btnNuevo);
        filtros.add(btnEliminar);
        filtros.add(btnExportar);

        centro.add(filtros, BorderLayout.NORTH);

        // ── TABLA ──
        String[] columnas = {"Fecha", "Tipo", "Descripcion", "Importe (EUR)", "Categoria", "Notas"};
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
        t.setIntercellSpacing(new Dimension(0, 0));
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
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        return btn;
    }

    public void recargar() {
        cargarTabla(movimientoService.obtenerTodos());
        actualizarSaldo();
    }

    private void cargarTabla(List<Movimiento> lista) {
        this.movimientosActuales = lista;
        modeloTabla.setRowCount(0);
        for (Movimiento m : lista) {
            modeloTabla.addRow(new Object[]{
                m.getFecha(),
                m.getTipo().getValor(),
                m.getDescripcion(),
                String.format("%.2f", m.getImporte()),
                m.getCategoria() != null ? m.getCategoria().getNombre() : "-",
                m.getNotas() != null ? m.getNotas() : ""
            });
        }
    }

    private void actualizarSaldo() {
        double saldo = movimientoService.calcularSaldo();
        lblSaldo.setText(String.format("Saldo: %.2f EUR", saldo));
        lblSaldo.setForeground(saldo >= 0 ? TemaManager.exito() : TemaManager.peligro());
    }

    private void filtrar() {
        String texto = txtBuscar.getText().trim();
        String tipo = (String) cmbTipo.getSelectedItem();
        PeriodoDashboard periodo = (PeriodoDashboard) cmbPeriodo.getSelectedItem();

        List<Movimiento> resultado;

        if (!"Todos".equals(tipo)) {
            resultado = movimientoService.filtrarPorTipo(TipoMovimiento.valueOf(tipo));
        } else if (!texto.isEmpty()) {
            resultado = movimientoService.filtrarPorTexto(texto);
        } else if (periodo != PeriodoDashboard.TODO) {
            resultado = movimientoService.filtrarPorRango(fechaInicioPeriodo(periodo), LocalDate.now());
        } else {
            resultado = movimientoService.obtenerTodos();
        }

        cargarTabla(resultado);
    }

    private LocalDate fechaInicioPeriodo(PeriodoDashboard periodo) {
        LocalDate hoy = LocalDate.now();
        return switch (periodo) {
            case MES       -> hoy.withDayOfMonth(1);
            case TRIMESTRE -> hoy.minusMonths(3);
            case ANIO      -> hoy.withDayOfYear(1);
            case TODO      -> LocalDate.of(2000, 1, 1);
        };
    }

    private void abrirFormularioNuevo() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Nuevo Movimiento", true);
        dialog.setSize(420, 400);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(TemaManager.fondoTarjeta());
        dialog.setLayout(new GridLayout(8, 2, 10, 12));
        ((JPanel) dialog.getContentPane()).setBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20));

        dialog.add(etiqueta("Tipo:"));
        JComboBox<TipoMovimiento> cmbTipoMov = new JComboBox<>(TipoMovimiento.values());
        dialog.add(cmbTipoMov);

        dialog.add(etiqueta("Descripcion:"));
        JTextField txtDesc = new JTextField();
        dialog.add(txtDesc);

        dialog.add(etiqueta("Importe (EUR):"));
        JTextField txtImporte = new JTextField();
        dialog.add(txtImporte);

        dialog.add(etiqueta("Categoria:"));
        List<Categoria> categorias = categoriaService.obtenerTodas();
        JComboBox<Categoria> cmbCat = new JComboBox<>(categorias.toArray(new Categoria[0]));
        dialog.add(cmbCat);

        dialog.add(etiqueta("Fecha (YYYY-MM-DD):"));
        JTextField txtFecha = new JTextField(LocalDate.now().toString());
        dialog.add(txtFecha);

        dialog.add(etiqueta("Notas:"));
        JTextField txtNotas = new JTextField();
        dialog.add(txtNotas);

        JLabel lblError = new JLabel(" ", SwingConstants.CENTER);
        lblError.setForeground(TemaManager.peligro());
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dialog.add(lblError);

        JButton btnGuardar = botonEstilo("Guardar", TemaManager.exito());
        dialog.add(btnGuardar);

        btnGuardar.addActionListener(e -> {
            try {
                TipoMovimiento tipo = (TipoMovimiento) cmbTipoMov.getSelectedItem();
                String desc = txtDesc.getText().trim();
                double importe = Double.parseDouble(txtImporte.getText().trim());
                Categoria cat = (Categoria) cmbCat.getSelectedItem();
                LocalDate fecha = LocalDate.parse(txtFecha.getText().trim());
                String notas = txtNotas.getText().trim();

                movimientoService.crear(tipo, desc, importe, cat, fecha, notas);
                recargar();
                dialog.dispose();

            } catch (NumberFormatException ex) {
                lblError.setText("El importe debe ser un numero valido.");
            } catch (java.time.format.DateTimeParseException ex) {
                lblError.setText("Fecha invalida. Usa formato YYYY-MM-DD.");
            } catch (ValidacionException ex) {
                lblError.setText(String.join(" | ", ex.getErrores()));
            } catch (Exception ex) {
                lblError.setText("Error: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }

    private void eliminarSeleccionado() {
        int filaVista = tabla.getSelectedRow();
        if (filaVista == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un movimiento para eliminar.");
            return;
        }
        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        Movimiento m = movimientosActuales.get(filaModelo);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Eliminar este movimiento?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            movimientoService.eliminar(m.getId());
            recargar();
        }
    }

    private void exportarCSV() {
        try {
            List<Movimiento> todos = movimientoService.obtenerTodos();
            if (todos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay movimientos para exportar.");
                return;
            }
            String ruta = ExportadorCSV.exportar(todos);
            JOptionPane.showMessageDialog(this, "Exportado en:\n" + ruta);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage());
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