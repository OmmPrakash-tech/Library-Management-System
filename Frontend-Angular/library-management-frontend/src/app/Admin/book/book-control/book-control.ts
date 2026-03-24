import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookControlService } from '../book-control.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-book-control',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './book-control.html',
  styleUrls: ['./book-control.css']
})
export class BookControl implements OnInit {

  books: any[] = [];
  selectedBook: any = null;

  constructor(private router: Router, private bookService: BookControlService) {}

  ngOnInit() {
    this.loadBooks(); // ✅ AUTO LOAD
  }

  // 📌 Load all books
  loadBooks() {
    this.bookService.getAllBooks().subscribe(res => {
      this.books = res;
    });
  }

  // 📌 Add book (you can connect form later)
onAddBook() {
  this.router.navigate(['/add-book']); // 🔥 NAVIGATION
}

  // 📌 View books (reload)
  onViewBooks() {
    this.loadBooks();
  }

  // 📌 Manage books
onManageBooks() {
  this.router.navigate(['/manage-books']);
}

// 📌 Soft delete
deleteBook(id: number) {
  this.bookService.softDeleteBook(id).subscribe(() => {
    this.loadBooks();
  });
}

// 📌 Hard delete
hardDeleteBook(id: number) {
  this.bookService.hardDeleteBook(id).subscribe(() => {
    this.loadBooks();
  });
}

  // 📌 Get by ISBN
  findByIsbn(isbn: string) {
    this.bookService.getBookByIsbn(isbn).subscribe(res => {
      this.selectedBook = res;
    });
  }

  viewBook(id: number) {
  this.router.navigate(['/view-book', id]); // 🔥 navigate with id
}

  // 📌 Stats
  loadStats() {
    this.bookService.getBookStats().subscribe(res => {
      console.log("Stats:", res);
    });
  }
}