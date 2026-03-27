package com.btg.fondos.exception;

public class ElementoNoEncontradoException extends RuntimeException {

    /**
     * Constructor que permite crear la excepción con un mensaje descriptivo.
     *
     * @param mensaje Descripción del error indicando qué elemento no fue encontrado.
     */
    public ElementoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}