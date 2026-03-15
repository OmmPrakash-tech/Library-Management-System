package com.example.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.domain.BookLoanStatus;
import com.example.mapper.WishlistMapper;
import com.example.model.Book;
import com.example.model.BookLoan;
import com.example.model.User;
import com.example.model.Wishlist;
import com.example.payload.dto.WishlistDTO;
import com.example.payload.response.PageResponse;
import com.example.repository.BookLoanRepository;
import com.example.repository.BookRepository;
import com.example.repository.WishlistRepository;
import com.example.service.UserService;
import com.example.service.WishlistService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserService userService;
    private final BookRepository bookRepository;
    private final WishlistMapper wishlistMapper;
    private final BookLoanRepository bookLoanRepository;

    @Override
    public WishlistDTO addToWishlist(Long bookId, String notes) throws Exception {

        User user = userService.getCurrentUser();

        // validate book exists
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new Exception("Book not found"));

        // check if already exists
        if (wishlistRepository.existsByUserIdAndBookId(user.getId(), bookId)) {
            throw new Exception("Book is already in your wishlist");
        }

        // create wishlist
        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setBook(book);
        wishlist.setNotes(notes);

        Wishlist saved = wishlistRepository.save(wishlist);

        return wishlistMapper.toDTO(saved);
    }

    @Override
   public void removeFromWishlist(Long bookId) throws Exception {

    User user = userService.getCurrentUser();

    Wishlist wishlist = wishlistRepository.findByUserIdAndBookId(
            user.getId(),
            bookId
    );

    if (wishlist == null) {
        throw new Exception("book is not in your wishlist");
    }

    wishlistRepository.delete(wishlist);
}

    @Override
    public PageResponse<WishlistDTO> getMyWishlist(int page, int size) {

        Long userId = userService.getCurrentUser().getId();

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
                .collect(Collectors.toList());

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
    List<BookLoan> bookLoans = bookLoanRepository.findByBookId(bookId);

    return bookLoans.stream()
            .anyMatch(loan -> loan.getUser().getId().equals(userId) &&
                    loan.getStatus() == BookLoanStatus.RETURNED);
}
}
