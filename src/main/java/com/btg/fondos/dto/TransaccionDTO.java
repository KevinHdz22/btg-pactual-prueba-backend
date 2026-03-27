package com.btg.fondos.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO para manejo de las transacciones
 *
 * @since 1.0.0
 */
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransaccionDTO {
    private String transaccionId;
    private String fondoId;
    private String nombreFondo;
    private String tipo;
    private BigDecimal monto;
    private Instant fechaCreacion;
}
