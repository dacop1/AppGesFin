package com.dacarex.capital.exception;

public class EntidadNoEncontradaException extends DacarexException {

    public EntidadNoEncontradaException(String entidad, Object id) {
        super("No se encontro " + entidad + " con id: " + id);
    }
}