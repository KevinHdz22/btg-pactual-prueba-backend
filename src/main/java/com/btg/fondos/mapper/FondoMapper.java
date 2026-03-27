package com.btg.fondos.mapper;

import com.btg.fondos.dto.FondoDTO;
import com.btg.fondos.model.Fondo;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * Mapper para la conversión entre la entidad Fondo y su representación DTO.
 * Utiliza MapStruct para generar automáticamente las implementaciones.
 */
@Mapper(componentModel = "spring")
public interface FondoMapper {

    /**
     * Convierte una entidad Fondo a su objeto DTO.
     *
     * @param fondo entidad a convertir
     * @return objeto FondoDTO
     */
    FondoDTO toDTO(Fondo fondo);


    /**
     * Convierte una lista de entidades Fondo a una lista de DTOs.
     *
     * @param fondos lista de entidades
     * @return lista de objetos FondoDTO
     */
    List<FondoDTO> toDTOList(List<Fondo> fondos);
}