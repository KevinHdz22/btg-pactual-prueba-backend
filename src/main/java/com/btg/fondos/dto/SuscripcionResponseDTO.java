package com.btg.fondos.dto;

import lombok.*;

import java.math.BigDecimal;
/**
 * DTO para gestion de respuestas de las operaciones sobre
 * los fondos
 *
 * @since 1.0.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuscripcionResponseDTO {

    private String transaccionId;
    private String fondoId;
    private String nombreFondo;
    private String tipo;
    private BigDecimal monto;
    private BigDecimal saldoRestante;
    private String mensaje;
}