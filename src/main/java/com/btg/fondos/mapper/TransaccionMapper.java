package com.btg.fondos.mapper;

import com.btg.fondos.dto.TransaccionDTO;
import com.btg.fondos.model.Transaccion;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper para la conversión entre la entidad Transaccion y su representación DTO.
 * Utiliza MapStruct para generar automáticamente las implementaciones.
 */
@Mapper(componentModel = "spring")
public interface TransaccionMapper {
    /**
     * Convierte una entidad Transaccion a su objeto DTO.
     *
     * @param transaccion entidad a convertir
     * @return objeto TransaccionDTO
     */
    TransaccionDTO toDTO(Transaccion transaccion);

    /**
     * Convierte una lista de entidades Transaccion a una lista de DTOs.
     *
     * @param transacciones lista de entidades
     * @return lista de objetos TransaccionDTO
     */
    List<TransaccionDTO> toDTOList(List<Transaccion> transacciones);
}