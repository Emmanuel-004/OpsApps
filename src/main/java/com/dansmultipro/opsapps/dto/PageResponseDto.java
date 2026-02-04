package com.dansmultipro.opsapps.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDto<T> {
    private List<T> data;
    private Integer currentPage;
    private Integer pageSize;
    private Integer totalPages;
    private Long totalElements;
}
