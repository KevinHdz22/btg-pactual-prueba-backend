package com.btg.fondos.controller;

import com.btg.fondos.dto.TransaccionDTO;
import com.btg.fondos.service.ITransaccionService;
import com.btg.fondos.service.implementation.TransaccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
/**
 * Clase donde se implementan servicios REST para la gestión de las transacciones de los usuarios
 */
@RestController
@RequestMapping("/api/v1/transacciones")
@RequiredArgsConstructor
public class TransaccionController {

    private final ITransaccionService transaccionService;

    /**
     * Servicio para obtener el historial de transacciones de un usuario
     * @param jwt objeto con información de la sesión del usuario
     * @return historial de transacciones
     */
    @GetMapping
    //@PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<TransaccionDTO>> historial(
            @AuthenticationPrincipal Jwt jwt) {

        String clienteId = (jwt != null) ? jwt.getSubject() : "cliente-123";
        return ResponseEntity.ok(transaccionService.obtenerHistorial(clienteId));
    }
}