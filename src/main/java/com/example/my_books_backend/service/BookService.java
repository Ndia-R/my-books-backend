package com.example.my_books_backend.service;

import com.example.my_books_backend.dto.book.BookDetailsResponse;
import com.example.my_books_backend.dto.book.BookPageResponse;
import com.example.my_books_backend.dto.book_chapter.BookTableOfContentsResponse;
import com.example.my_books_backend.dto.book_chapter_page_content.BookChapterPageContentResponse;

public interface BookService {
    /**
     * 最新の書籍リスト（１０冊分）を取得（ページング形式）
     * 
     * @param page ページ番号（0ベース）、nullの場合はデフォルト値が使用される
     * @param maxResults 1ページあたりの最大結果件数、nullの場合はデフォルト値が使用される
     * @return 最新の書籍リスト
     */
    BookPageResponse getLatestBooks(Integer page, Integer maxResults);

    /**
     * タイトルで書籍を検索したリストを取得（ページング形式）
     * 
     * @param keyword 検索キーワード
     * @param page ページ番号（0ベース）、nullの場合はデフォルト値が使用される
     * @param maxResults 1ページあたりの最大結果件数、nullの場合はデフォルト値が使用される
     * @return 検索結果
     */
    BookPageResponse searchBooksByTitleKeyword(String keyword, Integer page, Integer maxResults);

    /**
     * ジャンルIDで書籍を検索したリストを取得（ページング形式）
     * 
     * @param genreIdsQuery カンマ区切りのジャンルIDリスト（例："1,2,3"）
     * @param conditionQuery 検索条件（"SINGLE"、"AND"、"OR"のいずれか）
     * @param page ページ番号（0ベース）、nullの場合はデフォルト値が使用される
     * @param maxResults 1ページあたりの最大結果件数、nullの場合はデフォルト値が使用される
     * @return 検索結果
     */
    BookPageResponse searchBooksByGenre(String genreIdsQuery, String conditionQuery, Integer page,
            Integer maxResults);

    /**
     * 指定された書籍の詳細情報を取得
     * 
     * @param bookId 書籍ID
     * @return 書籍の詳細情報
     */
    BookDetailsResponse getBookDetails(String bookId);

    /**
     * 指定された書籍の目次情報（章のリスト）を取得
     * 
     * @param bookId 書籍ID
     * @return 書籍の目次情報
     */
    BookTableOfContentsResponse getBookTableOfContents(String bookId);

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
}
