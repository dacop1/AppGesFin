package com.dacarex.capital.vista.componentes;

import com.dacarex.capital.util.TemaManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;

public class PanelGraficoLineas extends JPanel {

    private List<double[]> puntos = new ArrayList<>();

    public PanelGraficoLineas() {
        setOpaque(false);
        setPreferredSize(new Dimension(400, 240));
    }

    public void setPuntos(List<double[]> puntos) {
        this.puntos = puntos;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        g2.setColor(TemaManager.fondoTarjeta());
        g2.fillRoundRect(0, 0, w - 1, h - 1, 16, 16);
        g2.setColor(TemaManager.borde());
        g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);

        if (puntos.size() < 2) {
            dibujarVacio(g2, w, h);
            g2.dispose();
            return;
        }

        int margenIzq = 60, margenDer = 20, margenSup = 25, margenInf = 30;
        int anchoGrafico = w - margenIzq - margenDer;
        int altoGrafico   = h - margenSup - margenInf;

        double minY = puntos.stream().mapToDouble(p -> p[1]).min().orElse(0);
        double maxY = puntos.stream().mapToDouble(p -> p[1]).max().orElse(1);
        if (minY == maxY) { minY -= 1; maxY += 1; }
        double rango = maxY - minY;

        double maxX = puntos.get(puntos.size() - 1)[0];
        if (maxX == 0) maxX = 1;

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        int lineas = 4;
        for (int i = 0; i <= lineas; i++) {
            int y = margenSup + altoGrafico - (altoGrafico * i / lineas);
            g2.setColor(TemaManager.borde());
            g2.drawLine(margenIzq, y, margenIzq + anchoGrafico, y);

            double valor = minY + (rango * i / lineas);
            String etiqueta = String.format("%.0f", valor);
            FontMetrics fm = g2.getFontMetrics();
            g2.setColor(TemaManager.textoSecundario());
            g2.drawString(etiqueta, margenIzq - fm.stringWidth(etiqueta) - 8, y + 4);
        }

        GeneralPath linea = new GeneralPath();
        GeneralPath area  = new GeneralPath();
        boolean primero = true;
        float ultimaX = margenIzq, ultimaY = margenSup;

        for (double[] p : puntos) {
            float px = (float) (margenIzq + (p[0] / maxX) * anchoGrafico);
            float py = (float) (margenSup + altoGrafico - ((p[1] - minY) / rango) * altoGrafico);

            if (primero) {
                linea.moveTo(px, py);
                area.moveTo(px, margenSup + altoGrafico);
                area.lineTo(px, py);
                primero = false;
            } else {
                linea.lineTo(px, py);
                area.lineTo(px, py);
            }
            ultimaX = px;
            ultimaY = py;
        }
        area.lineTo(ultimaX, margenSup + altoGrafico);
        area.closePath();

        GradientPaint degradado = new GradientPaint(
            0, margenSup, new Color(94, 124, 255, 90),
            0, margenSup + altoGrafico, new Color(94, 124, 255, 5)
        );
        g2.setPaint(degradado);
        g2.fill(area);

        g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(TemaManager.acento());
        g2.draw(linea);

        for (double[] p : puntos) {
            float px = (float) (margenIzq + (p[0] / maxX) * anchoGrafico);
            float py = (float) (margenSup + altoGrafico - ((p[1] - minY) / rango) * altoGrafico);
            g2.setColor(TemaManager.fondoTarjeta());
            g2.fillOval((int) px - 4, (int) py - 4, 8, 8);
            g2.setColor(TemaManager.acento());
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval((int) px - 4, (int) py - 4, 8, 8);
        }

        g2.dispose();
    }

    private void dibujarVacio(Graphics2D g2, int w, int h) {
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g2.setColor(TemaManager.textoSecundario());
        String texto = "Sin datos suficientes";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(texto, (w - fm.stringWidth(texto)) / 2, h / 2);
    }
}