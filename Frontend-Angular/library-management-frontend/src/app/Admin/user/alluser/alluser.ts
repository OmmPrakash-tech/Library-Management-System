import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Navbar } from '../../navbar/navbar';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-all-user',
  standalone: true,
  imports: [CommonModule, Navbar],
  templateUrl: './alluser.html',
  styleUrls: ['./alluser.css']
})
export class AllUser {

  users: any[] = [];
  loading: boolean = false;

  constructor(private router: Router, private http: HttpClient) {}

  // 🔥 Fetch users from backend
  showUsers() {
    this.loading = true;

    this.http.get<any[]>('http://localhost:5050/api/users/list')
      .subscribe({
        next: (data) => {
          console.log("API Response:", data);
          this.users = data;
          this.loading = false;
        },
        error: (err) => {
          console.error("Error:", err);
          this.loading = false;
        }
      });
  }

  // 🔹 Delete user (UI only for now)
deleteUser(id: number) {

  if (!confirm('Are you sure?')) return;

  this.http.delete(`http://localhost:5050/api/users/delete/${id}`, {
    responseType: 'text' // ✅ FIX HERE
  }).subscribe({
    next: () => {
      this.users = this.users.filter(user => user.id !== id);
      alert('User deleted ✅');
    },
    error: (err) => {
      console.error("Delete failed:", err);
      alert('Delete failed ❌');
    }
  });
}
  // 🔹 Edit user
editUser(id: number) {
  console.log("Navigating to edit user:", id);
  this.router.navigate(['/admin/edit-user', id]);
}

  // 🔹 Add user
  addUser() {
    this.router.navigate(['/admin/add-user']);
  }
}