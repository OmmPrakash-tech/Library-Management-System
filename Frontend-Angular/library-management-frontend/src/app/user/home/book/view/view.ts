import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-book-view',
   standalone: true,               // ✅ make sure this is present
  imports: [CommonModule], 
  templateUrl: './book-view.html',
  styleUrls: ['./book-view.css']
})
export class BookViewComponent implements OnInit {

  book: any;
  isWishlisted: boolean = false;

  private baseUrl = 'http://localhost:5050/api/wishlist';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private cdr: ChangeDetectorRef   // ✅ ADD
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');

    // ✅ Load book details
    this.http.get(`http://localhost:5050/api/books/${id}`)
      .subscribe((data: any) => {
        this.book = data;

        this.cdr.detectChanges();   // ✅ update UI
      });

    // ✅ Check wishlist
    this.checkWishlist(id);
  }

  // ✅ Borrow
  borrowBook() {
    alert(`Borrowing ${this.book?.title}`);
  }

  // ✅ Toggle Wishlist
  toggleWishlist() {

    if (this.isWishlisted) {
      this.http.delete(`${this.baseUrl}/${this.book.id}`).subscribe(() => {
        this.isWishlisted = false;

        this.cdr.detectChanges();   // ✅ force refresh
      });

    } else {
      this.http.post(`${this.baseUrl}/${this.book.id}`, {}).subscribe(() => {
        this.isWishlisted = true;

        this.cdr.detectChanges();   // ✅ force refresh
      });
    }
  }

  // ✅ Check if already in wishlist
  checkWishlist(bookId: any) {
    this.http.get<boolean>(`${this.baseUrl}/exists/${bookId}`)
      .subscribe(res => {
        this.isWishlisted = res;

        this.cdr.detectChanges();   // ✅ update button state
      });
  }

  // ✅ Back
  goBack() {
    this.router.navigate(['/book']);
  }
}