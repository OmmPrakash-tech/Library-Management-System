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

  signOut(){

    // remove stored token
    localStorage.removeItem("token");

    // optional: clear all storage
    localStorage.clear();

    // redirect to login page
    this.router.navigate(['/login']);
  }

}