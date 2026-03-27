package com.btg.fondos.service;

import com.btg.fondos.dto.FondoDTO;
import com.btg.fondos.dto.SuscripcionRequestDTO;
import com.btg.fondos.dto.SuscripcionResponseDTO;

import java.util.List;
/**
 * Interfaz que define las operaciones relacionadas con la gestión de fondos.
 */
public interface IFondoService {

    /**
     * Realiza la suscripción de un cliente a un fondo.
     *
     * @param clienteId identificador del cliente
     * @param request datos de la solicitud de suscripción
     * @return respuesta con el resultado de la operación
     */
    SuscripcionResponseDTO suscribir(String clienteId, SuscripcionRequestDTO request);

    /**
     * Cancela la suscripción de un cliente a un fondo.
     *
     * @param clienteId identificador del cliente
     * @param fondoId identificador del fondo
     * @return respuesta con el resultado de la operación
     */
    SuscripcionResponseDTO cancelar(String clienteId, String fondoId);

    /**
     * Obtiene la lista de fondos disponibles.
     *
     * @return lista de fondos en formato DTO
     */
    List<FondoDTO> listarFondos();
}
