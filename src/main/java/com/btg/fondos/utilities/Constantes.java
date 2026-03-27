package com.btg.fondos.utilities;
/**
 * Clase donde se definen las constantes que se van a utilizar en la lógica y peticiones del
 * aplicativo
 *
 * @since 1.0.0
 */
public class Constantes {

    /** Constantes de la aplicación */
    private Constantes() {
        throw new IllegalStateException("Clase de constantes");
    }

    /** Mensaje de error cuando el saldo es insuficiente */
    public static final String MSJ_ERROR_SALDO_INSUFICIENTE = "No tiene saldo disponible para vincularse al fondo {0}";

    /** Mensaje de error cuando ya existe una suscripción activa */
    public static final String MSJ_ERROR_SUSCRIPCION_ACTIVA = "El cliente ya tiene una suscripción activa al fondo {0}";

    /** Mensaje de error cuando no existe una suscripción activa */
    public static final String MSJ_ERROR_SIN_SUSCRIPCION_ACTIVA = "No tiene una suscripción activa al fondo {0} para cancelar ";

    /** Tipo de operación de apertura */
    public static final String FUNCION_APERTURA = "APERTURA";
    /** Tipo de operación de cancelación */
    public static final String FUNCION_CANCELACION = "CANCELACION";

    /** Mensaje de proceso exitoso */
    public static final String MSJ_PROCESO_EXITOSO = "{0} exitosa al fondo {1}";

    /** Mensaje cuando no se encuentra un cliente */
    public static final String MSJ_CLIENTE_NO_ENCONTRADO = "No se encontró un cliente con el id: {0}";

    /** Mensaje cuando no se encuentra un fondo */
    public static final String MSJ_FONDO_NO_ENCONTRADO = "No se encontró un fondo con el id: {0}";

    /** Texto de acción de cancelación */
    public static final String ACCION_CANCELACION = "Cancelación";

    /** Texto de acción de suscripción */
    public static final String ACCION_APERTURA = "Suscripción";

    /** Preferencia de notificación por email */
    public static final String PREF_EMAIL = "EMAIL";

    /** Correo remitente */
    public static final String MAIL_SOURCE = "noreply@btgpactual.com";

    /** Nombre de la entidad */
    public static final String ENTIDAD_NOMBRE = "BTG Pactual";

    /** Asunto del correo electrónico */
    public static final String ASUNTO_EMAIL = "Suscripción exitosa a {0}";

    /** Cuerpo del correo electrónico */
    public static final String CUERPO_EMAIL = "Estimado cliente,%n%nSu suscripción al fondo {0} por COP ${1} ha sido procesada.%n%n" + ENTIDAD_NOMBRE;

    /** Plantilla de mensaje SMS */
    public static final String SMS_TEMPLATE = ENTIDAD_NOMBRE + ": Suscripción al fondo {0} procesada.";
}
