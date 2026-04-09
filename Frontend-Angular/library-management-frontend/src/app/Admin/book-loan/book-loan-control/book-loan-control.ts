import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-book-loan-control',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './book-loan-control.html',
  styleUrls: ['./book-loan-control.css']
})
export class BookLoanControlComponent implements OnInit {

  loans: any[] = [];
  showRequests: boolean = false;
  loading: boolean = false;

  private BASE_URL = 'https://library-backend-docker.onrender.com/api/book-loans';

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef   // ✅ added
  ) {}

  ngOnInit(): void {
    this.getAllLoans();
  }

  // ✅ Headers with JWT
  getHeaders() {
    const token = localStorage.getItem('token');

    if (!token) {
      alert("Login required");
      return { headers: new HttpHeaders() };
    }

    return {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`
      })
    };
  }

  // ✅ Get ALL loans
  getAllLoans() {
    this.loading = true;

    this.http.get<any>(`${this.BASE_URL}/all`, this.getHeaders())
      .subscribe({
        next: (res) => {
          this.loans = res?.content || [];
          this.loading = false;

          this.cdr.detectChanges();   // ✅ force UI update
        },
        error: (err) => {
          console.error(err);
          alert("Failed to load all loans");
          this.loading = false;

          this.cdr.detectChanges();
        }
      });
  }

  // ✅ Get ONLY return requests
  getReturnRequests() {
    this.loading = true;

    this.http.get<any>(`${this.BASE_URL}/return-requests`, this.getHeaders())
      .subscribe({
        next: (res) => {
          this.loans = res?.content || [];
          this.loading = false;

          this.cdr.detectChanges();   // ✅ important
        },
        error: (err) => {
          console.error(err);
          alert("Failed to load return requests");
          this.loading = false;

          this.cdr.detectChanges();
        }
      });
  }

  // ✅ Toggle between ALL and REQUESTS
  toggleRequests() {
    this.showRequests = !this.showRequests;

    console.log("SHOW REQUESTS:", this.showRequests);

    if (this.showRequests) {
      this.getReturnRequests();
    } else {
      this.getAllLoans();
    }

    this.cdr.detectChanges();   // ✅ ensure UI updates instantly
  }

  // ✅ Approve return
  approveReturn(id: number, status: string) {

  if (status !== 'RETURN_REQUESTED') {
    alert("No return request found");
    return;
  }

  if (!confirm("Approve this return request?")) return;

  this.http.post<any>(
    `${this.BASE_URL}/approve-return/${id}`,
    {},
    {
      ...this.getHeaders(),
      responseType: 'json'
    }
  ).subscribe({   // ✅ MISSING PART FIXED

    next: (res) => {
      console.log("RESPONSE:", res);

      alert(res?.message || 'Book returned successfully');

      // 🔥 refresh correct view
      if (this.showRequests) {
        this.getReturnRequests();
      } else {
        this.getAllLoans();
      }

      this.cdr.detectChanges();
    },

    error: (err) => {
      console.error("ERROR:", err);
      alert(err?.error?.message || "Approve failed");

      this.cdr.detectChanges();
    }

  });
}
}