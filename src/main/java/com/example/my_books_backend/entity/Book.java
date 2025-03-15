package com.example.my_books_backend.entity;

import java.sql.Date;
import java.util.List;
import com.example.my_books_backend.entity.shared.EntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "book_genres", joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private List<Genre> genres;

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

    @Column(name = "image_path")
    private String imagePath;

    @OneToMany(mappedBy = "book")
    private List<Review> reviews;

    @OneToMany(mappedBy = "book")
    private List<Favorite> favorites;

    @OneToMany(mappedBy = "book")
    private List<Bookmark> bookmarks;
}
