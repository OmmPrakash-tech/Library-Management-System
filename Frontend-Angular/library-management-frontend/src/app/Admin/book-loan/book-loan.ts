import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-book-loan',
  standalone: true,
  templateUrl: './book-loan.html',
  styleUrls: ['./book-loan.css']
})
export class BookLoanComponent {

  constructor(private router: Router) {}

  goToManageLoans() {
    this.router.navigate(['/book-loan-control']);
  }
}