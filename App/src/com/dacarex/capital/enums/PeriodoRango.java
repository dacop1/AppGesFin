package com.dacarex.capital.enums;

import java.time.LocalDate;

public enum PeriodoRango {

    SEMANA("Ultima semana", Granularidad.DIA),
    MES("Ultimo mes", Granularidad.DIA),
    TRES_MESES("Ultimos 3 meses", Granularidad.MES),
    SEIS_MESES("Ultimos 6 meses", Granularidad.MES),
    ANIO("Ultimo año", Granularidad.MES),
    DOS_ANIOS("Ultimos 2 años", Granularidad.MES),
    TRES_ANIOS("Ultimos 3 años", Granularidad.ANIO),
    CUATRO_ANIOS("Ultimos 4 años", Granularidad.ANIO),
    CINCO_ANIOS("Ultimos 5 años", Granularidad.ANIO),
    TODO("Toda la vida", Granularidad.ANIO);

    public enum Granularidad { DIA, MES, ANIO }

    private final String etiqueta;
    private final Granularidad granularidad;

    PeriodoRango(String etiqueta, Granularidad granularidad) {
        this.etiqueta = etiqueta;
        this.granularidad = granularidad;
    }

    public String getEtiqueta() { return etiqueta; }

    public Granularidad granularidad() { return granularidad; }

    @Override
    public String toString() { return etiqueta; }

    public LocalDate fechaInicio() {
        LocalDate hoy = LocalDate.now();
        return switch (this) {
            case SEMANA       -> hoy.minusWeeks(1);
            case MES          -> hoy.minusMonths(1);
            case TRES_MESES   -> hoy.minusMonths(3);
            case SEIS_MESES   -> hoy.minusMonths(6);
            case ANIO         -> hoy.minusYears(1);
            case DOS_ANIOS    -> hoy.minusYears(2);
            case TRES_ANIOS   -> hoy.minusYears(3);
            case CUATRO_ANIOS -> hoy.minusYears(4);
            case CINCO_ANIOS  -> hoy.minusYears(5);
            case TODO         -> null;
        };
    }
}