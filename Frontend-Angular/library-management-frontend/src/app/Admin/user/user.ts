import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './user.html',
  styleUrls: ['./user.css']
})
export class User {

  constructor(private router: Router) {}

  controlUsers() {
    this.router.navigate(['/admin/alluser']); // ✅ route path
  }
}