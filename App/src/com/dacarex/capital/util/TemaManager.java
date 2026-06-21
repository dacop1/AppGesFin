package com.dacarex.capital.util;

public class TemaManager {

    private static boolean modoOscuro = false;

    public static void toggle() {
        modoOscuro = !modoOscuro;
    }

    public static boolean isModoOscuro() {
        return modoOscuro;
    }

    public static String getHojaEstilos() {
        String ruta = modoOscuro
            ? "/com/dacarex/capital/vista/theme-dark.css"
            : "/com/dacarex/capital/vista/theme-light.css";
        return TemaManager.class.getResource(ruta).toExternalForm();
    }
}