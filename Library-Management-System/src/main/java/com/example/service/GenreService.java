package com.example.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.exception.GenreException;
import com.example.payload.dto.GenreDTO;

public interface GenreService {

    GenreDTO createGenre(GenreDTO genreDTO);

    List<GenreDTO> getAllGenres();

    GenreDTO getGenreById(Long genreId) throws GenreException;

    GenreDTO getGenreByCode(String code);

    GenreDTO updateGenre(Long genreId, GenreDTO genreDTO) throws GenreException;

    void deleteGenre(Long genreId);        // Soft delete

    void hardDeleteGenre(Long genreId);    // Permanent delete

    List<GenreDTO> getTopLevelGenres();

    List<GenreDTO> getSubGenres(Long parentGenreId);

    List<GenreDTO> getGenreTree();

    Page<GenreDTO> searchGenres(String searchTerm, Pageable pageable);

    long getTotalActiveGenres();

    long getBookCountByGenre(Long genreId);
}