package com.example.my_books_backend.service;

import java.util.List;
import com.example.my_books_backend.dto.genre.GenreRequest;
import com.example.my_books_backend.dto.genre.GenreResponse;

public interface GenreService {
    List<GenreResponse> getAllGenres();

    GenreResponse getGenreById(Long id);

    GenreResponse createGenre(GenreRequest request);

    GenreResponse updateGenre(Long id, GenreRequest request);

    void deleteGenre(Long id);
}
