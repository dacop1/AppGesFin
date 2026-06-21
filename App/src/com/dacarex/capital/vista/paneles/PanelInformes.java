package com.dacarex.capital.vista.paneles;

import com.dacarex.capital.modelo.Movimiento;
import com.dacarex.capital.service.InformeService;
import com.dacarex.capital.service.MovimientoService;
import com.dacarex.capital.util.ExportadorCSV;
import com.dacarex.capital.util.TemaManager;
import com.dacarex.capital.vista.componentes.PanelGraficoTarta;
import com.dacarex.capital.vista.componentes.TarjetaKPI;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class PanelInformes extends JPanel {

    private final InformeService informeService = new InformeService();
    private final MovimientoService movimientoService = new MovimientoService();

    private JLabel lblTitulo;
    private JComboBox<String> cmbVista;
    private PanelGraficoTarta graficoTarta;
    private TarjetaKPI tarjetaMayorIngreso;
    private TarjetaKPI tarjetaMayorGasto;
    private TarjetaKPI tarjetaCategoria;
    private TarjetaKPI tarjetaPromedio;

    public PanelInformes() {
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(0, 15));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // ── CABECERA ──
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setOpaque(false);

        lblTitulo = new JLabel("Informes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitulo.setForeground(TemaManager.textoTitulo());
        cabecera.add(lblTitulo, BorderLayout.WEST);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acciones.setOpaque(false);

        cmbVista = new JComboBox<>(new String[]{"Gastos por categoria", "Ingresos por categoria"});
        cmbVista.addActionListener(e -> recargar());
        acciones.add(cmbVista);

        JButton btnExportar = new JButton("Exportar CSV");
        btnExportar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnExportar.setBackground(TemaManager.aviso());
        btnExportar.setForeground(Color.WHITE);
        btnExportar.setFocusPainted(false);
        btnExportar.setBorderPainted(false);
        btnExportar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnExportar.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        btnExportar.addActionListener(e -> exportarCSV());
        acciones.add(btnExportar);

        cabecera.add(acciones, BorderLayout.EAST);
        add(cabecera, BorderLayout.NORTH);

        // ── CONTENIDO ──
        JPanel contenido = new JPanel(new BorderLayout(0, 20));
        contenido.setOpaque(false);

        JPanel filaTarjetas = new JPanel(new GridLayout(1, 4, 18, 0));
        filaTarjetas.setOpaque(false);
        filaTarjetas.setPreferredSize(new Dimension(0, 110));

        tarjetaMayorIngreso = new TarjetaKPI("Mayor ingreso", "-", TemaManager.exito(), "⬆️");
        tarjetaMayorGasto    = new TarjetaKPI("Mayor gasto", "-", TemaManager.peligro(), "⬇️");
        tarjetaCategoria     = new TarjetaKPI("Categoria mas activa", "-", TemaManager.info(), "🏷️");
        tarjetaPromedio      = new TarjetaKPI("Promedio gasto/mes", "-", TemaManager.aviso(), "📊");

        filaTarjetas.add(tarjetaMayorIngreso);
        filaTarjetas.add(tarjetaMayorGasto);
        filaTarjetas.add(tarjetaCategoria);
        filaTarjetas.add(tarjetaPromedio);

        contenido.add(filaTarjetas, BorderLayout.NORTH);

        graficoTarta = new PanelGraficoTarta();
        contenido.add(graficoTarta, BorderLayout.CENTER);

        add(contenido, BorderLayout.CENTER);

        recargar();
    }

    public void recargar() {
        boolean esGastos = "Gastos por categoria".equals(cmbVista.getSelectedItem());

        graficoTarta.setDatos(esGastos
            ? informeService.gastosPorCategoria()
            : informeService.ingresosPorCategoria());

        Optional<Movimiento> mayorIngreso = informeService.mayorIngreso();
        Optional<Movimiento> mayorGasto    = informeService.mayorGasto();

        tarjetaMayorIngreso.actualizar(
            mayorIngreso.map(m -> String.format("%.2f EUR", m.getImporte())).orElse("-"),
            TemaManager.exito());

        tarjetaMayorGasto.actualizar(
            mayorGasto.map(m -> String.format("%.2f EUR", m.getImporte())).orElse("-"),
            TemaManager.peligro());

        tarjetaCategoria.actualizar(informeService.categoriaMasActiva(), TemaManager.info());

        tarjetaPromedio.actualizar(
            String.format("%.2f EUR", informeService.promedioGastoMensual()),
            TemaManager.aviso());
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

    public void aplicarTema() {
        lblTitulo.setForeground(TemaManager.textoTitulo());
        recargar();
        repaint();
    }
}