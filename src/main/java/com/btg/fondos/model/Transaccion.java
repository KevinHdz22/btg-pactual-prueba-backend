package com.btg.fondos.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Representa una transacción realizada por un cliente sobre un fondo.
 * Contiene información del cliente, fondo, tipo de transacción, monto y fecha.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaccion {
    private String clienteId;
    private String sk;
    private String transaccionId;
    private String fondoId;
    private String nombreFondo;
    private String tipo;
    private BigDecimal monto;
    private Instant fechaCreacion;
}