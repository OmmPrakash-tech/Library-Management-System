import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookService } from './bookService';
import { FormsModule } from '@angular/forms';

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

  constructor(private bookService: BookService) {}

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

  addToWishlist(book: any) {
    alert(`Wishlist: ${book.title}`);
  }
}