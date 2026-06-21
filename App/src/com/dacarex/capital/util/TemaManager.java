package com.dacarex.capital.util;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;

import java.awt.Color;

public class TemaManager {

    private static boolean modoOscuro = false;

    public static void toggle() {
        modoOscuro = !modoOscuro;
        aplicarLaf();
    }

    public static void aplicarLaf() {
        if (modoOscuro) {
            FlatDarkLaf.setup();
        } else {
            FlatLightLaf.setup();
        }
        FlatLaf.updateUI();
    }

    public static boolean isModoOscuro() {
        return modoOscuro;
    }

    // ── Fondos ──
    public static Color fondoApp() {
        return modoOscuro ? new Color(18, 18, 28) : new Color(244, 246, 250);
    }

    public static Color fondoTarjeta() {
        return modoOscuro ? new Color(30, 30, 45) : Color.WHITE;
    }

    public static Color fondoSidebar() {
        return modoOscuro ? new Color(14, 14, 24) : new Color(28, 28, 45);
    }

    public static Color fondoHover() {
        return modoOscuro ? new Color(45, 45, 65) : new Color(235, 238, 245);
    }

    // ── Textos ──
    public static Color textoTitulo() {
        return modoOscuro ? new Color(230, 230, 245) : new Color(25, 25, 40);
    }

    public static Color textoNormal() {
        return modoOscuro ? new Color(190, 190, 210) : new Color(70, 70, 90);
    }

    public static Color textoSecundario() {
        return modoOscuro ? new Color(130, 130, 155) : new Color(130, 130, 145);
    }

    // ── Bordes ──
    public static Color borde() {
        return modoOscuro ? new Color(55, 55, 80) : new Color(225, 227, 235);
    }

    // ── Colores fijos de acento / semantica ──
    public static Color acento()    { return new Color(94, 124, 255); }
    public static Color exito()     { return new Color(40, 199, 111); }
    public static Color peligro()   { return new Color(240, 82, 82);  }
    public static Color aviso()     { return new Color(255, 159, 67); }
    public static Color info()      { return new Color(23, 162, 184); }

    // Paleta para graficos (categorias)
    public static Color[] paletaGrafico() {
        return new Color[]{
            new Color(94, 124, 255),
            new Color(40, 199, 111),
            new Color(255, 159, 67),
            new Color(240, 82, 82),
            new Color(23, 162, 184),
            new Color(155, 89, 182),
            new Color(241, 196, 15),
            new Color(52, 152, 219)
        };
    }
}