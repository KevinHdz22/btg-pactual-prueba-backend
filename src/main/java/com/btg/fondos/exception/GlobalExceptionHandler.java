package com.btg.fondos.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Maneja la excepción cuando no se encuentra un elemento.
     *
     * @param ex excepción lanzada
     * @return respuesta con estado 404 (NOT_FOUND)
     */
    @ExceptionHandler(ElementoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleElementNotFound(ElementoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("ELEMENTO_NO_ENCONTRADO", ex.getMessage()));
    }


    /**
     * Maneja errores generales de la aplicación.
     *
     * @param ex excepción lanzada
     * @return respuesta con estado 500 (INTERNAL_SERVER_ERROR)
     */
    @ExceptionHandler(ErrorGeneralException.class)
    public ResponseEntity<ErrorResponse> handleErrorGeneral(ErrorGeneralException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("ERROR_GENERAL", ex.getMessage()));
    }


    /**
     * Maneja errores de acceso denegado.
     *
     * @param ex excepción lanzada
     * @return respuesta con estado 401 (UNAUTHORIZED)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccesDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("ACCESO_DENEGADO", "No tiene permisos para esta operación"));
    }

    /**
     * Maneja errores de validación en los datos de entrada.
     *
     * @param ex excepción lanzada
     * @return respuesta con estado 400 (BAD_REQUEST)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("VALIDACION_FALLIDA", mensaje));
    }

    /**
     * Maneja cualquier excepción no controlada.
     *
     * @param ex excepción lanzada
     * @return respuesta con estado 500 (INTERNAL_SERVER_ERROR)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Error inesperado: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("ERROR_INTERNO", "Ha ocurrido un error inesperado"));
    }
}

