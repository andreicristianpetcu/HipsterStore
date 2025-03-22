package net.petcu.store.service.dto;

import java.io.Serializable;

/**
 * A DTO for the {@link net.petcu.store.domain.Product} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public record ProductDTO(Long id, String name, String description) implements Serializable {}
