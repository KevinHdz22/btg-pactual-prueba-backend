package com.btg.fondos.exception;

public class ErrorGeneralException extends RuntimeException {
    /**
     * Constructor que permite crear la excepción con un mensaje descriptivo.
     *
     * @param error Descripción del error ocurrido.
     */
    public ErrorGeneralException(String error) {
        super(error);
    }
}
