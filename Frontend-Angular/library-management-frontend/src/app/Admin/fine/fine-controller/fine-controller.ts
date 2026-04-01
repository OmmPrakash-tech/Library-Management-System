import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpHeaders } from '@angular/common/http';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-fine-controller-page',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './fine-controller.html',
  styleUrls: ['./fine-controller.css']
})
export class FineControllerPageComponent implements OnInit {

  constructor(private http: HttpClient) {}

  // ✅ FIXED FIELD NAMES
  newFine = {
    bookLoanId: '',
    note: '',
    type: '',
    amount: ''
  };

  types = ['OVERDUE', 'DAMAGE', 'LOSS', 'PROCESSING'];

  fines: any[] = [];
  selectedFine: any = null;

  ngOnInit() {
    this.loadFines();
  }

  // 🔹 Auth Header
  getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');

    if (!token) {
      console.error("Token missing");
      return new HttpHeaders();
    }

    return new HttpHeaders({
      Authorization: `Bearer ${token}`
    });
  }

  // 🔹 Load fines
  loadFines() {
    this.http.get<any>('http://localhost:5050/api/fines', {
      headers: this.getAuthHeaders()
    }).subscribe({
      next: (res) => {
        console.log("API Response:", res);
        this.fines = res.content || res; // ✅ correct
      },
      error: (err) => console.error("Load Error:", err)
    });
  }

  // 🔹 Create fine
  createFine() {
    const payload = {
      bookLoanId: Number(this.newFine.bookLoanId), // ✅ FIXED
      type: this.newFine.type,
      amount: Number(this.newFine.amount),
      reason: "Manual fine",
      note: this.newFine.note // ✅ FIXED
    };

    this.http.post('http://localhost:5050/api/fines', payload, {
      headers: this.getAuthHeaders()
    }).subscribe({
      next: () => {
        this.loadFines();

        // ✅ RESET CORRECTLY
        this.newFine = {
          bookLoanId: '',
          note: '',
          type: '',
          amount: ''
        };
      },
      error: (err) => console.error("Create Error:", err)
    });
  }

  // 🔹 View fine
  viewFine(fine: any) {
    this.selectedFine = fine;
  }

  // 🔹 Waive fine
  waiveFine(id: number) {
    const payload = {
      reason: "Admin waived fine"
    };

    this.http.post(`http://localhost:5050/api/fines/${id}/waive`, payload, {
      headers: this.getAuthHeaders()
    }).subscribe({
      next: () => this.loadFines(),
      error: (err) => console.error("Waive Error:", err)
    });
  }
}