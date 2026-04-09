import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-wishlist',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './wishlist.html',
  styleUrls: ['./wishlist.css']
})
export class WishlistComponent implements OnInit {

  wishlist: any[] = [];
  count: number = 0;

  private baseUrl = 'https://library-backend-docker.onrender.com/api/wishlist';

  constructor(
    private http: HttpClient,
    private router: Router,
    private cdr: ChangeDetectorRef   // ✅ added
  ) {}

  ngOnInit(): void {
    this.loadWishlist();
    this.getCount();
  }

  // ✅ Load wishlist
loadWishlist() {
  this.http.get<any>(this.baseUrl).subscribe({
    next: (data) => {
      console.log('Wishlist API:', data);

      // ✅ HANDLE PAGINATION
      this.wishlist = data.content || [];

      this.count = data.totalElements || this.wishlist.length;

      this.cdr.detectChanges();
    },
    error: (err) => console.error(err)
  });
}

  // ✅ Remove from wishlist
  removeFromWishlist(bookId: number) {
    this.http.delete(`${this.baseUrl}/${bookId}`).subscribe({
      next: () => {
        this.loadWishlist();
        this.getCount();

        this.cdr.detectChanges();   // ✅ update immediately
      },
      error: (err) => console.error(err)
    });
  }

  // ✅ Get count
  getCount() {
    this.http.get<number>(`${this.baseUrl}/count`).subscribe({
      next: (data) => {
        this.count = data;

        this.cdr.detectChanges();   // ✅ update count
      },
      error: (err) => console.error(err)
    });
  }

  viewBook(bookId: number) {
  this.router.navigate(['/book', bookId]);
}
}