package com.example.my_books_backend.service;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Pageable;
import com.example.my_books_backend.dto.CursorPageResponse;
import com.example.my_books_backend.dto.book.BookDetailsResponse;
import com.example.my_books_backend.dto.book.BookPageResponse;
import com.example.my_books_backend.dto.book.BookResponse;
import com.example.my_books_backend.dto.book_chapter.BookTableOfContentsResponse;
import com.example.my_books_backend.dto.book_chapter_page_content.BookChapterPageContentResponse;
import com.example.my_books_backend.entity.Book;

public interface BookService {
    /**
     * 最新の書籍リスト（10冊分）を取得
     * 
     * @param pageable ページネーション情報（ページ番号、ページサイズ、ソート条件）
     * @return 最新の書籍リスト
     */
    BookPageResponse getLatestBooks(Pageable pageable);

    /**
     * タイトルで書籍を検索したリストを取得
     * 
     * @param keyword 検索キーワード
     * @param pageable ページネーション情報（ページ番号、ページサイズ、ソート条件）
     * @return 検索結果
     */
    BookPageResponse getBooksByTitleKeyword(String keyword, Pageable pageable);

    /**
     * タイトルで書籍を検索したリストを取得（カーソルベース）
     * 
     * @param keyword 検索キーワード
     * @param cursor カーソルID、nullの場合は先頭からlimit分のデータが返却される
     * @param limit 1ページあたりの最大結果件数、nullの場合はデフォルト値が使用される
     * @return 検索結果
     */
    CursorPageResponse<BookResponse> getBooksByTitleKeywordWithCursor(String keyword, String cursor,
            Integer limit);

    /**
     * ジャンルIDで書籍を検索したリストを取得
     * 
     * @param genreIdsQuery カンマ区切りのジャンルIDリスト（例："1,2,3"）
     * @param conditionQuery 検索条件（"SINGLE"、"AND"、"OR"のいずれか）
     * @param pageable ページネーション情報（ページ番号、ページサイズ、ソート条件）
     * @return 検索結果
     */
    BookPageResponse getBooksByGenre(String genreIdsQuery, String conditionQuery,
            Pageable pageable);

    /**
     * 指定された書籍の詳細情報を取得
     * 
     * @param id 書籍ID
     * @return 書籍の詳細情報
     */
    BookDetailsResponse getBookDetails(String id);

    /**
     * 指定された書籍の目次情報（章のリスト）を取得
     * 
     * @param id 書籍ID
     * @return 書籍の目次情報
     */
    BookTableOfContentsResponse getBookTableOfContents(String id);

    /**
     * 指定された書籍の特定の章・ページのコンテンツを取得
     * 
     * @param bookId 書籍ID
     * @param chapterNumber 章番号
     * @param pageNumber ページ番号
     * @return 書籍のコンテンツ情報
     */
    BookChapterPageContentResponse getBookChapterPageContent(String bookId, Integer chapterNumber,
            Integer pageNumber);

    /**
     * レビュー統計の取得と格納
     * 
     * @param books 書籍リスト
     * @param reviewCounts 書籍に対するレビュー数を格納するMap
     * @param averageRatings 書籍の平均評価点を格納するMap
     */
    void loadReviewStatistics(List<Book> books, Map<String, Integer> reviewCounts,
            Map<String, Double> averageRatings);
}
