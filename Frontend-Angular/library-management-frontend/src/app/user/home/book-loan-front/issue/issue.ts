import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BookLoanService } from '../book-loan.service';

@Component({
  selector: 'app-issue',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './issue.html',
  styleUrls: ['./issue.css']
})
export class IssueComponent implements OnInit {

  pendingBooks: any[] = [];
  currentLoans: any[] = [];

  showModal: boolean = false;
  selectedBookId: number | null = null;
  isChecked: boolean = false;

  activeSubscription: any = null;
  hasActiveSubscription: boolean = false;

  private baseUrl = 'http://localhost:5050/api/book-loans';
  private subscriptionApi = 'http://localhost:5050/api/subscriptions/user/active';

  constructor(
    private loanService: BookLoanService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.loadPendingBooks();
    this.getActiveSubscription();
    this.loadCurrentLoans();   // 🔥 important
  }

  // ================= LOAD BOOKS =================
  loadPendingBooks() {
    this.pendingBooks = this.loanService.getBook();
  }

  // ================= LOAD CURRENT LOANS =================
  loadCurrentLoans() {
    const token = localStorage.getItem('token');

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    this.http.get<any[]>(`${this.baseUrl}/my`, { headers }).subscribe({
      next: (res) => {
        this.currentLoans = res;
        console.log("CURRENT LOANS:", res);
      },
      error: (err) => console.error(err)
    });
  }

  // ================= GET SUBSCRIPTION =================
  getActiveSubscription() {
    const token = localStorage.getItem('token');

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    this.http.get(this.subscriptionApi, { headers }).subscribe({
      next: (res: any) => {

        console.log("ACTIVE SUB:", res);

        if (res.success === false) {
          this.hasActiveSubscription = false;
          this.activeSubscription = null;
        } else if (res && res.planId) {
          this.hasActiveSubscription = true;
          this.activeSubscription = res;
        } else {
          this.hasActiveSubscription = false;
        }
      },
      error: () => {
        this.hasActiveSubscription = false;
      }
    });
  }

  // ================= PLAN LOGIC =================
  getDaysFromPlan(planId: number): number {
    switch (planId) {
      case 1: return 7;
      case 2: return 14;
      case 3: return 30;
      default: return 7;
    }
  }

  getMaxBooks(planId: number): number {
    switch (planId) {
      case 1: return 3;
      case 2: return 7;
      case 3: return 20;
      default: return 3;
    }
  }

  // ================= OPEN MODAL =================
  openConfirmModal(bookId: number) {

    if (!this.hasActiveSubscription) {
      alert("❌ Please subscribe first");
      return;
    }

    const maxBooks = this.getMaxBooks(this.activeSubscription.planId);

    if (this.currentLoans.length >= maxBooks) {
      alert("❌ You have reached your maximum borrow limit.\nReturn a book to borrow again.");
      return;
    }

    this.selectedBookId = bookId;
    this.showModal = true;
    this.isChecked = false;
  }

  // ================= FINAL CONFIRM =================
  finalConfirm() {
    if (!this.isChecked) {
      alert("⚠️ Please accept terms first");
      return;
    }

    const token = localStorage.getItem('token');

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    const payload = {
      bookId: this.selectedBookId,
      checkoutDays: this.getDaysFromPlan(this.activeSubscription.planId),
      notes: "First borrow"
    };

    console.log("PAYLOAD:", payload);

    this.http.post(`${this.baseUrl}/checkout`, payload, { headers }).subscribe({
      next: () => {
        alert("✅ Book issued successfully");

        this.loanService.removeBook(this.selectedBookId!);
        this.loadPendingBooks();
        this.loadCurrentLoans();   // 🔥 refresh loans
        this.closeModal();
      },
      error: (err) => {
        console.error(err);
        alert("❌ Failed to issue book may be you've reached your max book limit");
      }
    });
  }

  // ================= CANCEL =================
  cancelBorrow(bookId: number) {
    this.loanService.removeBook(bookId);
    this.loadPendingBooks();
  }

  // ================= CLOSE MODAL =================
  closeModal() {
    this.showModal = false;
    this.selectedBookId = null;
  }
}