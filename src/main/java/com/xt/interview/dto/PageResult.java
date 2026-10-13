package com.xt.interview.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResult<T>(List<T> list, long totalElements, int totalPages, int currentPage, int pageSize) {

    public static <T> PageResult<T> from(Page<T> page) {
        return new PageResult<>(page.getContent(), page.getTotalElements(), page.getTotalPages(),
                page.getNumber(), page.getSize());
    }
}
