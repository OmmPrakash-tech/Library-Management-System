import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class BookLoanService {

  private selectedBooks: any[] = [];   // ✅ array now

  // ➕ Add book
  setBook(book: any) {
    // prevent duplicate
    const exists = this.selectedBooks.find(b => b.id === book.id);
    if (!exists) {
      this.selectedBooks.push(book);
    }
  }

  // 📥 Get all books
  getBook() {
    return this.selectedBooks;
  }

  // ❌ Remove one book
  removeBook(bookId: number) {
    this.selectedBooks = this.selectedBooks.filter(b => b.id !== bookId);
  }

  // 🧹 Clear all
  clearBook() {
    this.selectedBooks = [];
  }
}