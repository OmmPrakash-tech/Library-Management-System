import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpClientModule, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-subscription-controller',
  standalone: true,
  imports: [CommonModule, HttpClientModule, FormsModule],
  templateUrl: './subscription-contoll.html',
  styleUrls: ['./subscription-contoll.css']
})
export class SubscriptionControllerComponent implements OnInit {

  subscriptions: any[] = [];
  filterType: string = 'ALL';

  // ✅ FIXED TYPES (number instead of string)
  subscribeData = {
    userId: 0,
    planId: 0
  };

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadSubscriptions();
  }

  // ✅ TOKEN
  getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');

    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  // ✅ LOAD SUBSCRIPTIONS
  loadSubscriptions() {
    let url = '';

    if (this.filterType === 'ALL') {
      url = 'https://library-backend-docker.onrender.com/api/subscriptions/all';
    } else if (this.filterType === 'ACTIVE') {
      url = 'https://library-backend-docker.onrender.com/api/subscriptions/active';
    } else {
      url = 'https://library-backend-docker.onrender.com/api/subscriptions/admin';
    }

    this.http.get<any>(url, { headers: this.getAuthHeaders() })
      .subscribe({
        next: (res) => {
          console.log("API:", url);
          console.log("DATA:", res);

          if (res?.content) {
            this.subscriptions = res.content;
          } else if (Array.isArray(res)) {
            this.subscriptions = res;
          } else {
            this.subscriptions = [];
          }

          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error fetching subscriptions', err);
          alert('Failed to load subscriptions');
        }
      });
  }

  // ✅ FILTER SWITCH
  setFilter(type: string) {
    this.filterType = type;
    this.loadSubscriptions();
  }

  // ✅ SUBSCRIBE USER
  subscribe() {
    if (!this.subscribeData.userId || !this.subscribeData.planId) {
      alert('Please enter User ID and Plan ID');
      return;
    }

    this.http.post(
      'https://library-backend-docker.onrender.com/api/subscriptions/subscribenew',
      this.subscribeData,
      { headers: this.getAuthHeaders() }
    ).subscribe({
      next: () => {
        alert('Subscribed successfully ✅');
        this.subscribeData = { userId: 0, planId: 0 }; // reset
        this.loadSubscriptions();
      },
      error: (err) => {
        console.error(err);
        alert('Subscription failed ❌');
      }
    });
  }

  // ✅ ACTIVATE SUBSCRIPTION
  activate(id: number) {
    const paymentId = prompt("Enter Payment ID");

    if (!paymentId) {
      alert('Payment ID is required');
      return;
    }

    this.http.post(
      'https://library-backend-docker.onrender.com/api/subscriptions/activate',
      null,
      {
        params: {
          subscriptionId: id,
          paymentId: Number(paymentId)
        },
        headers: this.getAuthHeaders()
      }
    ).subscribe({
      next: () => {
        alert('Activated successfully 🚀');
        this.loadSubscriptions();
      },
      error: (err) => {
        console.error(err);
        alert('Activation failed ❌');
      }
    });
  }

  // ✅ CANCEL SUBSCRIPTION
cancel(id: number) {
  const reason = prompt("Enter reason for cancellation");

  let params: any = {};

  if (reason && reason.trim() !== '') {
    params.reason = reason;
  }

  this.http.patch(
    `https://library-backend-docker.onrender.com/api/subscriptions/${id}/cancel`,
    {},
    {
      params: params,
      headers: this.getAuthHeaders()
    }
  ).subscribe({
    next: () => {
      alert('Cancelled successfully ❌');
      this.loadSubscriptions();
    },
    error: (err) => {
      console.error(err);
      alert('Cancellation failed ❌');
    }
  });
}

  // ✅ DEACTIVATE EXPIRED
  deactivateExpired() {
    this.http.post(
      'https://library-backend-docker.onrender.com/api/subscriptions/admin/deactivate-expired',
      {},
      { headers: this.getAuthHeaders() }
    ).subscribe({
      next: () => {
        alert('Expired subscriptions deactivated ⚠️');
        this.loadSubscriptions();
      },
      error: (err) => {
        console.error(err);
        alert('Operation failed ❌');
      }
    });
  }

  // ✅ NAVIGATION
  goToPlans() {
    this.router.navigate(['/subscription-plans']);
  }
}