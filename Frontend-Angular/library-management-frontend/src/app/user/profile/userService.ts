import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private baseUrl = 'http://localhost:5050/api/users';

  constructor(private http: HttpClient) {}

  // ✅ GET profile
  getProfile() {
    return this.http.get(`${this.baseUrl}/profile`);
  }

  // ✅ UPDATE profile
  updateProfile(id: number, payload: any) {
    return this.http.put(`${this.baseUrl}/update/${id}`, payload);
  }
}