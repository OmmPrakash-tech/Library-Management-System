import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GenreService {

  private baseUrl = 'http://localhost:5050/api/genres';

  constructor(private http: HttpClient) {}

  // Create Genre
  createGenre(data: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/create`, data);
  }

  // Get All Genres
  getAllGenres(): Observable<any> {
    return this.http.get(this.baseUrl);
  }

  // Get Genre By ID
  getGenreById(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/${id}`);
  }

  // Get Genre By Code
  getGenreByCode(code: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/code/${code}`);
  }

  // Update Genre
  updateGenre(id: number, data: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}`, data);
  }

  // Soft Delete
  softDeleteGenre(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  // Hard Delete
  hardDeleteGenre(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}/hard`);
  }

  // Top Level Genres
  getTopGenres(): Observable<any> {
    return this.http.get(`${this.baseUrl}/top-level`);
  }

  // Sub Genres
  getSubGenres(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/${id}/sub-genres`);
  }

  // Genre Tree
  getGenreTree(): Observable<any> {
    return this.http.get(`${this.baseUrl}/tree`);
  }

  // Count Active Genres
  countGenres(): Observable<any> {
    return this.http.get(`${this.baseUrl}/count`);
  }

  // Count Books in Genre
  countBooksInGenre(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/${id}/book-count`);
  }

}