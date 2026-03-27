package com.btg.fondos.service;

import com.btg.fondos.dto.TransaccionDTO;

import java.util.List;
/**
 * Interfaz que define las operaciones relacionadas con las transacciones.
 */
public interface ITransaccionService {

    /**
     * Obtiene el historial de transacciones de un cliente.
     *
     * @param clienteId identificador del cliente
     * @return lista de transacciones en formato DTO
     */
    List<TransaccionDTO> obtenerHistorial(String clienteId);
}
