import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BookService {

  private baseUrl = 'https://library-backend-docker.onrender.com/api/books';
  private genreUrl = 'https://library-backend-docker.onrender.com/api/genres'; // ✅ NEW

  constructor(private http: HttpClient) {}

  // 📚 GET ALL BOOKS
  getAllBooks(): Observable<any> {
    return this.http.get(this.baseUrl);
  }

  // 📂 GET ALL GENRES
  getGenres(): Observable<any> {
    return this.http.get(this.genreUrl);
  }

  // 🔍 SEARCH BOOKS (WITH FILTER)
  searchBooks(keyword: string, genreId?: number): Observable<any> {

    const body: any = {
      searchTerm: keyword && keyword.trim() !== '' ? keyword : null, // ✅ FIX
      page: 0,
      size: 10,
      sortBy: 'createdAt',
      sortDirection: 'DESC'
    };

    // ✅ only send genre if selected
    if (genreId !== null && genreId !== undefined) {
      body.genreId = genreId;
    }

    return this.http.post(`${this.baseUrl}/search`, body);
  }
}