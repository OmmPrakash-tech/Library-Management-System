import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

declare var Razorpay: any;

@Component({
  selector: 'app-payment',
  templateUrl: './payment.html'
})
export class PaymentComponent implements OnInit {

  plan: any;

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.plan = history.state.plan;

    if (this.plan) {
      this.startPayment();
    }
  }

  startPayment() {

    const body = {
      planId: this.plan.id
    };

    this.http.post<any>('https://library-backend-docker.onrender.com/api/subscriptions/subscribe', body)
      .subscribe(res => {

        console.log("Payment Order:", res);

        const options: any = {
          key: res.key,
          amount: res.amount,
          currency: res.currency,
          name: "Library Subscription",
          description: res.description,
          order_id: res.razorpayOrderId,

          handler: (response: any) => {

            console.log("Payment Success:", response);

            // 🔥 CALL VERIFY API HERE
            this.verifyPayment(response, res.paymentId);

          },

          prefill: {
            name: "User",
            email: "user@email.com"
          },

          theme: {
            color: "#3399cc"
          }
        };

        const rzp = new Razorpay(options);
        rzp.open();

      }, err => {
        console.error("Payment API Error:", err);
      });
  }

  // ================= VERIFY PAYMENT =================
  verifyPayment(response: any, paymentId: number) {

    const verifyData = {
      paymentId: paymentId, // 🔥 from backend response
      razorpayPaymentId: response.razorpay_payment_id,
      razorpayOrderId: response.razorpay_order_id,
      razorpaySignature: response.razorpay_signature
    };

    this.http.post('https://library-backend-docker.onrender.com/api/payments/verify', verifyData)
      .subscribe({
        next: (res) => {
          console.log("VERIFY SUCCESS:", res);

          alert("Payment Verified & Subscription Activated ✅");

          // 🔥 Redirect back
          this.router.navigate(['/subscription']);
        },
        error: (err) => {
          console.error("VERIFY FAILED:", err);
          alert("Payment verification failed ❌");
        }
      });
  }
}