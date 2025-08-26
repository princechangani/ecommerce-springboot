package com.project.mapper;

import java.util.List;

public interface BaseMapper<D, E> {
    
    E toEntity(D dto);
    
    D toDto(E entity);
    
    List<E> toEntityList(List<D> dtoList);
    
    List<D> toDtoList(List<E> entityList);
    
    void updateEntityFromDto(D dto, @org.mapstruct.MappingTarget E entity);
}
