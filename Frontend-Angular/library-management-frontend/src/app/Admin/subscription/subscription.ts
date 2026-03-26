import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-subscription-hero',
  templateUrl: './subscription.html',
  styleUrls: ['./subscription.css']
})
export class SubscriptionHeroComponent {

  constructor(private router: Router) {}

  goToSubscription() {
    // 👉 tomorrow you can implement logic
    this.router.navigate(['/subscription']);
  }

}