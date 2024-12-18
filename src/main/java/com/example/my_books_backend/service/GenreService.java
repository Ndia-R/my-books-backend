package com.example.my_books_backend.service;

import java.util.List;
import com.example.my_books_backend.dto.genre.GenreCreateDto;
import com.example.my_books_backend.dto.genre.GenreDto;
import com.example.my_books_backend.dto.genre.GenrePatchDto;
import com.example.my_books_backend.dto.genre.GenrePutDto;

public interface GenreService {

    List<GenreDto> getGenres();

    GenreDto getGenreById(Integer id);

    GenreDto createGenre(GenreCreateDto dto);

    void putGenre(Integer id, GenrePutDto dto);

    void patchGenre(Integer id, GenrePatchDto dto);

    void deleteGenre(Integer id);
}
