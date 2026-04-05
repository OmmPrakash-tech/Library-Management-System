import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-my-reviews',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './my-reviews.html',
  styleUrls: ['./my-reviews.css']
})
export class MyReviews implements OnInit {

  reviews: any[] = [];
  editReviewId: number | null = null;
  editData: any = {};

  private baseUrl = 'http://localhost:5050/api/reviews';

  constructor(private http: HttpClient) {}

  ngOnInit(){
    this.getMyReviews();
  }

  // ================= GET ALL =================
  getMyReviews(){
    this.http.get<any[]>(`${this.baseUrl}/my`).subscribe({
      next: (res) => this.reviews = res || [],
      error: (err) => console.error(err)
    });
  }

  // ================= EDIT =================
  editReview(id: number){
    this.editReviewId = id;

    this.http.get<any>(`${this.baseUrl}/${id}`).subscribe({
      next: (res) => this.editData = res,
      error: (err) => console.error(err)
    });
  }

  // ================= UPDATE =================
  updateReview(){
    if (!this.editReviewId) return;

    this.http.put(
      `${this.baseUrl}/${this.editReviewId}`,
      this.editData
    ).subscribe({
      next: () => {
        alert("Review updated successfully ✅");
        this.editReviewId = null;
        this.getMyReviews(); // refresh list
      },
      error: (err) => console.error(err)
    });
  }

  // ================= CANCEL =================
  cancelEdit(){
    this.editReviewId = null;
    this.editData = {};
  }
}