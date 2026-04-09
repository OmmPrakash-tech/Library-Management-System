import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {

  private baseUrl = 'https://library-backend-docker.onrender.com/api';

  constructor(private http: HttpClient) {}

  subscribePlan(data: any) {
    return this.http.post(`${this.baseUrl}/subscriptions/subscribe`, data);
  }

  verifyPayment(data: any) {
    return this.http.post(`${this.baseUrl}/payments/verify`, data);
  }
}