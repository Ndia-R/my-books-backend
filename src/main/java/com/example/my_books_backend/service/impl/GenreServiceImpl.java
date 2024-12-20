package com.example.my_books_backend.service.impl;

import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import com.example.my_books_backend.dto.genre.CreateGenreRequest;
import com.example.my_books_backend.dto.genre.GenreResponse;
import com.example.my_books_backend.dto.genre.UpdateGenreRequest;
import com.example.my_books_backend.entity.Genre;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.mapper.GenreMapper;
import com.example.my_books_backend.repository.GenreRepository;
import com.example.my_books_backend.service.GenreService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @Override
    @Cacheable("getAllGenres")
    public List<GenreResponse> getAllGenres() {
        List<Genre> genres = genreRepository.findAll();
        return genreMapper.toResponseList(genres);
    }

    @Override
    @Cacheable(value = "getGenreById", key = "#p0")
    public GenreResponse getGenreById(Integer id) {
        Genre genre = findGenreById(id);
        return genreMapper.toResponse(genre);
    }

    @Override
    @CacheEvict(value = "getAllGenres", allEntries = true)
    public GenreResponse createGenre(CreateGenreRequest createGenreRequest) {
        Genre genre = genreMapper.toEntity(createGenreRequest);
        Genre saveGenre = genreRepository.save(genre);
        return genreMapper.toResponse(saveGenre);
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "getGenreById", key = "#p0"),
            @CacheEvict(value = "getAllGenres", allEntries = true)})
    public void putGenre(Integer id, UpdateGenreRequest updateGenreRequest) {
        Genre genre = findGenreById(id);

        genre.setName(updateGenreRequest.getName());
        genre.setDescription(updateGenreRequest.getDescription());
        genreRepository.save(genre);
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "getGenreById", key = "#p0"),
            @CacheEvict(value = "getAllGenres", allEntries = true)})
    public void patchGenre(Integer id, UpdateGenreRequest updateGenreRequest) {
        Genre genre = findGenreById(id);

        String name = updateGenreRequest.getName();
        String description = updateGenreRequest.getDescription();

        if (name != null) {
            genre.setName(name);
        }

        if (description != null) {
            genre.setName(description);
        }
        genreRepository.save(genre);
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "getGenreById", key = "#p0"),
            @CacheEvict(value = "getAllGenres", allEntries = true)})
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
