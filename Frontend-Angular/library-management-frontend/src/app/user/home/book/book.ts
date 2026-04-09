import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookService } from './bookService';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { BookLoanService } from '../book-loan-front/book-loan.service';

@Component({
  selector: 'app-book',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './book.html',
  styleUrls: ['./book.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class Book implements OnInit {

  books: any[] = [];
  genres: any[] = [];              // ✅ NEW (dynamic genres)

  searchText: string = '';
  selectedGenre: number | null = null;

  noResults: boolean = false;

  private baseUrl = 'https://library-backend-docker.onrender.com/api/wishlist';

  constructor(
    private bookService: BookService,
    private router: Router,
    private http: HttpClient,
    private loanService: BookLoanService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loadBooks();
    this.loadGenres(); // ✅ LOAD GENRES
  }

  // 📚 LOAD ALL BOOKS
  loadBooks() {
    this.bookService.getAllBooks().subscribe((data: any) => {

      // ⚠️ Handle both normal array & PageResponse
      this.books = data.content ? data.content : data;

      this.noResults = false;

      this.cdr.markForCheck();
    });
  }

  // 📂 LOAD GENRES (NEW)
  loadGenres() {
    this.bookService.getGenres().subscribe((res: any) => {
      this.genres = res;
      this.cdr.markForCheck();
    });
  }

  // 🔍 SEARCH + FILTER
  searchBooks() {
    this.bookService
      .searchBooks(this.searchText, this.selectedGenre!)
      .subscribe((res: any) => {

        this.books = res.content;

        // ✅ show message only when user searched
        this.noResults = this.books.length === 0 && this.searchText.trim() !== '';

        this.cdr.markForCheck();
      });
  }

  // 📚 BORROW
  borrowBook(book: any) {
    this.loanService.setBook(book);
    alert(`"${book.title}" sent to Issue Page for confirmation 📩`);
  }

  // ❤️ WISHLIST
  addToWishlist(bookId: number) {
    this.http.post(`${this.baseUrl}/${bookId}`, {}).subscribe({
      next: () => {
        alert('Added to wishlist ✅');
        this.cdr.markForCheck();
      },
      error: (err) => console.error(err)
    });
  }

  // 👁️ VIEW BOOK
  viewBook(bookId: number) {
    this.router.navigate(['/book', bookId]);
  }
}