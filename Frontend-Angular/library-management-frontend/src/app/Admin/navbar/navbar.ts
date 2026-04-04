import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})
export class Navbar {

  constructor(private router: Router) {}

  signOut() {
    // Clear session / token
    localStorage.clear();

    // Navigate to login page
    this.router.navigate(['/login']);
  }

  goToReviews() {
  this.router.navigate(['/reviews']); // 🔥 your reviews route
}

}