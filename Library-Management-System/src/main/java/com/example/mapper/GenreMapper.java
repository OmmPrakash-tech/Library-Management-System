package com.example.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.model.Genre;
import com.example.payload.dto.GenreDTO;
import com.example.repository.GenreRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GenreMapper {

    private final GenreRepository genreRepository;

    // ENTITY → DTO
    public GenreDTO toDTO(Genre genre) {

        if (genre == null) {
            return null;
        }

        GenreDTO dto = GenreDTO.builder()
                .id(genre.getId())
                .code(genre.getCode())
                .name(genre.getName())
                .description(genre.getDescription())
                .displayOrder(genre.getDisplayOrder())
                .active(genre.getActive())
                .createdAt(genre.getCreatedAt())
                .updatedAt(genre.getUpdatedAt())
                .build();

        // Parent Genre Mapping
        if (genre.getParentGenre() != null) {
            dto.setParentGenreId(genre.getParentGenre().getId());
            dto.setParentGenreName(genre.getParentGenre().getName());
        }

        // Sub-Genres Mapping
        if (genre.getSubGenres() != null && !genre.getSubGenres().isEmpty()) {

            dto.setSubGenres(
                    genre.getSubGenres().stream()
                            .filter(Genre::getActive)
                            .map(this::toDTO)
                            .toList()
            );
        }

        return dto;
    }

    // DTO → ENTITY
    public Genre toEntity(GenreDTO dto) {

        if (dto == null) {
            return null;
        }

        Genre genre = Genre.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .description(dto.getDescription())
                .displayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0)
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();

        if (dto.getParentGenreId() != null) {
            genreRepository.findById(dto.getParentGenreId())
                    .ifPresent(genre::setParentGenre);
        }

        return genre;
    }

    // Update Existing Entity
    public void updateEntityFromDTO(GenreDTO dto, Genre existingGenre) {

        if (dto == null || existingGenre == null) {
            return;
        }

        existingGenre.setCode(dto.getCode());
        existingGenre.setName(dto.getName());
        existingGenre.setDescription(dto.getDescription());
        existingGenre.setDisplayOrder(
                dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0
        );

        if (dto.getActive() != null) {
            existingGenre.setActive(dto.getActive());
        }

        if (dto.getParentGenreId() != null) {
            genreRepository.findById(dto.getParentGenreId())
                    .ifPresent(existingGenre::setParentGenre);
        }
    }

    // List Mapping
    public List<GenreDTO> toDTOList(List<Genre> genreList) {

        if (genreList == null || genreList.isEmpty()) {
            return List.of();
        }

        return genreList.stream()
                .map(this::toDTO)
                .toList();
    }
}