package com.btg.fondos.controller;

import com.btg.fondos.dto.FondoDTO;
import com.btg.fondos.dto.SuscripcionRequestDTO;
import com.btg.fondos.dto.SuscripcionResponseDTO;
import com.btg.fondos.service.IFondoService;
import com.btg.fondos.service.implementation.FondoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Clase donde se implementan servicios REST para la gestión de los fondos
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/fondos")
@RequiredArgsConstructor
@Validated
public class FondoController {

    private final IFondoService fondoService;

    /**
     * Servicio que permite realizar la suscripción a los fondos
     *
     * @param request datos de la petición
     * @param jwt objeto con información de la sesión del usuario
     * @return objeto SuscripcionResponseDTO con respuesta de la suscripción
     */
    @PostMapping("/suscribir")
    //@PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<SuscripcionResponseDTO> suscribir(
            @RequestBody @Valid SuscripcionRequestDTO request,
            @AuthenticationPrincipal Jwt jwt) {

        String clienteId = (jwt != null) ? jwt.getSubject() : "cliente-123";
        SuscripcionResponseDTO response = fondoService.suscribir(clienteId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Servicio que permite cancelar la suscripción a los fondos
     *
     * @param fondoId identificador del fondo
     * @param jwt objeto con información de la sesión del usuario
     * @return objeto SuscripcionResponseDTO con respuesta de la cancelación
     */
    @DeleteMapping("/cancelar/{fondoId}")
    //@PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<SuscripcionResponseDTO> cancelar(
            @PathVariable String fondoId,
            @AuthenticationPrincipal Jwt jwt) {

        String clienteId = (jwt != null) ? jwt.getSubject() : "cliente-123";
        SuscripcionResponseDTO response = fondoService.cancelar(clienteId, fondoId);
        return ResponseEntity.ok(response);
    }

    /**
     * Servicio para listar los fondos parametrizados
     *
     * @return listado de fondos parametrizados
     */
    @GetMapping
    //@PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<List<FondoDTO>> listarFondos() {
        return ResponseEntity.ok(fondoService.listarFondos());
    }
}

