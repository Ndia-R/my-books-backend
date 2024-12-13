package com.example.my_books_backend.mapper;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.genre.GenreCreateDto;
import com.example.my_books_backend.dto.genre.GenreDto;
import com.example.my_books_backend.model.Genre;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GenreMapper {

    private final ModelMapper modelMapper;

    public GenreDto toDto(Genre genre) {
        return modelMapper.map(genre, GenreDto.class);
    }

    public Genre toEntity(GenreDto genreDto) {
        return modelMapper.map(genreDto, Genre.class);
    }

    public Genre toEntity(GenreCreateDto createGenreDto) {
        return modelMapper.map(createGenreDto, Genre.class);
    }

    public List<GenreDto> toDtoList(List<Genre> genres) {
        return genres.stream().map(genre -> toDto(genre)).toList();
    }
}
