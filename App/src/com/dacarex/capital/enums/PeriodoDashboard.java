package com.dacarex.capital.enums;

public enum PeriodoDashboard {
    MES("Mes"),
    TRIMESTRE("Trimestre"),
    ANIO("Año"),
    TODO("Todo");

    private final String valor;

    PeriodoDashboard(String valor) { this.valor = valor; }

    public String getValor() { return valor; }

    @Override
    public String toString() { return valor; }
}