import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-subscription',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './subscribe.html',
  styleUrls: ['./subscribe.css']
})
export class SubscriptionComponent implements OnInit {

  plans: any[] = [];
  loading: boolean = false;
  errorMessage: string = '';

  activeSubscription: any = null;
  noSubscriptionMessage: string = '';
  hasActiveSubscription: boolean = false; // 🔥 MAIN FLAG

  private apiUrl = 'http://localhost:5050/api/subscription-plans';
  private activeApi = 'http://localhost:5050/api/subscriptions/user/active';

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private router: Router
  ) {}

  // ================= INIT =================
  ngOnInit(): void {
    this.getPlans();
    this.getActiveSubscription();
  }

  // ================= GET PLANS =================
  getPlans() {
    if (this.loading) return;

    this.loading = true;
    this.errorMessage = '';

    this.http.get<any[]>(this.apiUrl).subscribe({
      next: (res) => {
        this.plans = res || [];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Failed to load plans';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  refreshPlans() {
    this.getPlans();
  }

  // ================= SUBSCRIBE =================
  subscribe(plan: any) {

    // 🚫 Block if already subscribed
    if (this.hasActiveSubscription) {
      return;
    }

    // ✅ Go to payment
    this.router.navigate(['/payment'], {
      state: { plan: plan }
    });
  }

  // ================= GET ACTIVE =================
  getActiveSubscription() {

    this.activeSubscription = null;
    this.noSubscriptionMessage = '';
    this.hasActiveSubscription = false;

    this.http.get(this.activeApi).subscribe({
      next: (res: any) => {

        console.log("ACTIVE RESPONSE:", res);

        // ❌ No subscription
        if (res.success === false) {
          this.hasActiveSubscription = false;
          this.activeSubscription = null;
          this.noSubscriptionMessage = res.message;
        }

        // ✅ Has subscription
        else if (res && res.planName) {
          this.hasActiveSubscription = true;
          this.activeSubscription = res;
          this.noSubscriptionMessage = '';
        }

        // ❌ fallback
        else {
          this.hasActiveSubscription = false;
          this.activeSubscription = null;
        }

        this.cdr.detectChanges();
      },

      error: () => {
        this.hasActiveSubscription = false;
        this.noSubscriptionMessage = 'Failed to load subscription';
        this.cdr.detectChanges();
      }
    });
  }
}