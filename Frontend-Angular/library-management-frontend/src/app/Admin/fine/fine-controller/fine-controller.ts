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
  type: ''
};

types = ['OVERDUE', 'DAMAGE', 'LOSS'];

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
    this.http.get<any>('http://library-backend-docker.onrender.com/api/fines', {
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
  if (!this.newFine.bookLoanId || !this.newFine.type) {
    alert("Please fill all required fields");
    return;
  }

  const payload = {
    bookLoanId: Number(this.newFine.bookLoanId),
    type: this.newFine.type,
    note: this.newFine.note
  };

  this.http.post('http://library-backend-docker.onrender.com/api/fines', payload, {
    headers: this.getAuthHeaders()
  }).subscribe({
    next: () => {
      this.loadFines();

      this.newFine = {
        bookLoanId: '',
        note: '',
        type: ''
      };
    },
    error: (err) => console.error("Create Error:", err)
  });
}

  // 🔹 View fine
  viewFine(fine: any) {
    this.selectedFine = fine;
  }

  getTotalFine(): number {
  return this.fines.reduce(
    (sum, f) => sum + (f.amountOutstanding || 0),
    0
  );
}

  // 🔹 Waive fine
  waiveFine(id: number) {
    const payload = {
      reason: "Admin waived fine"
    };

    this.http.post(`http://library-backend-docker.onrender.com/api/fines/${id}/waive`, payload, {
      headers: this.getAuthHeaders()
    }).subscribe({
      next: () => this.loadFines(),
      error: (err) => console.error("Waive Error:", err)
    });
  }
}