import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Navbar } from '../navbar/navbar';
import { Hero } from '../hero/hero';
import { Userwork } from '../userwork/userwork';
import { Performance } from '../performance/performance';
import { Profile } from '../profile/profile';
import { Footer } from '../footer/footer';
import { Router, RouterModule } from '@angular/router';  // ✅ ADD Router

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule, Navbar, Hero, Userwork, Performance, Profile, Footer],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class Home {

  constructor(private router: Router) {}   // ✅ Inject Router

  goToBook() {
    this.router.navigate(['/book']);       // ✅ Navigation logic
  }

}