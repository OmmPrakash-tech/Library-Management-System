import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-review-user',
  standalone: true,
  imports: [CommonModule, FormsModule, MatSnackBarModule],
  templateUrl: './review-user.html',
  styleUrls: ['./review-user.css']
})
export class ReviewUserComponent implements OnInit {

    isEditMode: boolean = false;
editReviewId!: number;
  alreadyReviewed: boolean = false;
  myReviews: any[] = [];

  bookId!: number;

  review = {
    bookId: 0,
    rating: 5,
    title: '',
    reviewText: ''
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) {}

  // ✅ SINGLE ngOnInit
  ngOnInit(): void {
    this.bookId = Number(this.route.snapshot.paramMap.get('bookId'));
    this.review.bookId = this.bookId;

    // 🔥 CHECK IF ALREADY REVIEWED
    this.http.get<boolean>(`http://library-backend-docker.onrender.com/api/reviews/book/${this.bookId}/exists`)
      .subscribe(res => {
        this.alreadyReviewed = res;
      });
  }

  // ✅ SUBMIT REVIEW
  submitReview() {

    // 🔴 BLOCK IF ALREADY REVIEWED
    if (this.alreadyReviewed) {
      this.snackBar.open("You have already reviewed this book ❌", "Close", {
        duration: 3000
      });
      return;
    }

    if (!this.review.reviewText || this.review.reviewText.length < 10) {
      this.snackBar.open("Review must be at least 10 characters", "Close", {
        duration: 3000
      });
      return;
    }

    this.http.post('http://library-backend-docker.onrender.com/api/reviews', this.review)
      .subscribe({
        next: () => {

          this.snackBar.open('Review Submitted Successfully ✅', 'Close', {
            duration: 3000,
            panelClass: ['success-snackbar']
          });

          this.alreadyReviewed = true; // 🔥 update state

          this.router.navigate(['/my-loans']);
        },
        error: (err) => {
          this.snackBar.open(err.error?.message || 'Error submitting review', 'Close', {
            duration: 3000
          });
        }
      });
  }

  // ✅ GET MY REVIEWS
  getMyReviews() {
    this.http.get<any>('http://library-backend-docker.onrender.com/api/reviews/my')
      .subscribe({
        next: (res) => {

          this.snackBar.open('Fetched your reviews ✅', 'Close', {
  duration: 3000,
  panelClass: ['success-snackbar'],
//   horizontalPosition: 'right',   // 👉 move to right
//   verticalPosition: 'top'        // 👉 top popup
});

          this.myReviews = res.content || res;
        },
        error: (err) => {
          this.snackBar.open(err.error?.message || 'Failed to fetch reviews', 'Close', {
            duration: 3000
          });
        }
      });
  }

  // ✅ NAVIGATION
  goBack() {
    this.router.navigate(['/renew-return']);
  }

  // ⭐ STAR RATING
  setRating(star: number) {
    this.review.rating = star;
  }

  editReview(r: any) {
  this.isEditMode = true;
  this.editReviewId = r.id;

  this.review = {
    bookId: r.bookId,
    rating: r.rating,
    title: r.title,
    reviewText: r.reviewText
  };

  this.snackBar.open('Edit mode activated ✏️', 'Close', {
    duration: 2000
  });
}

  updateReview() {

  this.http.put(`http://library-backend-docker.onrender.com/api/reviews/${this.editReviewId}`, this.review)
    .subscribe({
      next: () => {

        this.snackBar.open('Review Updated Successfully ✅', 'Close', {
          duration: 3000
        });

        this.isEditMode = false;

        this.getMyReviews(); // 🔥 refresh list

        this.resetForm();
      },
      error: (err) => {
        this.snackBar.open(err.error?.message || 'Update failed', 'Close', {
          duration: 3000
        });
      }
    });
}

  resetForm() {
  this.review = {
    bookId: this.bookId,
    rating: 5,
    title: '',
    reviewText: ''
  };
}
}