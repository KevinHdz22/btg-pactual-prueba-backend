package com.btg.fondos.service.implementation;

import com.btg.fondos.model.Cliente;
import com.btg.fondos.model.Fondo;
import com.btg.fondos.utilities.Constantes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;

import java.math.BigDecimal;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para {@link NotificacionService}. Utiliza Mockito para simular
 * dependencias y verificar el comportamiento de los métodos.
 *
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock
    private SnsClient snsClient;

    @Mock
    private SesClient sesClient;

    @InjectMocks
    private NotificacionService notificacionService;

    private Cliente clienteMock;
    private Fondo fondoMock;


    @BeforeEach
    void setUp() {
        clienteMock = Cliente.builder()
                .clienteId("CLI-123")
                .email("cliente@test.com")
                .telefono("+573001234567")
                .build();

        fondoMock = Fondo.builder()
                .fondoId("FND-1")
                .nombre("Fondo Tecnología")
                .montoMinimo(new BigDecimal("500000"))
                .build();
    }

    @Test
    @DisplayName("Notificar - Preferencia Email")
    void notificarAsyncPreferenciaEmailDebeLlamarSesClient() {
        notificacionService.notificarAsync(clienteMock, fondoMock, Constantes.PREF_EMAIL);

        verify(sesClient, times(1)).sendEmail(ArgumentMatchers.<Consumer<SendEmailRequest.Builder>>any());
        verify(snsClient, never()).publish(ArgumentMatchers.<Consumer<PublishRequest.Builder>>any());
    }

    @Test
    @DisplayName("Notificar - Preferencia Sms")
    void notificarAsyncPreferenciaSMSDebeLlamarSnsClient() {

        notificacionService.notificarAsync(clienteMock, fondoMock, "SMS");

        verify(snsClient, times(1)).publish(ArgumentMatchers.<Consumer<PublishRequest.Builder>>any());
        verify(sesClient, never()).sendEmail(ArgumentMatchers.<Consumer<SendEmailRequest.Builder>>any());
    }

    @Test
    @DisplayName("Notificar - Opción por defecto")
    void notificarAsyncPreferenciaDesconocidaDebeUsarSmsPorDefecto() {
        notificacionService.notificarAsync(clienteMock, fondoMock, "OTRA_COSA");

        verify(snsClient, times(1)).publish(ArgumentMatchers.<Consumer<PublishRequest.Builder>>any());
        verify(sesClient, never()).sendEmail(ArgumentMatchers.<Consumer<SendEmailRequest.Builder>>any());
    }


    @Test
    @DisplayName("Notificar - Error simulado")
    void notificarAsyncErrorEnAWSNoDebeLanzarExcepcion() {
        doThrow(new RuntimeException("Error simulado de AWS SES"))
                .when(sesClient).sendEmail(ArgumentMatchers.<Consumer<SendEmailRequest.Builder>>any());

        assertDoesNotThrow(() ->
                notificacionService.notificarAsync(clienteMock, fondoMock, Constantes.PREF_EMAIL)
        );

        verify(sesClient, times(1)).sendEmail(ArgumentMatchers.<Consumer<SendEmailRequest.Builder>>any());
    }
}