package com.example.my_books_backend.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PaginationUtil {
    private static final Integer DEFAULT_START_PAGE = 0;
    private static final Integer DEFAULT_MAX_RESULTS = 20;

    public Pageable createPageable(Integer page, Integer maxResults, Sort sort) {
        int pageNumber = (page != null) ? page : DEFAULT_START_PAGE;
        int pageSize = (maxResults != null) ? maxResults : DEFAULT_MAX_RESULTS;
        return PageRequest.of(pageNumber, pageSize, sort);
    }
}
