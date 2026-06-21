package com.dacarex.capital.enums;

public enum TipoMovimiento {
    INGRESO("Ingreso"),
    GASTO("Gasto");

    private final String valor;

    TipoMovimiento(String valor) { this.valor = valor; }

    public String getValor() { return valor; }

    @Override
    public String toString() { return valor; }
}