import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BookControlService {

  private baseUrl = 'http://library-backend-docker.onrender.com/api/books';

  constructor(private http: HttpClient) {}

  // 📌 Create a single book
  createBook(data: any): Observable<any> {
    return this.http.post(this.baseUrl, data);
  }

  // 📌 Bulk create books
  createBooksBulk(data: any[]): Observable<any> {
    return this.http.post(`${this.baseUrl}/bulk`, data);
  }

  // 📌 Get all books
  getAllBooks(): Observable<any> {
    return this.http.get(this.baseUrl);
  }

  // 📌 Get book by ID
  getBookById(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/${id}`);
  }

  // 📌 Get book by ISBN
  getBookByIsbn(isbn: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/isbn/${isbn}`);
  }

  // 📌 Update book
  updateBook(id: number, data: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}`, data);
  }

  // 📌 Soft delete
  softDeleteBook(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  // 📌 Hard delete
  hardDeleteBook(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}/permanent`);
  }

  // 📌 Advanced search
  searchBooks(criteria: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/search`, criteria);
  }

  // 📌 Book statistics
  getBookStats(): Observable<any> {
    return this.http.get(`${this.baseUrl}/stats`);
  }
}