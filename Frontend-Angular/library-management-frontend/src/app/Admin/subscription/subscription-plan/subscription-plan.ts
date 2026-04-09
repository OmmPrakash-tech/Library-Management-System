import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';

@Component({
  selector: 'app-subscription-plan',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './subscription-plan.html',
  styleUrls: ['./subscription-plan.css']
})
export class SubscriptionPlanComponent implements OnInit {

  plans: any[] = [];
  isEditMode = false;
  selectedId: number | null = null;

  form: any = {
    planCode: '',
    name: '',
    description: '',
    durationDays: 0,
    price: 0,
    currency: 'INR',
    maxBooksAllowed: 0,
    maxDaysPerBook: 0,
    displayOrder: 0,
    isActive: true,
    isFeatured: false,
    badgeText: '',
    adminNotes: ''
  };

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadPlans();
  }

  getHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  // ✅ LOAD PLANS
  loadPlans() {
    this.http.get<any>('https://library-backend-docker.onrender.com/api/subscription-plans', {
      headers: this.getHeaders()
    }).subscribe({
      next: (res) => {
        this.plans = Array.isArray(res) ? res : res.content;
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  // ✅ CREATE / UPDATE
  savePlan() {
    const headers = this.getHeaders();

    if (this.isEditMode && this.selectedId) {
      this.http.put(
        `https://library-backend-docker.onrender.com/api/subscription-plans/admin/${this.selectedId}`,
        this.form,
        { headers }
      ).subscribe(() => {
        this.resetForm();
        this.loadPlans();
      });
    } else {
      this.http.post(
        'https://library-backend-docker.onrender.com/api/subscription-plans/admin',
        this.form,
        { headers }
      ).subscribe(() => {
        this.resetForm();
        this.loadPlans();
      });
    }
  }

  // ✅ EDIT
  editPlan(plan: any) {
    this.form = { ...plan };
    this.selectedId = plan.id;
    this.isEditMode = true;
  }

  // ✅ DELETE
  deletePlan(id: number) {
    if (!confirm('Delete this plan?')) return;

    this.http.delete(
      `https://library-backend-docker.onrender.com/api/subscription-plans/admin/${id}`,
      { headers: this.getHeaders() }
    ).subscribe(() => {
      this.loadPlans();
    });
  }

  // ✅ RESET
  resetForm() {
    this.isEditMode = false;
    this.selectedId = null;
    this.form = {
        id: null,
      planCode: '',
      name: '',
      description: '',
      durationDays: 0,
      price: 0,
      currency: 'INR',
      maxBooksAllowed: 0,
      maxDaysPerBook: 0,
      displayOrder: 0,
      isActive: true,
      isFeatured: false,
      badgeText: '',
      adminNotes: ''
    };
  }
}