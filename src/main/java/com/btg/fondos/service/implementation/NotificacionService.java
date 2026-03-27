package com.btg.fondos.service.implementation;

import com.btg.fondos.model.Cliente;
import com.btg.fondos.model.Fondo;
import com.btg.fondos.service.INotificacionService;
import com.btg.fondos.utilities.Constantes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.sns.SnsClient;

import java.text.MessageFormat;

/**
 * Implementación del servicio de notificaciones.
 * Permite enviar notificaciones por email o SMS según la preferencia del cliente.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionService implements INotificacionService {

    private final SnsClient snsClient;
    private final SesClient sesClient;

    /**
     * Envía una notificación de forma asíncrona según la preferencia del cliente.
     *
     * @param cliente cliente a notificar
     * @param fondo fondo asociado a la notificación
     * @param preferenciaElegida tipo de notificación (EMAIL o SMS)
     */
    @Override
    @Async
    public void notificarAsync(Cliente cliente, Fondo fondo, String preferenciaElegida) {
        try {
            if (Constantes.PREF_EMAIL.equalsIgnoreCase(preferenciaElegida)) {
                enviarEmail(cliente, fondo);
            } else {
                enviarSms(cliente, fondo);
            }
        } catch (Exception e) {
            log.error("Error enviando notificación a cliente={}: {}",
                    cliente.getClienteId(), e.getMessage());
        }
    }

    /**
     * Envía una notificación por correo electrónico.
     */
    private void enviarEmail(Cliente cliente, Fondo fondo) {
        String asunto = MessageFormat.format(Constantes.ASUNTO_EMAIL, fondo.getNombre());

        String cuerpo = String.format(
                MessageFormat.format(Constantes.CUERPO_EMAIL,
                        fondo.getNombre(),
                        fondo.getMontoMinimo().toPlainString())
        );

        sesClient.sendEmail(req -> req
                .destination(d -> d.toAddresses(cliente.getEmail()))
                .message(m -> m
                        .subject(s -> s.data(asunto))
                        .body(b -> b.text(t -> t.data(cuerpo)))
                )
                .source(Constantes.MAIL_SOURCE)
        );
    }

    /**
     * Envía una notificación por mensaje SMS.
     */
    private void enviarSms(Cliente cliente, Fondo fondo) {
        String mensaje = MessageFormat.format(Constantes.SMS_TEMPLATE, fondo.getNombre());

        snsClient.publish(p -> p
                .phoneNumber(cliente.getTelefono())
                .message(mensaje)
        );
    }
}