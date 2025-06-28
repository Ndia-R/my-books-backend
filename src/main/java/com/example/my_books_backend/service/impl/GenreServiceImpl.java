package com.example.my_books_backend.service.impl;

import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.my_books_backend.dto.genre.GenreRequest;
import com.example.my_books_backend.dto.genre.GenreResponse;
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

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable("getAllGenres")
    public List<GenreResponse> getAllGenres() {
        List<Genre> genres = genreRepository.findAll();
        return genreMapper.toGenreResponseList(genres);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable(value = "getGenreById", key = "#p0")
    public GenreResponse getGenreById(Long id) {
        Genre genre = genreRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Genre not found"));
        return genreMapper.toGenreResponse(genre);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    @CacheEvict(value = "getAllGenres", allEntries = true)
    public GenreResponse createGenre(GenreRequest request) {
        Genre genre = new Genre();
        genre.setName(request.getName());
        genre.setDescription(request.getDescription());
        Genre savedGenre = genreRepository.save(genre);
        return genreMapper.toGenreResponse(savedGenre);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    @Caching(evict = { @CacheEvict(value = "getGenreById", key = "#p0"),
        @CacheEvict(value = "getAllGenres", allEntries = true) })
    public GenreResponse updateGenre(Long id, GenreRequest request) {
        Genre genre = genreRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Genre not found"));

        String name = request.getName();
        String description = request.getDescription();

        if (name != null) {
            genre.setName(name);
        }

        if (description != null) {
            genre.setDescription(description);
        }
        Genre savedGenre = genreRepository.save(genre);
        return genreMapper.toGenreResponse(savedGenre);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    @Caching(evict = { @CacheEvict(value = "getGenreById", key = "#p0"),
        @CacheEvict(value = "getAllGenres", allEntries = true) })
    public void deleteGenre(Long id) {
        genreRepository.deleteById(id);
    }
}
