package com.dacarex.capital.vista.componentes;

import com.dacarex.capital.util.TemaManager;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class PanelGraficoTarta extends JPanel {

    private Map<String, Double> datos = new LinkedHashMap<>();

    public PanelGraficoTarta() {
        setOpaque(false);
        setPreferredSize(new Dimension(380, 280));
    }

    public void setDatos(Map<String, Double> datos) {
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

        g2.setColor(TemaManager.fondoTarjeta());
        g2.fillRoundRect(0, 0, w - 1, h - 1, 16, 16);
        g2.setColor(TemaManager.borde());
        g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);

        double total = datos.values().stream().mapToDouble(Double::doubleValue).sum();

        if (datos.isEmpty() || total == 0) {
            dibujarVacio(g2, w, h);
            g2.dispose();
            return;
        }

        int diametro = Math.min(h - 40, (w / 2) - 40);
        int cx = 30 + diametro / 2;
        int cy = h / 2;

        Color[] paleta = TemaManager.paletaGrafico();
        int colorIdx = 0;
        double anguloInicio = -90;

        int leyendaX = 30 + diametro + 30;
        int leyendaY = (h - datos.size() * 22) / 2;

        for (Map.Entry<String, Double> entry : datos.entrySet()) {
            double porcentaje = entry.getValue() / total;
            double angulo = porcentaje * 360;

            Color color = paleta[colorIdx % paleta.length];
            g2.setColor(color);
            g2.fillArc(cx - diametro / 2, cy - diametro / 2, diametro, diametro,
                    (int) Math.round(anguloInicio), (int) Math.round(angulo));

            g2.setColor(color);
            g2.fillRoundRect(leyendaX, leyendaY, 12, 12, 4, 4);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(TemaManager.textoNormal());
            String texto = String.format("%s (%.1f%%)", entry.getKey(), porcentaje * 100);
            g2.drawString(texto, leyendaX + 18, leyendaY + 11);

            leyendaY += 22;
            anguloInicio += angulo;
            colorIdx++;
        }

        int agujero = (int) (diametro * 0.55);
        g2.setColor(TemaManager.fondoTarjeta());
        g2.fillOval(cx - agujero / 2, cy - agujero / 2, agujero, agujero);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.setColor(TemaManager.textoTitulo());
        String totalTxt = String.format("%.0f EUR", total);
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(totalTxt, cx - fm.stringWidth(totalTxt) / 2, cy + 5);

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