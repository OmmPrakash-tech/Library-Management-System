import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-pricing',
  imports: [CommonModule],
  templateUrl: './pricing.html',
  styleUrls: ['./pricing.css']
})
export class Pricing {

  plans = [
    {
      name: "Basic Plan",
      price: "₹49",
      features: [
        "Seat Booking",
        "Book seat without seat no",
        "Direct Seat booking with Seat Map",
        "Swap Seat",
        "Seat Activity Management"
      ]
    },
    {
      name: "Standard Plan",
      price: "₹99",
      features: [
        "Seat Booking",
        "Seat Map Management",
        "User Management",
        "Reports Dashboard",
        "Attendance Tracking"
      ]
    },
    {
      name: "Premium Plan",
      price: "₹199",
      features: [
        "All Standard Features",
        "Advanced Reports",
        "Analytics Dashboard",
        "Priority Support",
        "Unlimited Users"
      ]
    }
  ];

}