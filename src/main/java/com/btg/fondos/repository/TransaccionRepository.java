package com.btg.fondos.repository;

import com.btg.fondos.model.Transaccion;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Repositorio encargado de la gestión de transacciones en DynamoDB.
 * Permite almacenar y consultar transacciones asociadas a un cliente.
 */
@Repository
@RequiredArgsConstructor
public class TransaccionRepository {

    private final DynamoDbClient dynamoDb;

    @Value("${aws.dynamodb.table-transacciones}")
    private String tableName;

    /**
     * Guarda una transacción en la base de datos.
     * Implementa idempotencia evitando duplicados mediante la clave SK.
     *
     * @param txn transacción a guardar
     */
    public void guardar(Transaccion txn) {
        String sk = "TXN#" + txn.getFechaCreacion().toEpochMilli()
                + "#" + txn.getTransaccionId();
        dynamoDb.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(Map.of(
                        "PK",              AttributeValue.fromS("CLIENT#" + txn.getClienteId()),
                        "SK",              AttributeValue.fromS(sk),
                        "transaccionId",   AttributeValue.fromS(txn.getTransaccionId()),
                        "fondoId",         AttributeValue.fromS(txn.getFondoId()),
                        "nombreFondo",     AttributeValue.fromS(txn.getNombreFondo()),
                        "tipo",            AttributeValue.fromS(txn.getTipo()),
                        "monto",           AttributeValue.fromN(txn.getMonto().toPlainString()),
                        "fechaCreacion",   AttributeValue.fromS(txn.getFechaCreacion().toString())
                ))
                .conditionExpression("attribute_not_exists(SK)") // idempotencia
                .build());
    }

    /**
     * Consulta las transacciones de un cliente.
     *
     * @param clienteId identificador del cliente
     * @return lista de transacciones ordenadas por fecha descendente
     */
    public List<Transaccion> findByClienteId(String clienteId) {
        QueryResponse resp = dynamoDb.query(QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression("PK = :pk AND begins_with(SK, :prefix)")
                .expressionAttributeValues(Map.of(
                        ":pk",     AttributeValue.fromS("CLIENT#" + clienteId),
                        ":prefix", AttributeValue.fromS("TXN#")
                ))
                .scanIndexForward(false)
                .build());

        return resp.items().stream()
                .map(this::mapToTransaccion)
                .toList();
    }

    /**
     * Convierte un item de DynamoDB en un objeto Transaccion.
     *
     * @param item mapa de atributos obtenido de DynamoDB
     * @return objeto Transaccion
     */
    private Transaccion mapToTransaccion(Map<String, AttributeValue> item) {
        return Transaccion.builder()
                .transaccionId(item.get("transaccionId").s())
                .fondoId(item.get("fondoId").s())
                .nombreFondo(item.get("nombreFondo").s())
                .tipo(item.get("tipo").s())
                .monto(new BigDecimal(item.get("monto").n()))
                .fechaCreacion(Instant.parse(item.get("fechaCreacion").s()))
                .build();
    }
}