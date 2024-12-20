package com.example.my_books_backend.mapper;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.genre.CreateGenreRequest;
import com.example.my_books_backend.dto.genre.GenreResponse;
import com.example.my_books_backend.entity.Genre;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GenreMapper {
    private final ModelMapper modelMapper;

    public GenreResponse toResponse(Genre genre) {
        return modelMapper.map(genre, GenreResponse.class);
    }

    public Genre toEntity(GenreResponse genreResponse) {
        return modelMapper.map(genreResponse, Genre.class);
    }

    public Genre toEntity(CreateGenreRequest createGenreDto) {
        return modelMapper.map(createGenreDto, Genre.class);
    }

    public List<GenreResponse> toResponseList(List<Genre> genres) {
        return genres.stream().map(genre -> toResponse(genre)).toList();
    }
}
