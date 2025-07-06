package com.example.my_books_backend.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.ArrayList;
import java.util.List;

public class PageableUtils {
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;
    private static final String DEFAULT_SORT_FIELD = "id";
    private static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.ASC;

    public static final List<String> BOOK_ALLOWED_FIELDS = new ArrayList<>(
        List.of("title", "publicationDate", "reviewCount", "averageRating", "popularity")
    );
    public static final List<String> REVIEW_ALLOWED_FIELDS = new ArrayList<>(
        List.of("updatedAt", "createdAt", "rating")
    );
    public static final List<String> FAVORITE_ALLOWED_FIELDS = new ArrayList<>(
        List.of("updatedAt", "createdAt")
    );
    public static final List<String> BOOKMARK_ALLOWED_FIELDS = new ArrayList<>(
        List.of("updatedAt", "createdAt")
    );

    public static Pageable createPageable(
        int page,
        int size,
        String sortString,
        List<String> category
    ) {
        // pageableは内部的に0ベースなので、1ベース→0ベースへ
        page = Math.max(0, page - 1);

        // 1ページあたりの最大数は制限する
        size = (size <= 0) ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);

        // ソート条件
        Sort sort = parseSort(sortString, category);

        return PageRequest.of(page, size, sort);
    }

    // ソート条件の解析
    private static Sort parseSort(String sortString, List<String> category) {
        if (sortString == null || sortString.trim().isEmpty()) {
            return Sort.by(DEFAULT_SORT_DIRECTION, DEFAULT_SORT_FIELD);
        }

        String[] sortParams = sortString.trim().split("\\.");
        if (sortParams.length != 2) {
            return Sort.by(DEFAULT_SORT_DIRECTION, DEFAULT_SORT_FIELD);
        }

        String sortField = sortParams[0].trim();
        if (!category.contains(sortField)) {
            sortField = DEFAULT_SORT_FIELD;
        }

        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.fromString(sortParams[1].trim());
        } catch (IllegalArgumentException e) {
            sortDirection = DEFAULT_SORT_DIRECTION;
        }

        // 第二ソートは「id」とする
        return Sort.by(sortDirection, sortField).and(Sort.by(Sort.Direction.ASC, "id"));
    }
}
