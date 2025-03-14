package com.example.my_books_backend.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.example.my_books_backend.dto.genre.GenreRequest;
import com.example.my_books_backend.dto.genre.GenreResponse;
import com.example.my_books_backend.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping("/genres")
    public ResponseEntity<List<GenreResponse>> getAllGenres() {
        List<GenreResponse> genreResponses = genreService.getAllGenres();
        return ResponseEntity.ok(genreResponses);
    }

    @GetMapping("/genres/{id}")
    public ResponseEntity<GenreResponse> getGenreById(@PathVariable Long id) {
        GenreResponse genreResponse = genreService.getGenreById(id);
        return ResponseEntity.ok(genreResponse);
    }

    @PostMapping("/genres")
    public ResponseEntity<GenreResponse> createGenre(@Valid @RequestBody GenreRequest request) {
        GenreResponse genreResponse = genreService.createGenre(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(genreResponse.getId()).toUri();
        return ResponseEntity.created(location).body(genreResponse);
    }

    @PutMapping("/genres/{id}")
    public ResponseEntity<GenreResponse> updateGenre(@PathVariable Long id,
            @Valid @RequestBody GenreRequest request) {
        GenreResponse genreResponse = genreService.updateGenre(id, request);
        return ResponseEntity.ok(genreResponse);
    }

    @DeleteMapping("/genres/{id}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}
