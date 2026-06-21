package com.dacarex.capital.util;

import java.text.NumberFormat;
import java.util.Locale;

public class Formato {

    private static final NumberFormat MONEDA =
        NumberFormat.getCurrencyInstance(new Locale("es", "ES"));

    public static String moneda(double valor) {
        return MONEDA.format(valor);
    }
}