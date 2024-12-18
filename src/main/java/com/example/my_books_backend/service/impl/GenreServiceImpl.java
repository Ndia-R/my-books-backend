package com.example.my_books_backend.service.impl;

import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import com.example.my_books_backend.dto.genre.GenreCreateDto;
import com.example.my_books_backend.dto.genre.GenreDto;
import com.example.my_books_backend.dto.genre.GenrePatchDto;
import com.example.my_books_backend.dto.genre.GenrePutDto;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.mapper.GenreMapper;
import com.example.my_books_backend.model.Genre;
import com.example.my_books_backend.repository.GenreRepository;
import com.example.my_books_backend.service.GenreService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @Override
    @Cacheable("getGenres")
    public List<GenreDto> getGenres() {
        List<Genre> genres = genreRepository.findAll();
        return genreMapper.toDtoList(genres);
    }

    @Override
    @Cacheable(value = "getGenreById", key = "#p0")
    public GenreDto getGenreById(Integer id) {
        Genre genre = findGenreById(id);
        return genreMapper.toDto(genre);
    }

    @Override
    @CacheEvict(value = "getGenres", allEntries = true)
    public GenreDto createGenre(GenreCreateDto dto) {
        Genre genre = genreMapper.toEntity(dto);
        Genre saveGenre = genreRepository.save(genre);
        return genreMapper.toDto(saveGenre);
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "getGenreById", key = "#p0"),
            @CacheEvict(value = "getGenres", allEntries = true)})
    public void putGenre(Integer id, GenrePutDto dto) {
        Genre genre = findGenreById(id);

        genre.setName(dto.getName());
        genre.setDescription(dto.getDescription());
        genreRepository.save(genre);
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "getGenreById", key = "#p0"),
            @CacheEvict(value = "getGenres", allEntries = true)})
    public void patchGenre(Integer id, GenrePatchDto dto) {
        Genre genre = findGenreById(id);

        if (dto.getName() != null) {
            genre.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            genre.setName(dto.getDescription());
        }
        genreRepository.save(genre);
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "getGenreById", key = "#p0"),
            @CacheEvict(value = "getGenres", allEntries = true)})
    public void deleteGenre(Integer id) {
        Genre genre = findGenreById(id);
        genreRepository.delete(genre);
    }

    private Genre findGenreById(Integer id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("見つかりませんでした。 ID: " + id));
        return genre;
    }
}
