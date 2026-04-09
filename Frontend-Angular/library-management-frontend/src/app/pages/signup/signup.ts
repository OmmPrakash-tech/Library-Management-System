import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, FormsModule,RouterModule],
  templateUrl: './signup.html',
  styleUrls: ['./signup.css']
})
export class Signup implements OnInit {

  constructor(private http: HttpClient) {}

  images = [
    '/signup1.png',
    '/signup2.png',
    '/signup3.png',
    '/signup4.png',
    '/signup5.png'
  ];

  currentIndex = 0;

  // user object
  user = {
    fullName: '',
    email: '',
    phone: '',
    password: ''
  };

  // checkbox state
  termsAccepted = false;

  ngOnInit() {

    setInterval(() => {

      this.currentIndex++;

      if (this.currentIndex >= this.images.length) {
        this.currentIndex = 0;
      }

    }, 5000);

  }

  signup(form: NgForm) {

    // check form validation
    if (form.invalid) {
      alert("Please fill all required fields correctly.");
      return;
    }

    // check checkbox
    if (!this.termsAccepted) {
      alert("Please accept the Terms and Conditions.");
      return;
    }

    // send request
    this.http.post("https://library-backend-docker.onrender.com/api/auth/signup", this.user)
      .subscribe({

        next: (res) => {
          alert("Signup Successful!");
          console.log(res);

          // reset form after success
          form.resetForm();
          this.termsAccepted = false;
        },

        error: (err) => {
          alert("Signup Failed!");
          console.error(err);
        }

      });

  }

}