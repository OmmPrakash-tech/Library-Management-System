package com.example.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.exception.GenreException;
import com.example.mapper.GenreMapper;
import com.example.model.Genre;
import com.example.payload.dto.GenreDTO;
import com.example.repository.GenreRepository;
import com.example.service.GenreService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @Override
    public List<GenreDTO> getAllGenres() {

        List<Genre> genres =
                genreRepository.findByActiveTrueOrderByDisplayOrderAsc();

        return genreMapper.toDTOList(genres);
    }

    @Override
    @Transactional
    public GenreDTO createGenre(GenreDTO genreDTO) {

        Genre genre = genreMapper.toEntity(genreDTO);

        Genre savedGenre = genreRepository.save(genre);

        return genreMapper.toDTO(savedGenre);
    }

    @Override
    public GenreDTO getGenreById(Long genreId) {

        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new GenreException("Genre not found"));

        return genreMapper.toDTO(genre);
    }

    @Override
    @Transactional
    public GenreDTO updateGenre(Long genreId, GenreDTO genreDTO) {

        Genre existingGenre = genreRepository.findById(genreId)
                .orElseThrow(() -> new GenreException("Genre not found"));

        genreMapper.updateEntityFromDTO(genreDTO, existingGenre);

        Genre updatedGenre = genreRepository.save(existingGenre);

        return genreMapper.toDTO(updatedGenre);
    }

    @Override
    @Transactional
    public void deleteGenre(Long genreId) {

        Genre existingGenre = genreRepository.findById(genreId)
                .orElseThrow(() -> new GenreException("Genre not found"));

        existingGenre.setActive(false); // Soft delete

        genreRepository.save(existingGenre);
    }

    @Override
    @Transactional
    public void hardDeleteGenre(Long genreId) {

        Genre existingGenre = genreRepository.findById(genreId)
                .orElseThrow(() -> new GenreException("Genre not found"));

        genreRepository.delete(existingGenre);
    }

    @Override
    public List<GenreDTO> getTopLevelGenres() {

        List<Genre> topLevelGenres =
                genreRepository.findByParentGenreIsNullAndActiveTrueOrderByDisplayOrderAsc();

        return genreMapper.toDTOList(topLevelGenres);
    }

    @Override
    public Page<GenreDTO> searchGenres(String searchTerm, Pageable pageable) {

        Page<Genre> genrePage = genreRepository.findAll(pageable);

        return genrePage.map(genreMapper::toDTO);
    }

    @Override
    public long getTotalActiveGenres() {
        return genreRepository.countByActiveTrue();
    }

    @Override
    public long getBookCountByGenre(Long genreId) {

        Genre genre = genreRepository.findById(genreId)
                .orElseThrow(() -> new GenreException("Genre not found"));

        if (genre.getSubGenres() == null) {
            return 0;
        }

        return genre.getSubGenres().size();
    }
}