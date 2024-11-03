package com.example.my_books_backend.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.example.my_books_backend.dto.genre.GenreCreateDto;
import com.example.my_books_backend.dto.genre.GenreDto;
import com.example.my_books_backend.dto.genre.GenreUpdateDto;
import com.example.my_books_backend.model.Genre;
import com.example.my_books_backend.service.GenreService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/genres")
public class GenreController {

    @Autowired
    private GenreService genreService;

    @GetMapping("")
    public ResponseEntity<List<GenreDto>> getGenres() {
        List<GenreDto> genres = genreService.getGenres();
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GenreDto> getGenreById(@PathVariable Integer id) {
        GenreDto genre = genreService.getGenreById(id);
        return ResponseEntity.ok(genre);
    }

    @PostMapping("")
    public ResponseEntity<GenreDto> createGenre(@Valid @RequestBody GenreCreateDto dto) {
        GenreDto genre = genreService.createGenre(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(genre.getId()).toUri();
        return ResponseEntity.created(location).body(genre);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenreDto> updateGenre(@PathVariable Integer id,
            @Valid @RequestBody GenreUpdateDto dto) {
        genreService.updateGenre(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<GenreDto> patchGenre(@PathVariable Integer id,
            @Valid @RequestBody Map<String, Object> updates) {
        genreService.patchGenre(id, updates);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Genre> deleteGenre(@PathVariable Integer id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}
