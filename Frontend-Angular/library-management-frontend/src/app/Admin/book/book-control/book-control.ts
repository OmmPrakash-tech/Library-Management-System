import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BookControlService } from '../book-control.service';
import { Router } from '@angular/router';


@Component({
  selector: 'app-book-control',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './book-control.html',
  styleUrls: ['./book-control.css'],
  
})
export class BookControl implements OnInit {

  books: any[] = [];
  selectedBook: any = null;

  constructor(
    private router: Router,
    private bookService: BookControlService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loadBooks();
  }

  loadBooks() {
    this.bookService.getAllBooks().subscribe(res => {
      this.books = res;
      this.cdr.markForCheck();
    });
  }

  onAddBook() {
    this.router.navigate(['/add-book']);
  }

  onViewBooks() {
    this.loadBooks();
  }

  onManageBooks() {
    this.router.navigate(['/manage-books']);
  }

  hardDeleteBook(id: number) {
    this.bookService.hardDeleteBook(id).subscribe(() => {
      this.loadBooks();
      this.cdr.markForCheck();
    });
  }

  findByIsbn(isbn: string) {
    this.bookService.getBookByIsbn(isbn).subscribe(res => {
      this.selectedBook = res;
      this.cdr.markForCheck();
    });
  }

  viewBook(id: number) {
    this.router.navigate(['/view-book', id]);
  }

  loadStats() {
    this.bookService.getBookStats().subscribe(res => {
      console.log("Stats:", res);
    });
  }
}