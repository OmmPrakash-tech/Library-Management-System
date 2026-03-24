import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-genre',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './genre.html',
  styleUrls: ['./genre.css']
})
export class GenreComponent {

  constructor(private router: Router) {}

  controlGenre() {
    this.router.navigate(['/genre-control']);
  }

}