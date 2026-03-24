import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-manage-books',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './manage-books.html',
  styleUrls: ['./manage-books.css']
})
export class ManageBooks {

  // 🔥 Dummy data (frontend only)
  totalBooks = 120;
  activeBooks = 95;
  deletedBooks = 25;

  // Graph data (simple)
  stats = [
    { label: 'Total', value: 120 },
    { label: 'Active', value: 95 },
    { label: 'Deleted', value: 25 }
  ];

}