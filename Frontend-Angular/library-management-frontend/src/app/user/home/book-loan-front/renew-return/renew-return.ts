import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-renew-return',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './renew-return.html',
  styleUrls: ['./renew-return.css']
})
export class RenewReturnComponent implements OnInit {

  loans: any[] = [];
  loading: boolean = true;

  // ✅ BASE API URL (PORT 5050)
  private BASE_URL = 'http://localhost:5050/api/book-loans';

  constructor(
    private http: HttpClient,
    private cd: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.fetchLoans();
  }

  // ✅ Headers with Token
  getHeaders() {
    const token = localStorage.getItem('token');

    console.log("TOKEN:", token); // Debug

    return {
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`
      })
    };
  }

  // ✅ Fetch Loans
  fetchLoans() {
    this.loading = true;

    this.http.get<any[]>(`${this.BASE_URL}/my`, this.getHeaders())
      .subscribe({
       next: (res: any) => {
  console.log("API RESPONSE:", res);

  this.loans = res.content;   // ✅ FIX
  this.loading = false;

  this.cd.detectChanges();
},
        error: (err) => {
          console.error('FULL ERROR:', err);
          console.error('STATUS:', err.status);
          console.error('MESSAGE:', err.error);

          this.loading = false;
          this.cd.detectChanges();
        }
      });
  }

  // ✅ Renew Book
renewBook(loan: any) {

  // ❌ Block if already returned
  if (loan.status !== 'CHECKED_OUT') {
    alert("Book already returned!");
    return;
  }

  // ❌ Block if max renew reached
  if (loan.renewalCount >= loan.maxRenewals) {
    alert("Maximum renew limit reached!");
    return;
  }

  // ❌ Block if backend says no
  if (!loan.canRenew) {
    alert("Renewal not allowed!");
    return;
  }

  // ✅ Ask user for extension days
  let days: any = prompt("Enter extension days (max 7):");

  if (!days) return;

  days = Number(days);

  // ❌ Validate input
  if (isNaN(days) || days <= 0) {
    alert("Invalid number!");
    return;
  }

  if (days > 7) {
    alert("Max 7 days allowed per renewal!");
    return;
  }

  const notes = prompt("Enter notes (optional):") || "Renewed";

  const body = {
    bookLoanId: loan.id,        // ✅ correct field
    extensionDays: days,        // ✅ required
    notes: notes                // ✅ optional
  };

  this.http.post(`${this.BASE_URL}/renew`, body, this.getHeaders())
    .subscribe({
      next: () => {
        alert('Book renewed successfully');
        this.fetchLoans();
      },
      error: (err) => {
        console.error('Renew failed:', err);
        alert(err.error?.message || 'Renew failed');
      }
    });
}

  // ✅ Return Book
returnBook(loan: any) {

  if (loan.status !== 'CHECKED_OUT') {
    alert("Book already returned!");
    return;
  }

  if (!confirm('Are you sure to return this book?')) return;

  const body = {
    bookLoanId: loan.id,      // ✅ correct field name
    condition: "RETURNED",    // ✅ required
    notes: "Finished reading" // ✅ optional (you can customize)
  };

  this.http.post(`${this.BASE_URL}/checkin`, body, this.getHeaders())
    .subscribe({
      next: () => {
        alert('Book returned successfully');
        this.fetchLoans();
      },
      error: (err) => {
        console.error('Return failed:', err);
        alert(err.error?.message || 'Return failed');
      }
    });
}

  // ✅ Overdue Check
  isOverdue(date: string): boolean {
    return new Date(date) < new Date();
  }
}