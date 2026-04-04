import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-book-view',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './book-view.html',
  styleUrls: ['./book-view.css'],
  changeDetection: ChangeDetectionStrategy.OnPush // 🔥 IMPORTANT
})
export class BookViewComponent implements OnInit {

  book: any;
  isWishlisted: boolean = false;
  reviews: any[] = []; // ⭐ NEW

  private baseUrl = 'http://localhost:5050/api/wishlist';
  private reviewUrl = 'http://localhost:5050/api/reviews';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');

    if (id) {
      this.loadBook(id);
      this.checkWishlist(id);
      this.loadReviews(id); // ⭐ LOAD REVIEWS
    }
  }

  // 📘 Load book
  loadBook(id: any) {
    this.http.get(`http://localhost:5050/api/books/${id}`)
      .subscribe((data: any) => {
        this.book = data;
        this.cdr.markForCheck();
      });
  }

  // ⭐ Load reviews of this book
  loadReviews(bookId: any) {
    this.http.get<any>(`${this.reviewUrl}/book/${bookId}`)
      .subscribe(res => {
        this.reviews = res?.content || [];
        this.cdr.markForCheck();
      });
  }

  // 📚 Borrow
  borrowBook() {
    alert(`Borrowing ${this.book?.title}`);
  }

  // ❤️ Wishlist toggle
  toggleWishlist() {
    if (this.isWishlisted) {
      this.http.delete(`${this.baseUrl}/${this.book.id}`).subscribe(() => {
        this.isWishlisted = false;
        this.cdr.markForCheck();
      });
    } else {
      this.http.post(`${this.baseUrl}/${this.book.id}`, {}).subscribe(() => {
        this.isWishlisted = true;
        this.cdr.markForCheck();
      });
    }
  }

  // 🔍 Check wishlist
  checkWishlist(bookId: any) {
    this.http.get<boolean>(`${this.baseUrl}/exists/${bookId}`)
      .subscribe(res => {
        this.isWishlisted = res;
        this.cdr.markForCheck();
      });
  }

  // 🔙 Back
  goBack() {
    this.router.navigate(['/book']);
  }
}