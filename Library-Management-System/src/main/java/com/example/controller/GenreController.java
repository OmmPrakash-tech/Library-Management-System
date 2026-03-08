package com.example.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.exception.GenreException;
import com.example.payload.dto.GenreDTO;
import com.example.payload.response.ApiResponse;
import com.example.service.GenreService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    // Create Genre
    @PostMapping("/create")
    public ResponseEntity<GenreDTO> addGenre(@Valid @RequestBody GenreDTO genre) {

        GenreDTO createdGenre = genreService.createGenre(genre);

        return ResponseEntity.ok(createdGenre);
    }

    // Get All Genres
    @GetMapping
    public ResponseEntity<List<GenreDTO>> getAllGenres() {

        List<GenreDTO> genres = genreService.getAllGenres();

        return ResponseEntity.ok(genres);
    }

    // Get Genre By Id
    @GetMapping("/{genreId}")
    public ResponseEntity<GenreDTO> getGenreById(@PathVariable Long genreId)
            throws GenreException {

        GenreDTO genre = genreService.getGenreById(genreId);

        return ResponseEntity.ok(genre);
    }

    // Update Genre
    @PutMapping("/{genreId}")
    public ResponseEntity<GenreDTO> updateGenre(
            @PathVariable Long genreId,
            @Valid @RequestBody GenreDTO genre
    ) throws GenreException {

        GenreDTO updatedGenre = genreService.updateGenre(genreId, genre);

        return ResponseEntity.ok(updatedGenre);
    }

    // Soft Delete Genre
    @DeleteMapping("/{genreId}")
    public ResponseEntity<ApiResponse> deleteGenre(@PathVariable Long genreId)
            throws GenreException {

        genreService.deleteGenre(genreId);

        ApiResponse response =
                new ApiResponse("Genre deleted - soft delete", true);

        return ResponseEntity.ok(response);
    }

    // Hard Delete Genre
    @DeleteMapping("/{genreId}/hard")
    public ResponseEntity<ApiResponse> hardDeleteGenre(@PathVariable Long genreId)
            throws GenreException {

        genreService.hardDeleteGenre(genreId);

        ApiResponse response =
                new ApiResponse("Genre deleted - hard delete", true);

        return ResponseEntity.ok(response);
    }

    // Get Top Level Genres
    @GetMapping("/top-level")
    public ResponseEntity<List<GenreDTO>> getTopLevelGenres() {

        List<GenreDTO> genres = genreService.getTopLevelGenres();

        return ResponseEntity.ok(genres);
    }

    // Count Active Genres
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalActiveGenres() {

        Long count = genreService.getTotalActiveGenres();

        return ResponseEntity.ok(count);
    }

    // Count Books in Genre
    @GetMapping("/{genreId}/book-count")
    public ResponseEntity<Long> getBookCountByGenres(@PathVariable Long genreId) {

        long count = genreService.getBookCountByGenre(genreId);

        return ResponseEntity.ok(count);
    }
}