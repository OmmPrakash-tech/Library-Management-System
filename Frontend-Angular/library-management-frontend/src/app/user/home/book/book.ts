import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
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
  changeDetection: ChangeDetectionStrategy.OnPush   // ✅ ADDED
})
export class Book implements OnInit {

  books: any[] = [];
  searchText: string = '';

  private baseUrl = 'http://localhost:5050/api/wishlist';

  constructor(
    private bookService: BookService,
    private router: Router,
    private http: HttpClient,
    private loanService: BookLoanService
  ) {}

  ngOnInit() {
    this.loadBooks();
  }

  // 📚 Load all books
  loadBooks() {
    this.bookService.getAllBooks().subscribe((data: any) => {
      this.books = data;
    });
  }

  // 🔍 Search books
  searchBooks() {
    this.bookService.searchBooks(this.searchText).subscribe((data: any) => {
      this.books = data;
    });
  }



borrowBook(book: any) {
  this.loanService.setBook(book);
  alert(`"${book.title}" sent to Issue Page for confirmation 📩`);
}

  // ❤️ Wishlist
  addToWishlist(bookId: number) {
    console.log('Adding book ID:', bookId);

    this.http.post(`${this.baseUrl}/${bookId}`, {}).subscribe({
      next: () => {
        alert('Added to wishlist ✅');
      },
      error: (err) => console.error(err)
    });
  }

  // 👁️ View Book
  viewBook(bookId: number) {
    this.router.navigate(['/book', bookId]);
  }

}