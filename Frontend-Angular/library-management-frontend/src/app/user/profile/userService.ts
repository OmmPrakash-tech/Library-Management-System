import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// 🔥 USER MODEL (ADD THIS)
export interface User {
  id: number;
  email: string;
  fullName: string;
  phone?: string;
  role?: string;
  profileImage?: string; // 🔥 IMPORTANT
}

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private baseUrl = 'http://localhost:5050/api/users';

  constructor(private http: HttpClient) {}

  // ================= GET PROFILE =================
  getProfile(): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/profile`);
  }

  // ================= UPDATE PROFILE =================
  updateProfile(id: number, payload: User): Observable<User> {
    return this.http.put<User>(`${this.baseUrl}/update/${id}`, payload);
  }
}