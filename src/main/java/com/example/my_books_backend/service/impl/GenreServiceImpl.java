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
        return genreMapper.toGenreResponseList(genres);
    }

    @Override
    @Cacheable(value = "getGenreById", key = "#p0")
    public GenreResponse getGenreById(Long id) {
        Genre genre = findGenreById(id);
        return genreMapper.toGenreResponse(genre);
    }

    @Override
    @CacheEvict(value = "getAllGenres", allEntries = true)
    public GenreResponse createGenre(CreateGenreRequest request) {
        Genre genre = genreMapper.toGenreEntity(request);
        Genre saveGenre = genreRepository.save(genre);
        return genreMapper.toGenreResponse(saveGenre);
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "getGenreById", key = "#p0"),
            @CacheEvict(value = "getAllGenres", allEntries = true)})
    public void updateGenre(Long id, UpdateGenreRequest request) {
        Genre genre = findGenreById(id);

        String name = request.getName();
        String description = request.getDescription();

        if (name != null) {
            genre.setName(name);
        }

        if (description != null) {
            genre.setDescription(description);
        }
        genreRepository.save(genre);
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "getGenreById", key = "#p0"),
            @CacheEvict(value = "getAllGenres", allEntries = true)})
    public void deleteGenre(Long id) {
        Genre genre = findGenreById(id);
        genreRepository.delete(genre);
    }

    private Genre findGenreById(Long id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("見つかりませんでした。 ID: " + id));
        return genre;
    }
}
