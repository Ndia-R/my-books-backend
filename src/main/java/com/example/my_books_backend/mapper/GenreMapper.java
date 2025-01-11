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

    public GenreResponse toGenreResponse(Genre genre) {
        return modelMapper.map(genre, GenreResponse.class);
    }

    public List<GenreResponse> toGenreResponseList(List<Genre> genres) {
        return genres.stream().map(genre -> toGenreResponse(genre)).toList();
    }

    public Genre toGenreEntity(CreateGenreRequest createGenreRequest) {
        return modelMapper.map(createGenreRequest, Genre.class);
    }
}
