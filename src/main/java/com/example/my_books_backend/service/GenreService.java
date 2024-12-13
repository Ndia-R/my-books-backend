package com.example.my_books_backend.service;

import java.util.List;
import java.util.Map;
import com.example.my_books_backend.dto.genre.GenreCreateDto;
import com.example.my_books_backend.dto.genre.GenreDto;
import com.example.my_books_backend.dto.genre.GenreUpdateDto;

public interface GenreService {

    List<GenreDto> getGenres();

    GenreDto getGenreById(Integer id);

    GenreDto createGenre(GenreCreateDto dto);

    void updateGenre(Integer id, GenreUpdateDto dto);

    void patchGenre(Integer id, Map<String, Object> updates);

    void deleteGenre(Integer id);
}
