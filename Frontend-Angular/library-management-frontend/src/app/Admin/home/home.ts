import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Navbar } from '../navbar/navbar';
import { Hero } from '../hero/hero';
import { User } from '../user/user';
import { Book } from '../book/book';
import { GenreComponent } from '../genre/genre';


@Component({
  selector: 'app-admin-home',
  standalone: true,
  imports: [CommonModule, Navbar, Hero, User, Book, GenreComponent],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class Home {

  adminName: string = 'Admin';

}