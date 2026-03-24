import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-book',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './book.html',
  styleUrls: ['./book.css']
})
export class Book {

   constructor(private router: Router) {}

  controlBooks() {
    this.router.navigate(['/book-control']);
  }

}