package com.example.my_books_backend.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import com.example.my_books_backend.dto.PageResponse;
import java.util.function.Function;

public class PageableUtils {
    private static final long DEFAULT_PAGE_SIZE = 20;
    private static final long MAX_PAGE_SIZE = 100;
    private static final String DEFAULT_SORT_FIELD = "id";
    private static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.ASC;

    // ソート可能なフィールドのリスト（エンドポイントで指定可能なフィールド）
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

    /**
     * ページネーション用のPageableオブジェクトを作成
     * 
     * @param page ページ番号（1ベース）
     * @param size 1ページあたりの最大結果件数
     * @param sortString ソート条件（例: "xxxx.desc", "xxxx.asc"）
     * @param category ソート可能なフィールドのリスト
     * @return Pageableオブジェクト 
     */
    public static Pageable createPageable(
        long page,
        long size,
        String sortString,
        List<String> category
    ) {
        page = Math.max(0, page - 1); // pageableは内部的に0ベースなので、1ベース→0ベースへ
        size = (size <= 0) ? DEFAULT_PAGE_SIZE : Math.min(size, MAX_PAGE_SIZE);
        Sort sort = parseSort(sortString, category);

        return PageRequest.of((int) page, (int) size, sort);
    }

    /**
     * ソート条件の解析
     * 
     * @param sortString ソート条件（例: "xxxx.desc", "xxxx.asc"）
     * @param category ソート可能なフィールドのリスト
     * @return Sortオブジェクト
     */
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

    /**
     * 汎用的なPageResponse変換メソッド
     * 
     * @param <T> エンティティの型
     * @param <R> レスポンスの型
     * @param page Pageオブジェクト
     * @param responseList 変換済みのレスポンスリスト
     * @return PageResponseオブジェクト
     */
    public static <T, R> PageResponse<R> toPageResponse(Page<T> page, List<R> responseList) {
        return new PageResponse<R>(
            (long) page.getNumber() + 1, // Pageableの内部的にはデフォルトで0ベースだが、エンドポイントとしては1ベースなので+1する
            (long) page.getSize(),
            (long) page.getTotalPages(),
            page.getTotalElements(),
            page.hasNext(),
            page.hasPrevious(),
            responseList
        );
    }

    /**
     * 2クエリ戦略でソート順序を保持するためのユーティリティメソッド
     * IDリストの順序に従ってリストを並び替える
     * 
     * @param <T> エンティティの型
     * @param <ID> IDの型
     * @param ids 元のIDリスト（正しい順序）
     * @param list 並び替える対象のリスト
     * @param idExtractor IDを抽出する関数
     * @return ソート順序が復元されたリスト
     */
    public static <T, ID> List<T> restoreSortOrder(
        List<ID> ids,
        List<T> list,
        Function<T, ID> idExtractor
    ) {
        // ソート順序を保持するためのマップを作成
        Map<ID, Long> idOrder = IntStream.range(0, ids.size())
            .boxed()
            .collect(Collectors.toMap(ids::get, i -> i.longValue()));

        // 元のソート順序でリストを並び替え
        return list.stream()
            .sorted((item1, item2) -> {
                Long order1 = idOrder.get(idExtractor.apply(item1));
                Long order2 = idOrder.get(idExtractor.apply(item2));
                return order1.compareTo(order2);
            })
            .collect(Collectors.toList());
    }
}
