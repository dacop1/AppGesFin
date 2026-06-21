package com.dacarex.capital.vista.componentes;

import com.dacarex.capital.util.TemaManager;

import javax.swing.*;
import java.awt.*;

public class TarjetaKPI extends JPanel {

    private String titulo;
    private String valor;
    private Color colorValor;
    private String icono;

    public TarjetaKPI(String titulo, String valor, Color colorValor, String icono) {
        this.titulo = titulo;
        this.valor = valor;
        this.colorValor = colorValor;
        this.icono = icono;
        setOpaque(false);
        setPreferredSize(new Dimension(220, 110));
    }

    public void actualizar(String valor, Color colorValor) {
        this.valor = valor;
        this.colorValor = colorValor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Fondo con esquinas redondeadas
        g2.setColor(TemaManager.fondoTarjeta());
        g2.fillRoundRect(0, 0, w - 1, h - 1, 18, 18);

        g2.setColor(TemaManager.borde());
        g2.drawRoundRect(0, 0, w - 1, h - 1, 18, 18);

        // Barra de acento lateral
        g2.setColor(colorValor);
        g2.fillRoundRect(0, 0, 6, h, 18, 18);

        // Icono
        g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        g2.setColor(colorValor);
        g2.drawString(icono, 20, 38);

        // Titulo
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g2.setColor(TemaManager.textoSecundario());
        g2.drawString(titulo, 20, 62);

        // Valor
        g2.setFont(new Font("Segoe UI", Font.BOLD, 24));
        g2.setColor(colorValor);
        g2.drawString(valor, 20, 92);

        g2.dispose();
    }
}