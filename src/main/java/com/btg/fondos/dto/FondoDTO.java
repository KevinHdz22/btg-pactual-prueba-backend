package com.btg.fondos.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO para manejo de los fondos
 *
 * @since 1.0.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FondoDTO {

    private String fondoId;
    private String nombre;
    private BigDecimal montoMinimo;
    private String categoria;
}