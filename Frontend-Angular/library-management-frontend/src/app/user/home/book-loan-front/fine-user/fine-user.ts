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

  private apiUrl = 'https://library-backend-docker.onrender.com/api/fines/my';

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
    case 'PARTIALLY_PAID': return 'partially_paid';
    default: return '';
  }
}

  getAuthHeaders(): HttpHeaders {
  const token = localStorage.getItem('token');

  return new HttpHeaders({
    Authorization: `Bearer ${token}`
  });
}

  payFine(fineId: number) {

  this.http.post<any>(
    `https://library-backend-docker.onrender.com/api/fines/${fineId}/pay`,
    {},
    { headers: this.getAuthHeaders() }
  ).subscribe({
    next: (res) => {

      const options: any = {
        key: res.key, // from backend
        amount: res.amount,
        currency: "INR",
        name: "Library System",
        description: "Fine Payment",
        order_id: res.orderId,

        handler: (response: any) => {

          // ✅ Confirm payment
          this.confirmPayment(
            fineId,
            res.amount,
            response.razorpay_payment_id
          );
        },

        theme: {
          color: "#38bdf8"
        }
      };

      const rzp = new (window as any).Razorpay(options);
      rzp.open();
    },

    error: (err) => console.error("Payment Error:", err)
  });
}

confirmPayment(fineId: number, amount: number, transactionId: string) {

  const amountInRupees = amount / 100; // ✅ FIX

  this.http.post(
    `https://library-backend-docker.onrender.com/api/fines/${fineId}/confirm-payment`,
    null,
    {
      params: {
        amount: amountInRupees,
        transactionId: transactionId
      },
      headers: this.getAuthHeaders()
    }
  ).subscribe({
    next: (res) => {
      console.log("Payment confirmed", res);
      this.getMyFines();
    },
    error: (err) => console.error("Confirm Error:", err)
  });
}
}