import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-book-loan-front',
  standalone: true,                    // ✅ ADD THIS
  imports: [CommonModule, RouterModule],
  templateUrl: './book-loan-front.html',
  styleUrls: ['./book-loan-front.css']
})
export class BookLoanFrontComponent {


}