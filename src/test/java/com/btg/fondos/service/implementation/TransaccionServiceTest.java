package com.btg.fondos.service.implementation;

import com.btg.fondos.dto.TransaccionDTO;
import com.btg.fondos.mapper.FondoMapper;
import com.btg.fondos.mapper.TransaccionMapper;
import com.btg.fondos.model.Transaccion;
import com.btg.fondos.repository.TransaccionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para {@link TransaccionService}. Utiliza Mockito para simular
 * dependencias y verificar el comportamiento de los métodos.
 *
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class TransaccionServiceTest {

    @Mock
    private TransaccionRepository transaccionRepo;

    @Spy
    private TransaccionMapper transaccionMapper = Mappers.getMapper(TransaccionMapper.class);

    @InjectMocks
    private TransaccionService transaccionService;

    @Test
    @DisplayName("Obtener historial - Exitoso")
    void obtenerHistorialConTransaccionesDebeRetornarListaDTO() {
        String clienteId = "CLI-123";

        Transaccion txnBd = Transaccion.builder()
                .transaccionId("TXN-001")
                .fondoId("FND-1")
                .tipo("APERTURA")
                .monto(new BigDecimal("500000"))
                .build();

        List<Transaccion> transaccionesBd = List.of(txnBd);


        when(transaccionRepo.findByClienteId(clienteId)).thenReturn(transaccionesBd);

        List<TransaccionDTO> resultado = transaccionService.obtenerHistorial(clienteId);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("TXN-001", resultado.getFirst().getTransaccionId());

        verify(transaccionRepo, times(1)).findByClienteId(clienteId);
        verify(transaccionMapper, times(1)).toDTOList(transaccionesBd);
    }


    @Test
    @DisplayName("Obtener historial - Lista vacia")
    void obtenerHistorialSinTransaccionesDebeRetornarListaVacia() {
        String clienteNuevoId = "CLI-999";

        when(transaccionRepo.findByClienteId(clienteNuevoId)).thenReturn(Collections.emptyList());
        when(transaccionMapper.toDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

        List<TransaccionDTO> resultado = transaccionService.obtenerHistorial(clienteNuevoId);

        assertNotNull(resultado, "El resultado no debe ser nulo, debe ser una lista vacía");
        assertTrue(resultado.isEmpty(), "La lista de historial debe estar vacía");

        verify(transaccionRepo, times(1)).findByClienteId(clienteNuevoId);
        verify(transaccionMapper, times(1)).toDTOList(Collections.emptyList());
    }
}