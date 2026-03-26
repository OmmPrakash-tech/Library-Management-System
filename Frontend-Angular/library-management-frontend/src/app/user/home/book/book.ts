import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookService } from './bookService';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';   // ✅ ADD

@Component({
  selector: 'app-book',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './book.html',
  styleUrls: ['./book.css']
})
export class Book implements OnInit {

  books: any[] = [];
  searchText: string = '';

  private baseUrl = 'http://localhost:5050/api/wishlist'; // ✅ ADD

  constructor(
    private bookService: BookService,
    private router: Router,
    private http: HttpClient   // ✅ ADD
  ) {}

  ngOnInit() {
    this.loadBooks();
  }

  // Load all books
  loadBooks() {
    this.bookService.getAllBooks().subscribe((data: any) => {
      this.books = data;
    });
  }

  // Search books
  searchBooks() {
    this.bookService.searchBooks(this.searchText).subscribe((data: any) => {
      this.books = data;
    });
  }

  borrowBook(book: any) {
    alert(`Borrow: ${book.title}`);
  }

  // ✅ FIXED
  addToWishlist(bookId: number) {
    console.log('Adding book ID:', bookId); // debug

    this.http.post(`${this.baseUrl}/${bookId}`, {}).subscribe({
      next: () => {
        alert('Added to wishlist ✅');
      },
      error: (err) => console.error(err)
    });
  }

  viewBook(bookId: number) {
  this.router.navigate(['/book', bookId]);
}

}