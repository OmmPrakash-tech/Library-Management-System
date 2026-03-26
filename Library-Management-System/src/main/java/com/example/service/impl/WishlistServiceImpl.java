package com.example.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.domain.BookLoanStatus;
import com.example.exception.AlreadyExistsException;
import com.example.exception.ResourceNotFoundException;
import com.example.mapper.WishlistMapper;
import com.example.model.Book;
import com.example.model.User;
import com.example.model.Wishlist;
import com.example.payload.dto.WishlistDTO;
import com.example.payload.response.PageResponse;
import com.example.repository.BookLoanRepository;
import com.example.repository.BookRepository;
import com.example.repository.WishlistRepository;
import com.example.service.UserService;
import com.example.service.WishlistService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserService userService;
    private final BookRepository bookRepository;
    private final WishlistMapper wishlistMapper;
    private final BookLoanRepository bookLoanRepository;

@Transactional
@Override
public WishlistDTO addToWishlist(Long bookId, String notes) {

    User user = userService.getCurrentUser();

    // validate book exists
    Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

    // check if already exists
    if (wishlistRepository.existsByUserIdAndBookId(user.getId(), bookId)) {
        throw new AlreadyExistsException("Book is already in your wishlist");
    }

    // clean notes
    String cleanNotes = notes != null ? notes.trim() : null;

    // create wishlist
    Wishlist wishlist = Wishlist.builder()
            .user(user)
            .book(book)
            .notes(cleanNotes)
            .build();

    Wishlist saved = wishlistRepository.save(wishlist);

    return wishlistMapper.toDTO(saved);
}

@Transactional
@Override
public void removeFromWishlist(Long bookId) {

    User user = userService.getCurrentUser();

    Wishlist wishlist = wishlistRepository
            .findByUserIdAndBookId(user.getId(), bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book is not in your wishlist"));

    wishlistRepository.delete(wishlist);
}

@Override
public PageResponse<WishlistDTO> getMyWishlist(int page, int size) {

    Long userId = userService.getCurrentUser().getId();

    page = Math.max(page, 0);
    size = Math.min(Math.max(size, 1), 50);

    Pageable pageable = PageRequest.of(
            page,
            size,
            Sort.by("addedAt").descending()
    );

    Page<Wishlist> wishlistPage = wishlistRepository.findByUserId(userId, pageable);

    return convertToPageResponse(wishlistPage);
}

  private PageResponse<WishlistDTO> convertToPageResponse(Page<Wishlist> wishlistPage) {

    List<WishlistDTO> wishlistDTOs = wishlistPage.getContent()
            .stream()
            .map(wishlistMapper::toDTO)
            .toList();

    return new PageResponse<>(
            wishlistDTOs,
            wishlistPage.getNumber(),
            wishlistPage.getSize(),
            wishlistPage.getTotalElements(),
            wishlistPage.getTotalPages(),
            wishlistPage.isLast(),
            wishlistPage.isFirst(),
            wishlistPage.isEmpty()
    );
}

  private boolean hasUserReadBook(Long userId, Long bookId) {

    return bookLoanRepository.existsByUserIdAndBookIdAndStatus(
            userId,
            bookId,
            BookLoanStatus.RETURNED
    );
}

@Override
public boolean isBookInWishlist(Long bookId) {

    Long userId = userService.getCurrentUser().getId();

    return wishlistRepository.existsByUserIdAndBookId(userId, bookId);
}

@Override
public long getWishlistCount() {

    Long userId = userService.getCurrentUser().getId();

    return wishlistRepository.countByUserId(userId);
}
}
