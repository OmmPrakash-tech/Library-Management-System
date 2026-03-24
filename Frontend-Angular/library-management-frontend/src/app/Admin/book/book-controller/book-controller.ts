import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-book-controller',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './book-controller.html',
  styleUrls: ['./book-controller.css']
})
export class BookController {

  onAddBook() {
    console.log('Add Book Clicked');
  }

  onViewBooks() {
    console.log('View Books Clicked');
  }

  onManageBooks() {
    console.log('Manage Books Clicked');
  }

}