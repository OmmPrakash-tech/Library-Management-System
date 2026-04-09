import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-reviews',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './review.html',
  styleUrls: ['./review.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReviewComponent implements OnInit {

  reviews: any[] = [];
  page = 0;
  size = 10;

  BASE_URL = 'http://library-backend-docker.onrender.com/api/reviews';

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loadReviews();
  }

  // 📌 GET ALL REVIEWS
  loadReviews() {
    this.http.get<any>(`${this.BASE_URL}?page=${this.page}&size=${this.size}`)
      .subscribe({
        next: (res) => {
          console.log("Reviews:", res);

          // ✅ match your backend response
          this.reviews = res?.content || [];

          // 🔥 trigger UI update (OnPush)
          this.cdr.markForCheck();
        },
        error: (err) => console.error(err)
      });
  }

  // ❌ DELETE REVIEW
  deleteReview(id: number) {
    if (!confirm("Delete this review?")) return;

    this.http.delete(`${this.BASE_URL}/${id}`)
      .subscribe({
        next: () => {
          alert("Review deleted");

          // 🔥 reload + trigger UI
          this.loadReviews();
          this.cdr.markForCheck();
        },
        error: (err) => console.error(err)
      });
  }

  // 🔄 REFRESH
  refresh() {
    this.loadReviews();
  }
}