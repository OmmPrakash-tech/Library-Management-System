import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-fine',
  templateUrl: './fine.html',
  styleUrls: ['./fine.css']
})
export class FineComponent {

  constructor(private router: Router) {}  // ✅ inside class

  goToFines() {
    this.router.navigate(['/fines']);
  }

}