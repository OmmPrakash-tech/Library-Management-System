import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-hero',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './hero.html',
  styleUrls: ['./hero.css']
})


export class Hero {

  constructor(private router: Router) {}

  goToLibrary() {
  this.router.navigate(['/library-info']);
}
}