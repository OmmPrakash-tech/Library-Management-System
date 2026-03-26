import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-userwork',
  standalone: true,
  imports: [RouterModule],
  templateUrl: './userwork.html',
  styleUrls: ['./userwork.css']
})
export class Userwork {

  constructor(private router: Router) {}

  goToBook() {
    this.router.navigate(['/book']);
  }

  goToSubscribe() {
  this.router.navigate(['/subscription']);
}

goToWishlist() {
  this.router.navigate(['/wishlist']);
}

}