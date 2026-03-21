import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BookService {

  private baseUrl = 'http://localhost:5050/api/books';

  constructor(private http: HttpClient) {}

  // GET all books
  getAllBooks(): Observable<any> {
    return this.http.get(this.baseUrl);
  }

  // SEARCH books
  searchBooks(keyword: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/search`, { keyword });
  }
}