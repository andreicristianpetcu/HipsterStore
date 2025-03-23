package net.petcu.store.service.dto;

import java.io.Serializable;

public record ProductDTO(Long id, String name, String description) implements Serializable {}
