package com.dacarex.capital.vista.paneles;

import com.dacarex.capital.service.InformeService;
import com.dacarex.capital.service.MovimientoService;
import com.dacarex.capital.util.TemaManager;
import com.dacarex.capital.vista.componentes.PanelGraficoBarras;
import com.dacarex.capital.vista.componentes.PanelGraficoLineas;
import com.dacarex.capital.vista.componentes.PanelGraficoTarta;
import com.dacarex.capital.vista.componentes.TarjetaKPI;
import javax.swing.Scrollable;

import javax.swing.*;
import java.awt.*;

public class PanelDashboard extends JPanel {

    private final MovimientoService movimientoService = new MovimientoService();
    private final InformeService informeService = new InformeService();

    private TarjetaKPI tarjetaSaldo;
    private TarjetaKPI tarjetaIngresos;
    private TarjetaKPI tarjetaGastos;
    private TarjetaKPI tarjetaCategoria;

    private PanelGraficoLineas graficoLineas;
    private PanelGraficoTarta graficoTarta;
    private PanelGraficoBarras graficoBarras;

    private JLabel lblSaludo;
    private JPanel contenedor;

    public PanelDashboard() {
        inicializarComponentes();
        recargar();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        contenedor = new PanelDesplazable();
        contenedor.setOpaque(false);
        contenedor.setLayout(new BoxLayout(contenedor, BoxLayout.Y_AXIS));

        // ── CABECERA ──
        lblSaludo = new JLabel("Dashboard");
        lblSaludo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblSaludo.setForeground(TemaManager.textoTitulo());
        lblSaludo.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenedor.add(lblSaludo);

        JLabel lblSub = new JLabel("Resumen general de tus finanzas");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(TemaManager.textoSecundario());
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSub.setBorder(BorderFactory.createEmptyBorder(2, 0, 20, 0));
        contenedor.add(lblSub);

        // ── TARJETAS KPI ──
        JPanel filaTarjetas = new JPanel(new GridLayout(1, 4, 18, 0));
        filaTarjetas.setOpaque(false);
        filaTarjetas.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaTarjetas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        tarjetaSaldo     = new TarjetaKPI("Saldo actual", "0.00 EUR", TemaManager.acento(), "💰");
        tarjetaIngresos  = new TarjetaKPI("Ingresos totales", "0.00 EUR", TemaManager.exito(), "📈");
        tarjetaGastos    = new TarjetaKPI("Gastos totales", "0.00 EUR", TemaManager.peligro(), "📉");
        tarjetaCategoria = new TarjetaKPI("Categoria mas activa", "-", TemaManager.info(), "🏷️");

        filaTarjetas.add(tarjetaSaldo);
        filaTarjetas.add(tarjetaIngresos);
        filaTarjetas.add(tarjetaGastos);
        filaTarjetas.add(tarjetaCategoria);

        contenedor.add(filaTarjetas);
        contenedor.add(Box.createVerticalStrut(20));

        // ── GRAFICO DE LINEAS (ancho completo) ──
        JLabel lblEvolucion = crearTituloSeccion("Evolucion del saldo acumulado");
        contenedor.add(lblEvolucion);

        graficoLineas = new PanelGraficoLineas();
        graficoLineas.setAlignmentX(Component.LEFT_ALIGNMENT);
        graficoLineas.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        contenedor.add(graficoLineas);
        contenedor.add(Box.createVerticalStrut(20));

        // ── FILA INFERIOR: TARTA + BARRAS ──
        JPanel filaGraficos = new JPanel(new GridLayout(1, 2, 18, 0));
        filaGraficos.setOpaque(false);
        filaGraficos.setAlignmentX(Component.LEFT_ALIGNMENT);
        filaGraficos.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

        JPanel colIzq = new JPanel(new BorderLayout());
        colIzq.setOpaque(false);
        colIzq.add(crearTituloSeccion("Gastos por categoria"), BorderLayout.NORTH);
        graficoTarta = new PanelGraficoTarta();
        colIzq.add(graficoTarta, BorderLayout.CENTER);

        JPanel colDer = new JPanel(new BorderLayout());
        colDer.setOpaque(false);
        colDer.add(crearTituloSeccion("Ingresos vs Gastos (6 meses)"), BorderLayout.NORTH);
        graficoBarras = new PanelGraficoBarras();
        colDer.add(graficoBarras, BorderLayout.CENTER);

        filaGraficos.add(colIzq);
        filaGraficos.add(colDer);

        contenedor.add(filaGraficos);

        JScrollPane scroll = new JScrollPane(contenedor);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);
    }

    private JLabel crearTituloSeccion(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(TemaManager.textoTitulo());
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    public void recargar() {
        double saldo    = movimientoService.calcularSaldo();
        double ingresos = movimientoService.calcularTotalIngresos();
        double gastos   = movimientoService.calcularTotalGastos();
        String catTop   = informeService.categoriaMasActiva();

        tarjetaSaldo.actualizar(String.format("%.2f EUR", saldo),
                saldo >= 0 ? TemaManager.exito() : TemaManager.peligro());
        tarjetaIngresos.actualizar(String.format("%.2f EUR", ingresos), TemaManager.exito());
        tarjetaGastos.actualizar(String.format("%.2f EUR", gastos), TemaManager.peligro());
        tarjetaCategoria.actualizar(catTop, TemaManager.info());

        graficoLineas.setPuntos(informeService.evolucionSaldoAcumulado());
        graficoTarta.setDatos(informeService.gastosPorCategoria());
        graficoBarras.setDatos(informeService.evolucionMensual(6));
    }

    public void aplicarTema() {
        lblSaludo.setForeground(TemaManager.textoTitulo());
        recargar();
        repaint();
    }
    
    
    private static class PanelDesplazable extends JPanel implements Scrollable {
        @Override
        public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override
        public int getScrollableUnitIncrement(Rectangle r, int orient, int dir) { return 16; }
        @Override
        public int getScrollableBlockIncrement(Rectangle r, int orient, int dir) { return 100; }
        @Override
        public boolean getScrollableTracksViewportWidth() { return true; }
        @Override
        public boolean getScrollableTracksViewportHeight() { return false; }
    }
}