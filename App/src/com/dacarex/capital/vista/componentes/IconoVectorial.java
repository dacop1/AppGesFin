package com.dacarex.capital.vista.componentes;

import javax.swing.Icon;
import java.awt.*;

public class IconoVectorial implements Icon {

    public enum Tipo { CASA, MONEDA, ETIQUETA, GRAFICO, LUNA, SOL, SALIR }

    private final Tipo tipo;
    private final Color color;
    private final int tamano;

    public IconoVectorial(Tipo tipo, Color color, int tamano) {
        this.tipo = tipo;
        this.color = color;
        this.tamano = tamano;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.translate(x, y);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (tipo) {
            case CASA -> dibujarCasa(g2);
            case MONEDA -> dibujarMoneda(g2);
            case ETIQUETA -> dibujarEtiqueta(g2);
            case GRAFICO -> dibujarGrafico(g2);
            case LUNA -> dibujarLuna(g2);
            case SOL -> dibujarSol(g2);
            case SALIR -> dibujarSalir(g2);
        }
        g2.dispose();
    }

    private void dibujarCasa(Graphics2D g2) {
        int s = tamano;
        Polygon tejado = new Polygon();
        tejado.addPoint(s / 2, 1);
        tejado.addPoint(s - 1, s / 2);
        tejado.addPoint(1, s / 2);
        g2.fill(tejado);
        g2.fillRect(s / 4, s / 2, s / 2, s / 2 - 1);
    }

    private void dibujarMoneda(Graphics2D g2) {
        int s = tamano;
        g2.drawOval(1, 1, s - 2, s - 2);
        g2.setFont(new Font("Arial", Font.BOLD, s - 6));
        g2.drawString("$", s / 2 - 3, s / 2 + 4);
    }

    private void dibujarEtiqueta(Graphics2D g2) {
        int s = tamano;
        Polygon p = new Polygon();
        p.addPoint(1, s / 3);
        p.addPoint(s * 2 / 3, 1);
        p.addPoint(s - 1, s / 3);
        p.addPoint(s / 3, s - 1);
        g2.draw(p);
        g2.fillOval(s / 2 - 1, s / 4 - 1, 3, 3);
    }

    private void dibujarGrafico(Graphics2D g2) {
        int s = tamano;
        g2.drawLine(2, s - 2, 2, 2);
        g2.drawLine(2, s - 2, s - 2, s - 2);
        g2.fillRect(4, s - 6, 3, 4);
        g2.fillRect(8, s - 9, 3, 7);
        g2.fillRect(12, s - 12, 3, 10);
    }

    private void dibujarLuna(Graphics2D g2) {
        int s = tamano;
        g2.fillOval(1, 1, s - 2, s - 2);
        Composite original = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT));
        g2.fillOval(s / 3, 0, s - 2, s - 2);
        g2.setComposite(original);
    }

    private void dibujarSol(Graphics2D g2) {
        int s = tamano;
        int r = s / 3;
        g2.fillOval(s / 2 - r / 2, s / 2 - r / 2, r, r);
        for (int i = 0; i < 8; i++) {
            double ang = Math.toRadians(i * 45);
            int x1 = (int) (s / 2 + Math.cos(ang) * r);
            int y1 = (int) (s / 2 + Math.sin(ang) * r);
            int x2 = (int) (s / 2 + Math.cos(ang) * (s / 2 - 1));
            int y2 = (int) (s / 2 + Math.sin(ang) * (s / 2 - 1));
            g2.drawLine(x1, y1, x2, y2);
        }
    }

    private void dibujarSalir(Graphics2D g2) {
        int s = tamano;
        g2.drawRoundRect(2, 2, s / 2, s - 4, 3, 3);
        g2.drawLine(s / 2, s / 2, s - 2, s / 2);
        g2.drawLine(s - 6, s / 2 - 4, s - 2, s / 2);
        g2.drawLine(s - 6, s / 2 + 4, s - 2, s / 2);
    }

    @Override public int getIconWidth() { return tamano; }
    @Override public int getIconHeight() { return tamano; }
}