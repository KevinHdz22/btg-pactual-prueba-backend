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
import com.btg.fondos.service.IFondoService;
import com.btg.fondos.service.INotificacionService;
import com.btg.fondos.utilities.Constantes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Implementación del servicio de gestión de fondos.
 * Contiene la lógica para suscripción, cancelación y consulta de fondos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FondoService implements IFondoService {

    private final ClienteRepository clienteRepo;
    private final FondoRepository fondoRepo;
    private final TransaccionRepository txnRepo;
    private final INotificacionService notificacionService;
    private final FondoMapper fondoMapper;


    /**
     * Realiza la suscripción de un cliente a un fondo.
     *
     * @param clienteId identificador del cliente
     * @param request datos de la solicitud de suscripción
     * @return respuesta con el resultado de la operación
     */
    @Override
    public SuscripcionResponseDTO suscribir(String clienteId, SuscripcionRequestDTO request) {
        Cliente cliente = obtenerClienteOrThrow(clienteId);
        Fondo fondo = obtenerFondoOrThrow(request.getFondoId());
        String mensajeError;

        if (cliente.getSaldo().compareTo(fondo.getMontoMinimo()) < 0) {
            mensajeError = MessageFormat.format(Constantes.MSJ_ERROR_SALDO_INSUFICIENTE, fondo.getNombre());
            throw new ErrorGeneralException(mensajeError);
        }

        if (tieneSuscripcionActiva(clienteId, fondo.getFondoId())) {
            mensajeError = MessageFormat.format(Constantes.MSJ_ERROR_SUSCRIPCION_ACTIVA, fondo.getNombre());
            throw new ErrorGeneralException(mensajeError);
        }

        BigDecimal nuevoSaldo = cliente.getSaldo().subtract(fondo.getMontoMinimo());

        clienteRepo.actualizarSaldo(clienteId, nuevoSaldo);
        Transaccion txn = registrarTransaccion(clienteId, fondo, Constantes.FUNCION_APERTURA);

        notificacionService.notificarAsync(cliente, fondo, request.getNotificacionPreferencia());
        log.info("Suscripción exitosa: cliente={} fondo={} txn={}", clienteId, fondo.getFondoId(), txn.getTransaccionId());

        return construirRespuesta(txn, fondo, nuevoSaldo, Constantes.ACCION_APERTURA);
    }

    /**
     * Cancela la suscripción de un cliente a un fondo.
     *
     * @param clienteId identificador del cliente
     * @param fondoId identificador del fondo
     * @return respuesta con el resultado de la operación
     */
    @Override
    public SuscripcionResponseDTO cancelar(String clienteId, String fondoId) {
        Cliente cliente = obtenerClienteOrThrow(clienteId);
        Fondo fondo = obtenerFondoOrThrow(fondoId);

        if (!tieneSuscripcionActiva(clienteId, fondoId)) {
            String error = MessageFormat.format(Constantes.MSJ_ERROR_SIN_SUSCRIPCION_ACTIVA, fondo.getNombre());
            throw new ErrorGeneralException(error);
        }

        BigDecimal nuevoSaldo = cliente.getSaldo().add(fondo.getMontoMinimo());

        clienteRepo.actualizarSaldo(clienteId, nuevoSaldo);
        Transaccion txn = registrarTransaccion(clienteId, fondo, Constantes.FUNCION_CANCELACION);

        return construirRespuesta(txn, fondo, nuevoSaldo, Constantes.ACCION_CANCELACION);
    }

    /**
     * Obtiene la lista de fondos disponibles.
     *
     * @return lista de fondos en formato DTO
     */
    @Override
    public List<FondoDTO> listarFondos() {
        List<Fondo> fondos = fondoRepo.findAll();
        return fondoMapper.toDTOList(fondos);
    }


    /**
     * Obtiene un cliente o lanza excepción si no existe.
     */
    private Cliente obtenerClienteOrThrow(String clienteId) {
        return clienteRepo.findById(clienteId)
                .orElseThrow(() -> new ElementoNoEncontradoException(
                        MessageFormat.format(Constantes.MSJ_CLIENTE_NO_ENCONTRADO, clienteId)
                ));
    }

    /**
     * Obtiene un fondo o lanza excepción si no existe.
     */
    private Fondo obtenerFondoOrThrow(String fondoId) {
        return fondoRepo.findById(fondoId)
                .orElseThrow(() -> new ElementoNoEncontradoException(
                        MessageFormat.format(Constantes.MSJ_FONDO_NO_ENCONTRADO, fondoId)));
    }

    /**
     * Verifica si un cliente tiene una suscripción activa a un fondo.
     */
    private boolean tieneSuscripcionActiva(String clienteId, String fondoId) {
        List<Transaccion> historial = txnRepo.findByClienteId(clienteId);

        long aperturas = historial.stream()
                .filter(t -> t.getFondoId().equals(fondoId)
                        && Constantes.FUNCION_APERTURA.equals(t.getTipo()))
                .count();

        long cancelaciones = historial.stream()
                .filter(t -> t.getFondoId().equals(fondoId)
                        && Constantes.FUNCION_CANCELACION.equals(t.getTipo()))
                .count();

        return aperturas > cancelaciones;
    }


    /**
     * Registra una transacción para un cliente.
     */
    private Transaccion registrarTransaccion(String clienteId, Fondo fondo, String tipo) {
        Transaccion txn = Transaccion.builder()
                .transaccionId(UUID.randomUUID().toString())
                .clienteId(clienteId)
                .fondoId(fondo.getFondoId())
                .nombreFondo(fondo.getNombre())
                .tipo(tipo)
                .monto(fondo.getMontoMinimo())
                .fechaCreacion(Instant.now())
                .build();

        txnRepo.guardar(txn);
        return txn;
    }

    /**
     * Construye la respuesta de una operación de suscripción o cancelación.
     */
    private SuscripcionResponseDTO construirRespuesta(Transaccion txn, Fondo fondo, BigDecimal saldoRestante, String proceso) {
        String mensajeRespuesta = MessageFormat.format(Constantes.MSJ_PROCESO_EXITOSO, proceso, fondo.getNombre());

        return SuscripcionResponseDTO.builder()
                .transaccionId(txn.getTransaccionId())
                .fondoId(fondo.getFondoId())
                .nombreFondo(fondo.getNombre())
                .monto(fondo.getMontoMinimo())
                .saldoRestante(saldoRestante)
                .mensaje(mensajeRespuesta)
                .build();
    }
}