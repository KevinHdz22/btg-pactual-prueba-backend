package com.btg.fondos.service.implementation;

import com.btg.fondos.dto.FondoDTO;
import com.btg.fondos.dto.SuscripcionRequestDTO;
import com.btg.fondos.dto.SuscripcionResponseDTO;
import com.btg.fondos.exception.ElementoNoEncontradoException;
import com.btg.fondos.exception.ErrorGeneralException;
import com.btg.fondos.mapper.FondoMapper;
import com.btg.fondos.model.Cliente;
import com.btg.fondos.model.Fondo;
import com.btg.fondos.model.Transaccion;
import com.btg.fondos.repository.ClienteRepository;
import com.btg.fondos.repository.FondoRepository;
import com.btg.fondos.repository.TransaccionRepository;
import com.btg.fondos.service.INotificacionService;
import com.btg.fondos.utilities.Constantes;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para {@link FondoService}. Utiliza Mockito para simular
 * dependencias y verificar el comportamiento de los métodos.
 *
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class FondoServiceTest {

    @Mock
    private ClienteRepository clienteRepo;
    @Mock
    private FondoRepository fondoRepo;
    @Mock
    private TransaccionRepository txnRepo;
    @Mock
    private INotificacionService notificacionService;

    @Spy
    private FondoMapper fondoMapper = Mappers.getMapper(FondoMapper.class);

    @InjectMocks
    private FondoService fondoService;

    private Cliente clienteMock;
    private Fondo fondoMock;
    private SuscripcionRequestDTO requestMock;

    @BeforeEach
    void setUp() {
        clienteMock = Cliente.builder()
                .clienteId("CLI-123")
                .saldo(new BigDecimal("5000000"))
                .build();

        fondoMock = Fondo.builder()
                .fondoId("FND-1")
                .nombre("Fondo Acciones")
                .montoMinimo(new BigDecimal("1000000"))
                .build();

        requestMock = SuscripcionRequestDTO.builder()
                .fondoId("FND-1")
                .notificacionPreferencia("EMAIL")
                .build();
    }

    @Test
    @DisplayName("Suscribir fondo - Exitosamente")
    void suscribirFondoExitosamente() {
        when(clienteRepo.findById("CLI-123")).thenReturn(Optional.of(clienteMock));
        when(fondoRepo.findById("FND-1")).thenReturn(Optional.of(fondoMock));
        when(txnRepo.findByClienteId("CLI-123")).thenReturn(Collections.emptyList());

        SuscripcionResponseDTO response = fondoService.suscribir("CLI-123", requestMock);

        assertNotNull(response);
        assertEquals("FND-1", response.getFondoId());
        assertEquals(new BigDecimal("4000000"), response.getSaldoRestante());

        verify(clienteRepo, times(1)).actualizarSaldo("CLI-123", new BigDecimal("4000000"));
        verify(txnRepo, times(1)).guardar(any(Transaccion.class));
        verify(notificacionService, times(1)).notificarAsync(clienteMock, fondoMock, "EMAIL");
    }

    @Test
    @DisplayName("Suscribir fondo - Saldo insuficiente")
    void suscribirSaldoInsuficienteDebeLanzarExcepcion() {
        clienteMock.setSaldo(new BigDecimal("500000"));
        when(clienteRepo.findById("CLI-123")).thenReturn(Optional.of(clienteMock));
        when(fondoRepo.findById("FND-1")).thenReturn(Optional.of(fondoMock));

        assertThrows(ErrorGeneralException.class, () -> fondoService.suscribir("CLI-123", requestMock));

        verify(clienteRepo, never()).actualizarSaldo(anyString(), any());
        verify(txnRepo, never()).guardar(any());
    }


    @Test
    @DisplayName("Suscribir fondo - Cliente no encontrado")
    void suscribirClienteNoExisteDebeLanzarExcepcion() {
        when(clienteRepo.findById("CLI-999")).thenReturn(Optional.empty());

        assertThrows(ElementoNoEncontradoException.class, () -> fondoService.suscribir("CLI-999", requestMock));
    }

    @Test
    @DisplayName("Cancelar fondo - Exitosamente")
    void cancelarExitoDebeDevolverDineroAlSaldo() {
        when(clienteRepo.findById("CLI-123")).thenReturn(Optional.of(clienteMock));
        when(fondoRepo.findById("FND-1")).thenReturn(Optional.of(fondoMock));

        Transaccion txnApertura = Transaccion.builder().fondoId("FND-1").tipo(Constantes.FUNCION_APERTURA).build();
        when(txnRepo.findByClienteId("CLI-123")).thenReturn(List.of(txnApertura));

        SuscripcionResponseDTO response = fondoService.cancelar("CLI-123", "FND-1");

        assertNotNull(response);
        assertEquals(new BigDecimal("6000000"), response.getSaldoRestante());

        verify(clienteRepo, times(1)).actualizarSaldo("CLI-123", new BigDecimal("6000000"));
        verify(txnRepo, times(1)).guardar(any(Transaccion.class));
    }

    @Test
    @DisplayName("Cancelar fondo - Subscripción no activa")
    void cancelarSinSuscripcionActivaDebeLanzarExcepcion() {
        when(clienteRepo.findById("CLI-123")).thenReturn(Optional.of(clienteMock));
        when(fondoRepo.findById("FND-1")).thenReturn(Optional.of(fondoMock));
        when(txnRepo.findByClienteId("CLI-123")).thenReturn(Collections.emptyList());

        assertThrows(ErrorGeneralException.class, () -> fondoService.cancelar("CLI-123", "FND-1"));
        verify(clienteRepo, never()).actualizarSaldo(anyString(), any());
    }


    @Test
    @DisplayName("Listar fondos - Exitosamente")
    void listarFondosDebeRetornarListaDTO() {
        List<Fondo> fondosDb = List.of(fondoMock);
        FondoDTO dto = FondoDTO.builder().nombre("Fondo Acciones").build();

        when(fondoRepo.findAll()).thenReturn(fondosDb);
        when(fondoMapper.toDTOList(fondosDb)).thenReturn(List.of(dto));

        List<FondoDTO> resultado = fondoService.listarFondos();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals("Fondo Acciones", resultado.getFirst().getNombre());
        verify(fondoRepo, times(1)).findAll();
    }
}