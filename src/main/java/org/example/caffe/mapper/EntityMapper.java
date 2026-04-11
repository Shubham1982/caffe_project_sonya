package org.example.caffe.mapper;

import java.util.List;

/**
 * Generic contract for all entity <-> DTO mappers.
 *
 * @param <D> the DTO type
 * @param <E> the entity type
 */
public interface EntityMapper<D, E> {

    D toDto(E entity);

    E toEntity(D dto);

    List<D> toDtoList(List<E> entityList);

    List<E> toEntityList(List<D> dtoList);
}
