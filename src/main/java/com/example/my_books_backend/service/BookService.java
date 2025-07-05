package com.example.my_books_backend.service;

import com.example.my_books_backend.dto.PageResponse;
import com.example.my_books_backend.dto.SliceResponse;
import com.example.my_books_backend.dto.book.BookDetailsResponse;
import com.example.my_books_backend.dto.book.BookResponse;
import com.example.my_books_backend.dto.book_chapter.BookTableOfContentsResponse;
import com.example.my_books_backend.dto.book_chapter_page_content.BookChapterPageContentResponse;

public interface BookService {
    /**
     * 書籍一覧取得（ページネーション用）
     * 
     * @param page ページ番号（1ベース）
     * @param size 1ページあたりの最大結果件数
     * @param sortString ソート条件（例: "xxxx.desc", "xxxx.asc"）
     * @return 最新の書籍リスト
     */
    PageResponse<BookResponse> getBooks(
        Integer page,
        Integer size,
        String sortString
    );

    /**
     * 書籍一覧取得（無限スクロール用）
     * 
     * @param page ページ番号（1ベース）
     * @param size 1ページあたりの最大結果件数
     * @param sortString ソート条件（例: "xxxx.desc", "xxxx.asc"）
     * @return 最新の書籍リスト
     */
    SliceResponse<BookResponse> getBooksForScroll(
        Integer page,
        Integer size,
        String sortString
    );

    /**
     * タイトルで書籍を検索したリストを取得（ページネーション用）
     * 
     * @param keyword 検索キーワード
     * @param page ページ番号（1ベース）
     * @param size 1ページあたりの最大結果件数
     * @param sortString ソート条件（例: "xxxx.desc", "xxxx.asc"）
     * @return 検索結果
     */
    PageResponse<BookResponse> getBooksByTitleKeyword(
        String keyword,
        Integer page,
        Integer size,
        String sortString
    );

    /**
     * タイトルで書籍を検索したリストを取得（無限スクロール用）
     * 
     * @param keyword 検索キーワード
     * @param page ページ番号（1ベース）
     * @param size 1ページあたりの最大結果件数
     * @param sortString ソート条件（例: "xxxx.desc", "xxxx.asc"）
     * @return 検索結果
     */
    SliceResponse<BookResponse> getBooksByTitleKeywordForScroll(
        String keyword,
        Integer page,
        Integer size,
        String sortString
    );

    /**
     * ジャンルIDで書籍を検索したリストを取得（ページネーション用）
     * 
     * @param genreIdsQuery カンマ区切りのジャンルIDリスト（例："1,2,3"）
     * @param conditionQuery 検索条件（"SINGLE"、"AND"、"OR"のいずれか）
     * @param page ページ番号（1ベース）
     * @param size 1ページあたりの最大結果件数
     * @param sortString ソート条件（例: "xxxx.desc", "xxxx.asc"）
     * @return 検索結果
     */
    PageResponse<BookResponse> getBooksByGenre(
        String genreIdsQuery,
        String conditionQuery,
        Integer page,
        Integer size,
        String sortString
    );

    /**
     * ジャンルIDで書籍を検索したリストを取得（無限スクロール用）
     * 
     * @param genreIdsQuery カンマ区切りのジャンルIDリスト（例："1,2,3"）
     * @param conditionQuery 検索条件（"SINGLE"、"AND"、"OR"のいずれか）
     * @param page ページ番号（1ベース）
     * @param size 1ページあたりの最大結果件数
     * @param sortString ソート条件（例: "xxxx.desc", "xxxx.asc"）
     * @return 検索結果
     */
    SliceResponse<BookResponse> getBooksByGenreForScroll(
        String genreIdsQuery,
        String conditionQuery,
        Integer page,
        Integer size,
        String sortString
    );

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
    BookChapterPageContentResponse getBookChapterPageContent(
        String bookId,
        Integer chapterNumber,
        Integer pageNumber
    );
}
