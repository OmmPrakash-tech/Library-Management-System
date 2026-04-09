import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-edit-user',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './edituser.html',
  styleUrls: ['./edituser.css']
})
export class EditUser implements OnInit {

  userId!: number;

  user = {
    fullName: '',
    email: '',
    phone: '',
    password: '',
    role: ''
  };

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadUser();
  }

  // 🔹 Load single user
  loadUser() {
    this.http.get<any>(`http://library-backend-docker.onrender.com/api/users/${this.userId}`)
      .subscribe(data => {
        this.user = {
          fullName: data.fullName,
          email: data.email,
          phone: data.phone,
          password: '',
          role: data.role
        };
      });
  }

  // 🔥 Update user
  updateUser() {
    this.http.put(`http://library-backend-docker.onrender.com/api/users/update/${this.userId}`, this.user)
      .subscribe({
        next: () => {
          alert('User updated successfully ✅');
          this.router.navigate(['/admin/alluser']);
        },
        error: (err) => {
          console.error(err);
          alert('Update failed ❌');
        }
      });
  }
}