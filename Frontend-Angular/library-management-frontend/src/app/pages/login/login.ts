import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class Login implements OnInit {

constructor(private http: HttpClient, private router: Router) {}

  /* LOGIN DATA */

  loginData = {
    email: '',
    password: ''
  };

  /* IMAGE SLIDER */

  images = [
    '/signin1.png',
    '/signin2.png',
    '/signin3.png'
  ];

  currentIndex = 0;

  isFading = false;

  ngOnInit() {

    setInterval(() => {

      /* start fade */

      this.isFading = true;

      setTimeout(() => {

        this.currentIndex++;

        if (this.currentIndex >= this.images.length) {
          this.currentIndex = 0;
        }

        /* stop fade */

        this.isFading = false;

      }, 500);

    }, 3000);

  }

  /* LOGIN FUNCTION */

login(form: NgForm) {

  if (form.invalid) {
    alert("Please enter email and password");
    return;
  }

  this.http.post("http://localhost:5050/api/auth/login", this.loginData)
    .subscribe({

      next: (res: any) => {

        console.log(res);

        /* store token */

        localStorage.setItem("token", res.jwt);

        /* store role */

        localStorage.setItem("role", res.user.role);

        /* ROLE BASED NAVIGATION */

        if(res.user.role === "ROLE_ADMIN"){

          this.router.navigate(['/admin/home']);

        }

        else if(res.user.role === "ROLE_USER"){

          this.router.navigate(['/user/home']);

        }

      },

      error: (err) => {

        alert("Invalid email or password");

        console.error(err);

      }

    });

}

}