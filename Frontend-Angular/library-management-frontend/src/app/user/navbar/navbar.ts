import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.css']
})
export class Navbar {

  constructor(private router: Router){}

signOut(): void {
  const confirmLogout = confirm("Are you sure you want to sign out?");
  if (!confirmLogout) return;

  localStorage.clear();
  this.router.navigate(['/login']);
}

goToMyReviews(): void {
  this.router.navigate(['/my-reviews']);
}
}