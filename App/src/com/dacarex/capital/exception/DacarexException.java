package com.dacarex.capital.exception;

public class DacarexException extends RuntimeException {

    public DacarexException(String mensaje) {
        super(mensaje);
    }

    public DacarexException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}