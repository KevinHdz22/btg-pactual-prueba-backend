package com.btg.fondos.model;

import lombok.*;

import java.math.BigDecimal;
/**
 * Representa un cliente dentro del sistema.
 * Contiene información básica de contacto, saldo y preferencias de notificación.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {
    private String clienteId;
    private String email;
    private String telefono;
    private BigDecimal saldo;
    private String notificacionPreferencia;
}