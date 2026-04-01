import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { SubscriptionControllerComponent } from './subscription-contoll/subscription-contoll';


@Component({
  selector: 'app-subscription-hero',
  templateUrl: './subscription.html',
  styleUrls: ['./subscription.css']
})
export class SubscriptionHeroComponent {

  constructor(private router: Router) {}

 goToSubscription() {
  this.router.navigate(['/subscriptions']);
}

}