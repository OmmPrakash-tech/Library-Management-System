package com.example.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.model.Genre;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    // Active genres sorted
    List<Genre> findByActiveTrueOrderByDisplayOrderAsc();

    // Root genres
    List<Genre> findByParentGenreIsNullAndActiveTrueOrderByDisplayOrderAsc();

    // Sub genres
    List<Genre> findByParentGenreIdAndActiveTrueOrderByDisplayOrderAsc(Long parentGenreId);

    // Find by code
    Optional<Genre> findByCode(String code);

    Optional<Genre> findByCodeIgnoreCase(String code);

    Optional<Genre> findByCodeAndActiveTrue(String code);

    // Duplicate check
    boolean existsByCodeIgnoreCase(String code);

    // Search by name
Page<Genre> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // Count active
    long countByActiveTrue();
}