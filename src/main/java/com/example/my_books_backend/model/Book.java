package com.example.my_books_backend.model;

import java.sql.Date;
import com.example.my_books_backend.model.shared.EntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "books")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Book extends EntityBase {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "genre_ids", nullable = false)
    private String genreIds;

    @Column(name = "authors", nullable = false)
    private String authors;

    @Column(name = "publisher", nullable = false)
    private String publisher;

    @Column(name = "published_date", nullable = false)
    private Date publishedDate;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "page_count", nullable = false)
    private Integer pageCount;

    @Column(name = "isbn", nullable = false)
    private String isbn;

    @Column(name = "image_url")
    private String imageUrl;
}
