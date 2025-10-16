package com.gambling.betting_odds_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Generic pagination response wrapper
// Can be reused for any type of data
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> content;              // The actual data (list of OddsResponse, etc.)
    private int pageNumber;               // Current page number (0-indexed)
    private int pageSize;                 // Number of items per page
    private long totalElements;           // Total number of items across all pages
    private int totalPages;               // Total number of pages
    private boolean first;                // Is this the first page?
    private boolean last;                 // Is this the last page?
    private boolean empty;                // Is the page empty (no data)?

    // Convenience method to create from Spring's Page object
    public static <T> PageResponse<T> of(org.springframework.data.domain.Page<T> page){
        return new PageResponse<>(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast(),
            page.isEmpty()
        );
    }
}
