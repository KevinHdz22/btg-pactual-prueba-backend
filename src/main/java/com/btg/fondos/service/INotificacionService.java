package com.btg.fondos.service;

import com.btg.fondos.model.Cliente;
import com.btg.fondos.model.Fondo;

/**
 * Interfaz que define las operaciones de notificación a clientes.
 */
public interface INotificacionService {

    /**
     * Envía una notificación de forma asíncrona según la preferencia del cliente.
     *
     * @param cliente cliente a notificar
     * @param fondo fondo asociado a la notificación
     * @param preferenciaElegida tipo de notificación (EMAIL o SMS)
     */
    void notificarAsync(Cliente cliente, Fondo fondo, String preferenciaElegida);
}
