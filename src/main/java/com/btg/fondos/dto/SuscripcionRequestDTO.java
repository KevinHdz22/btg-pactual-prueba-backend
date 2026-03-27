package com.btg.fondos.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para manejo de los request de suscripciones
 *
 * @since 1.0.0
 */
@Getter
@Setter
@Builder
public class SuscripcionRequestDTO {

    @NotBlank(message = "El fondoId es obligatorio")
    private String fondoId;

    @NotBlank(message = "La preferencia de notificación es obligatoria (EMAIL o SMS)")
    private String notificacionPreferencia;
}