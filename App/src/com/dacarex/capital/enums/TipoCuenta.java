package com.dacarex.capital.enums;

public enum TipoCuenta {
    PERSONAL("Personal"),
    EMPRESA("Empresa");

    private final String valor;

    TipoCuenta(String valor) { this.valor = valor; }

    public String getValor() { return valor; }

    @Override
    public String toString() { return valor; }
}