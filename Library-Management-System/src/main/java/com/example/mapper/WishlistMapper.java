package com.example.mapper;

import org.springframework.stereotype.Component;

import com.example.model.Wishlist;
import com.example.payload.dto.WishlistDTO;

@Component
public class WishlistMapper {

    private final BookMapper bookMapper;

    public WishlistMapper(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    public WishlistDTO toDTO(Wishlist wishlist) {

        if (wishlist == null) return null;

        var user = wishlist.getUser();
        var book = wishlist.getBook();

        return WishlistDTO.builder()
                .id(wishlist.getId())
                .userId(user != null ? user.getId() : null)
                .userFullName(user != null ? user.getFullName() : null)
                .book(book != null ? bookMapper.toDTO(book) : null)
                .addedAt(wishlist.getAddedAt())
                .notes(wishlist.getNotes())
                .build();
    }
}