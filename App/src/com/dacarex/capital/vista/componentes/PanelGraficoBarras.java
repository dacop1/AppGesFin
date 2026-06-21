package com.dacarex.capital.vista.componentes;

import com.dacarex.capital.util.TemaManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedHashMap;
import java.util.Map;

public class PanelGraficoBarras extends JPanel {

    private Map<String, double[]> datos = new LinkedHashMap<>();

    public PanelGraficoBarras() {
        setOpaque(false);
        setPreferredSize(new Dimension(400, 280));
    }

    public void setDatos(Map<String, double[]> datos) {
        this.datos = datos;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        int margenIzq = 55, margenDer = 20, margenSup = 30, margenInf = 45;
        int anchoGrafico = w - margenIzq - margenDer;
        int altoGrafico   = h - margenSup - margenInf;

        g2.setColor(TemaManager.fondoTarjeta());
        g2.fillRoundRect(0, 0, w - 1, h - 1, 16, 16);
        g2.setColor(TemaManager.borde());
        g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);

        if (datos.isEmpty()) {
            dibujarVacio(g2, w, h);
            g2.dispose();
            return;
        }

        double maximo = datos.values().stream()
                .flatMapToDouble(v -> java.util.stream.DoubleStream.of(v[0], v[1]))
                .max().orElse(1);
        if (maximo == 0) maximo = 1;
        maximo *= 1.15;

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        int numLineas = 4;
        for (int i = 0; i <= numLineas; i++) {
            int y = margenSup + altoGrafico - (altoGrafico * i / numLineas);
            g2.setColor(TemaManager.borde());
            g2.drawLine(margenIzq, y, margenIzq + anchoGrafico, y);

            double valor = maximo * i / numLineas;
            String etiqueta = String.format("%.0f", valor);
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(TemaManager.textoSecundario());
            g2.drawString(etiqueta, margenIzq - fm.stringWidth(etiqueta) - 8, y + 4);
        }

        int numGrupos = datos.size();
        int anchoGrupo = anchoGrafico / numGrupos;
        int anchoBarra = Math.min(22, anchoGrupo / 3);

        int x = margenIzq;
        for (Map.Entry<String, double[]> entry : datos.entrySet()) {
            double ingresos = entry.getValue()[0];
            double gastos    = entry.getValue()[1];

            int centroGrupo = x + anchoGrupo / 2;
            int altoIngreso = (int) (altoGrafico * (ingresos / maximo));
            int altoGasto    = (int) (altoGrafico * (gastos / maximo));
            int yBase = margenSup + altoGrafico;

            g2.setColor(TemaManager.exito());
            g2.fill(new RoundRectangle2D.Double(
                centroGrupo - anchoBarra - 2, yBase - altoIngreso, anchoBarra, altoIngreso, 6, 6));

            g2.setColor(TemaManager.peligro());
            g2.fill(new RoundRectangle2D.Double(
                centroGrupo + 2, yBase - altoGasto, anchoBarra, altoGasto, 6, 6));

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(TemaManager.textoSecundario());
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(entry.getKey(), centroGrupo - fm.stringWidth(entry.getKey()) / 2, yBase + 18);

            x += anchoGrupo;
        }

        dibujarLeyenda(g2, w);
        g2.dispose();
    }

    private void dibujarLeyenda(Graphics2D g2, int w) {
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        int x = w - 170, y = 14;

        g2.setColor(TemaManager.exito());
        g2.fillRoundRect(x, y, 12, 12, 4, 4);
        g2.setColor(TemaManager.textoNormal());
        g2.drawString("Ingresos", x + 18, y + 11);

        g2.setColor(TemaManager.peligro());
        g2.fillRoundRect(x + 85, y, 12, 12, 4, 4);
        g2.setColor(TemaManager.textoNormal());
        g2.drawString("Gastos", x + 103, y + 11);
    }

    private void dibujarVacio(Graphics2D g2, int w, int h) {
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g2.setColor(TemaManager.textoSecundario());
        String texto = "Sin datos suficientes";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(texto, (w - fm.stringWidth(texto)) / 2, h / 2);
    }
}