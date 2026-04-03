import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpHeaders } from '@angular/common/http';
import { HttpClient, HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-fine-user',
  standalone: true,
  imports: [CommonModule, HttpClientModule],
  templateUrl: './fine-user.html',
  styleUrls: ['./fine-user.css']
})
export class FineUserComponent implements OnInit {

  fines: any[] = [];
  loading: boolean = false;
  error: string = '';

  private apiUrl = 'http://localhost:5050/api/fines/my';

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef   // ✅ added
  ) {}

  ngOnInit(): void {
    this.getMyFines();
  }

  getMyFines() {
  this.loading = true;

  const token = localStorage.getItem('token'); // or wherever you store it

  const headers = new HttpHeaders({
    Authorization: `Bearer ${token}`
  });

  this.http.get<any[]>(this.apiUrl, { headers }).subscribe({
    next: (res) => {
      this.fines = res;
      this.loading = false;
      this.cdr.detectChanges();
    },
    error: (err) => {
      console.error(err);
      this.error = 'Failed to load fines';
      this.loading = false;
      this.cdr.detectChanges();
    }
  });
}

  getStatusClass(status: string): string {
    switch (status) {
      case 'PAID': return 'paid';
      case 'WAIVED': return 'waived';
      case 'PENDING': return 'pending';
      default: return '';
    }
  }
}