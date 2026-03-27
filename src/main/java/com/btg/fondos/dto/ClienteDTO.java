package com.btg.fondos.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO para manejo de los clientes
 *
 * @since 1.0.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    private String clienteId;
    private String email;
    private String telefono;
    private BigDecimal saldo;
    private String notificacionPreferencia;
}