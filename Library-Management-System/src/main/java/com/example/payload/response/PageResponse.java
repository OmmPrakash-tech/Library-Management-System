package com.example.payload.response;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponse<T> {

    private List<T> content;

    private int pageNumber;

    private int pageSize;

    private long totalElements;

    private int totalPages;

    private boolean last;

    private boolean first;

    private boolean empty;

    public static <T> PageResponse<T> from(Page<T> page) {
    PageResponse<T> response = new PageResponse<>();

    response.setContent(page.getContent());
    response.setPageNumber(page.getNumber());
    response.setPageSize(page.getSize());
    response.setTotalElements(page.getTotalElements());
    response.setTotalPages(page.getTotalPages());
    response.setLast(page.isLast());
    response.setFirst(page.isFirst());
    response.setEmpty(page.isEmpty());

    return response;
}

}