package com.example.service;

import com.example.payload.dto.WishlistDTO;
import com.example.payload.response.PageResponse;

public interface WishlistService {

    WishlistDTO addToWishlist(Long bookId, String notes);

    void removeFromWishlist(Long bookId);

    PageResponse<WishlistDTO> getMyWishlist(int page, int size);

    boolean isBookInWishlist(Long bookId);

    long getWishlistCount();
}