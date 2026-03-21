package com.example.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.exception.GenreException;
import com.example.payload.dto.GenreDTO;
import com.example.payload.response.ApiResponse;
import com.example.service.GenreService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins="http://localhost:4200")
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    // Create Genre
    @PostMapping("/create")
    public ResponseEntity<GenreDTO> addGenre(@Valid @RequestBody GenreDTO genreDTO) {

        GenreDTO createdGenre = genreService.createGenre(genreDTO);

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

    // Get Genre By Code
    @GetMapping("/code/{code}")
    public ResponseEntity<GenreDTO> getGenreByCode(@PathVariable String code) {

        GenreDTO genre = genreService.getGenreByCode(code);

        return ResponseEntity.ok(genre);
    }

    // Update Genre
    @PutMapping("/{genreId}")
    public ResponseEntity<GenreDTO> updateGenre(
            @PathVariable Long genreId,
            @Valid @RequestBody GenreDTO genreDTO
    ) throws GenreException {

        GenreDTO updatedGenre = genreService.updateGenre(genreId, genreDTO);

        return ResponseEntity.ok(updatedGenre);
    }

    // Soft Delete Genre
    @DeleteMapping("/{genreId}")
    public ResponseEntity<ApiResponse> deleteGenre(@PathVariable Long genreId)
            throws GenreException {

        genreService.deleteGenre(genreId);

        ApiResponse response =
                new ApiResponse("Genre deleted (soft delete)", true);

        return ResponseEntity.ok(response);
    }

    // Hard Delete Genre
    @DeleteMapping("/{genreId}/hard")
    public ResponseEntity<ApiResponse> hardDeleteGenre(@PathVariable Long genreId)
            throws GenreException {

        genreService.hardDeleteGenre(genreId);

        ApiResponse response =
                new ApiResponse("Genre permanently deleted", true);

        return ResponseEntity.ok(response);
    }

    // Get Top Level Genres
    @GetMapping("/top-level")
    public ResponseEntity<List<GenreDTO>> getTopLevelGenres() {

        List<GenreDTO> genres = genreService.getTopLevelGenres();

        return ResponseEntity.ok(genres);
    }

    // Get Sub Genres
    @GetMapping("/{genreId}/sub-genres")
    public ResponseEntity<List<GenreDTO>> getSubGenres(@PathVariable Long genreId) {

        List<GenreDTO> subGenres = genreService.getSubGenres(genreId);

        return ResponseEntity.ok(subGenres);
    }

    // Get Genre Tree
    @GetMapping("/tree")
    public ResponseEntity<List<GenreDTO>> getGenreTree() {

        List<GenreDTO> genreTree = genreService.getGenreTree();

        return ResponseEntity.ok(genreTree);
    }

    // Search Genres
    @GetMapping("/search")
    public ResponseEntity<Page<GenreDTO>> searchGenres(
            @RequestParam(required = false) String searchTerm,
            Pageable pageable
    ) {

        Page<GenreDTO> genres = genreService.searchGenres(searchTerm, pageable);

        return ResponseEntity.ok(genres);
    }

    // Count Active Genres
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalActiveGenres() {

        long count = genreService.getTotalActiveGenres();

        return ResponseEntity.ok(count);
    }

    // Count Books in Genre
    @GetMapping("/{genreId}/book-count")
    public ResponseEntity<Long> getBookCountByGenre(@PathVariable Long genreId) {

        long count = genreService.getBookCountByGenre(genreId);

        return ResponseEntity.ok(count);
    }
}