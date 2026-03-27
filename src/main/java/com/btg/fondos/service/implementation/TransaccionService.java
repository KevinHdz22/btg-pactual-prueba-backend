package com.btg.fondos.service.implementation;

import com.btg.fondos.dto.TransaccionDTO;
import com.btg.fondos.mapper.TransaccionMapper;
import com.btg.fondos.model.Transaccion;
import com.btg.fondos.repository.TransaccionRepository;
import com.btg.fondos.service.ITransaccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementación del servicio de transacciones.
 * Permite consultar el historial de transacciones de un cliente.
 */
@Service
@RequiredArgsConstructor
public class TransaccionService implements ITransaccionService {

    private final TransaccionRepository transaccionRepo;
    private final TransaccionMapper transaccionMapper;

    /**
     * Obtiene el historial de transacciones de un cliente.
     *
     * @param clienteId identificador del cliente
     * @return lista de transacciones en formato DTO
     */
    @Override
    public List<TransaccionDTO> obtenerHistorial(String clienteId) {
        List<Transaccion> transacciones = transaccionRepo.findByClienteId(clienteId);
        return transaccionMapper.toDTOList(transacciones);
    }
}
