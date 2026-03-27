package com.btg.fondos.model;

import lombok.*;

import java.math.BigDecimal;
/**
 * Representa un fondo dentro del sistema.
 * Contiene información básica como identificación, nombre, monto mínimo y categoría.
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Fondo {
    private String fondoId;
    private String nombre;
    private BigDecimal montoMinimo;
    private String categoria;
}