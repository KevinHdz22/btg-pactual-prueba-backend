package com.btg.fondos.repository;

import com.btg.fondos.model.Fondo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
/**
 * Repositorio encargado de la gestión de datos de fondos en DynamoDB.
 * Permite consultar información individual o listar todos los fondos.
 */
@Repository
@RequiredArgsConstructor
public class FondoRepository {

    private final DynamoDbClient dynamoDb;

    @Value("${aws.dynamodb.table-fondos}")
    private String tableName;


    /**
     * Busca un fondo por su identificador.
     *
     * @param fondoId identificador del fondo
     * @return Optional con el fondo si existe, o vacío si no se encuentra
     */
    public Optional<Fondo> findById(String fondoId) {
        GetItemResponse resp = dynamoDb.getItem(GetItemRequest.builder()
                .tableName(tableName)
                .key(Map.of("PK", AttributeValue.fromS("FONDO#" + fondoId)))
                .build());

        if (!resp.hasItem()) return Optional.empty();

        Map<String, AttributeValue> item = resp.item();
        return Optional.of(Fondo.builder()
                .fondoId(fondoId)
                .nombre(item.get("nombre").s())
                .montoMinimo(new BigDecimal(item.get("montoMinimo").n()))
                .categoria(item.get("categoria").s())
                .build());
    }

    /**
     * Obtiene la lista de todos los fondos registrados.
     *
     * @return lista de fondos
     */
    public List<Fondo> findAll() {
        ScanResponse resp = dynamoDb.scan(ScanRequest.builder()
                .tableName(tableName)
                .build());

        return resp.items().stream()
                .map(item -> Fondo.builder()
                        .fondoId(item.get("fondoId").s())
                        .nombre(item.get("nombre").s())
                        .montoMinimo(new BigDecimal(item.get("montoMinimo").n()))
                        .categoria(item.get("categoria").s())
                        .build())
                .toList();
    }
}