package com.example.mapper;

import org.springframework.stereotype.Component;

import com.example.exception.BookException;
import com.example.model.Book;
import com.example.model.Genre;
import com.example.payload.dto.BookDTO;
import com.example.repository.GenreRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookMapper {

    private final GenreRepository genreRepository;

    public BookDTO toDTO(Book book) {

        if (book == null) {
            return null;
        }

        return BookDTO.builder()
                .id(book.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .author(book.getAuthor())
                .genreId(book.getGenre() != null ? book.getGenre().getId() : null)
                .genreName(book.getGenre() != null ? book.getGenre().getName() : null)
                .genreCode(book.getGenre() != null ? book.getGenre().getCode() : null)
                .publisher(book.getPublisher())
                .publishedDate(book.getPublishedDate())
                .language(book.getLanguage())
                .pages(book.getPages())
                .description(book.getDescription())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .price(book.getPrice())
                .coverImageUrl(book.getCoverImageUrl())
                .active(book.getActive())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }

    public Book toEntity(BookDTO dto) throws BookException {

        if (dto == null) {
            return null;
        }

        Book book = new Book();

        book.setId(dto.getId());
        book.setIsbn(dto.getIsbn());
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());

        if (dto.getGenreId() != null) {

            Genre genre = genreRepository.findById(dto.getGenreId())
                    .orElseThrow(() ->
                            new BookException("Genre with ID " + dto.getGenreId() + " not found"));

            book.setGenre(genre);
        }

        book.setPublisher(dto.getPublisher());
        book.setPublishedDate(dto.getPublishedDate());
        book.setLanguage(dto.getLanguage());
        book.setPages(dto.getPages());
        book.setDescription(dto.getDescription());
        book.setTotalCopies(dto.getTotalCopies());
        book.setAvailableCopies(dto.getAvailableCopies());
        book.setPrice(dto.getPrice());
        book.setCoverImageUrl(dto.getCoverImageUrl());

        book.setActive(dto.getActive() != null ? dto.getActive() : true);

        return book;
    }

    public void updateEntityFromDTO(BookDTO dto, Book book) throws BookException {

        if (dto == null || book == null) {
            return;
        }

        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());

        if (dto.getGenreId() != null) {

            Genre genre = genreRepository.findById(dto.getGenreId())
                    .orElseThrow(() ->
                            new BookException("Genre with ID " + dto.getGenreId() + " not found"));

            book.setGenre(genre);
        }

        book.setPublisher(dto.getPublisher());
        book.setPublishedDate(dto.getPublishedDate());
        book.setLanguage(dto.getLanguage());
        book.setPages(dto.getPages());
        book.setDescription(dto.getDescription());
        book.setTotalCopies(dto.getTotalCopies());
        book.setAvailableCopies(dto.getAvailableCopies());
        book.setPrice(dto.getPrice());
        book.setCoverImageUrl(dto.getCoverImageUrl());

        if (dto.getActive() != null) {
            book.setActive(dto.getActive());
        }
    }
}