package com.btg.fondos.repository;

import com.btg.fondos.model.Cliente;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

/**
 * Repositorio encargado de la gestión de datos de clientes en DynamoDB.
 * Permite consultar y actualizar la información relacionada con los clientes.
 */
@Repository
@RequiredArgsConstructor
public class ClienteRepository {

    private final DynamoDbClient dynamoDb;

    @Value("${aws.dynamodb.table-clientes}")
    private String tableName;


    /**
     * Busca un cliente por su identificador.
     *
     * @param clienteId identificador del cliente
     * @return Optional con el cliente si existe, o vacío si no se encuentra
     */
    public Optional<Cliente> findById(String clienteId) {
        GetItemResponse resp = dynamoDb.getItem(GetItemRequest.builder()
                .tableName(tableName)
                .key(Map.of("PK", AttributeValue.fromS("CLIENT#" + clienteId)))
                .build());

        if (!resp.hasItem()) return Optional.empty();

        Map<String, AttributeValue> item = resp.item();
        return Optional.of(Cliente.builder()
                .clienteId(clienteId)
                .email(item.get("email").s())
                .telefono(item.get("telefono").s())
                .saldo(new BigDecimal(item.get("saldo").n()))
                .notificacionPreferencia(item.get("notificacionPreferencia").s())
                .build());
    }

    /**
     * Actualiza el saldo de un cliente.
     *
     * @param clienteId identificador del cliente
     * @param nuevoSaldo nuevo saldo a registrar
     */
    public void actualizarSaldo(String clienteId, BigDecimal nuevoSaldo) {
        dynamoDb.updateItem(UpdateItemRequest.builder()
                .tableName(tableName)
                .key(Map.of("PK", AttributeValue.fromS("CLIENT#" + clienteId)))
                .updateExpression("SET saldo = :saldo")
                .expressionAttributeValues(Map.of(
                        ":saldo", AttributeValue.fromN(nuevoSaldo.toPlainString())
                ))
                .build());
    }
}