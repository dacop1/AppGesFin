package com.dacarex.capital.exception;

import java.util.List;

public class ValidacionException extends DacarexException {

    private final List<String> errores;

    public ValidacionException(List<String> errores) {
        super("Error de validacion: " + String.join(", ", errores));
        this.errores = errores;
    }

    public ValidacionException(String mensaje) {
        super(mensaje);
        this.errores = List.of(mensaje);
    }

    public List<String> getErrores() {
        return errores;
    }
}