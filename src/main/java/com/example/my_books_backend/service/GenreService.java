package com.example.my_books_backend.service;

import java.util.List;
import com.example.my_books_backend.dto.genre.CreateGenreRequest;
import com.example.my_books_backend.dto.genre.GenreResponse;
import com.example.my_books_backend.dto.genre.UpdateGenreRequest;

public interface GenreService {
    List<GenreResponse> getAllGenres();

    GenreResponse getGenreById(Long id);

    GenreResponse createGenre(CreateGenreRequest request);

    void updateGenre(Long id, UpdateGenreRequest request);

    void deleteGenre(Long id);
}
