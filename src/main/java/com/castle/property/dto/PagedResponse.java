package com.castle.property.dto;

import java.util.List;

/**
 * Generic paginated response wrapper used by list endpoints.
 */
public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    public static <T> PagedResponse<T> of(org.springframework.data.domain.Page<T> source) {
        return new PagedResponse<>(
                source.getContent(),
                source.getNumber(),
                source.getSize(),
                source.getTotalElements(),
                source.getTotalPages()
        );
    }
}
