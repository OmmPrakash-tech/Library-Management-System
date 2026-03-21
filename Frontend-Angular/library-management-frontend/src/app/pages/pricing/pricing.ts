import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-pricing',
  imports: [CommonModule],
  templateUrl: './pricing.html',
  styleUrls: ['./pricing.css']
})
export class Pricing {

  showPlans = false;

  togglePlans(){
    this.showPlans = !this.showPlans;
  }

}