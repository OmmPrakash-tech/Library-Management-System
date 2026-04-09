package com.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.payload.dto.WishlistDTO;
import com.example.payload.response.ApiResponse;
import com.example.payload.response.PageResponse;
import com.example.service.WishlistService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins="https://library-management-system-3d9t.onrender.com")
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping("/{bookId}")
    public ResponseEntity<WishlistDTO> addToWishlist(
            @PathVariable Long bookId,
            @RequestParam(required = false) String notes
    ) {
        return ResponseEntity.ok(wishlistService.addToWishlist(bookId, notes));
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<ApiResponse> removeFromWishlist(
            @PathVariable Long bookId
    ) {
        wishlistService.removeFromWishlist(bookId);

        return ResponseEntity.ok(
                new ApiResponse("Book removed from wishlist successfully", true)
        );
    }

    @GetMapping
    public ResponseEntity<PageResponse<WishlistDTO>> getMyWishlist(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(wishlistService.getMyWishlist(page, size));
    }

    @GetMapping("/exists/{bookId}")
    public ResponseEntity<Boolean> isInWishlist(@PathVariable Long bookId) {
        return ResponseEntity.ok(wishlistService.isBookInWishlist(bookId));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getWishlistCount() {
        return ResponseEntity.ok(wishlistService.getWishlistCount());
    }
}