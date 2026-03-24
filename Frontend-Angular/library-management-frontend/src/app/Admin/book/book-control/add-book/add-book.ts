import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { BookControlService } from '../../book-control.service';

@Component({
  selector: 'app-add-book',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-book.html',
  styleUrls: ['./add-book.css']
})
export class AddBook {

  book = {
    isbn: '',
    title: '',
    author: '',
    genreId: 0,
    publisher: '',
    publishedDate: '',
    language: '',
    pages: 0,
    description: '',
    totalCopies: 0,
    availableCopies: 0,
    price: 0
  };

  constructor(
    private bookService: BookControlService,
    private router: Router
  ) {}

  addBook() {

    if (!this.book.title || !this.book.author || !this.book.isbn) {
      alert("Required fields missing!");
      return;
    }

    // 🔥 IMPORTANT FIX
    this.book.availableCopies = this.book.totalCopies;

    this.bookService.createBook(this.book).subscribe({
      next: () => {
        alert("Book added successfully!");
        this.router.navigate(['/book-control']);
      },
      error: (err) => {
        console.log("ERROR:", err);
        alert("Error adding book");
      }
    });
  }

  goBack() {
    this.router.navigate(['/book-control']);
  }
}