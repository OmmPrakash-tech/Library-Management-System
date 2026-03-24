import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { GenreService } from '../genre.service';

@Component({
  selector: 'app-genre-control',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './genre-control.html',
  styleUrls: ['./genre-control.css']
})
export class GenreControl implements OnInit {

  genres: any[] = [];
  filteredGenres: any[] = [];
  genreTree: any[] = [];

  searchText: string = '';

  editMode = false;
  selectedId: number | null = null;

  newGenre = {
    name: '',
    code: ''
  };

  constructor(private genreService: GenreService) {}

  ngOnInit() {
    this.loadGenres();
    this.loadTree();
  }

  // LOAD GENRES
  loadGenres() {
    this.genreService.getAllGenres().subscribe(res => {
      this.genres = res;
      this.filteredGenres = res;
    });
  }

  // LOAD TREE
  loadTree() {
    this.genreService.getGenreTree().subscribe(res => {
      this.genreTree = res;
    });
  }

  // ADD / UPDATE
onAddGenre() {

  if (!this.newGenre.name?.trim() || !this.newGenre.code?.trim()) {
    alert("Name & Code required!");
    return;
  }

  this.genreService.createGenre(this.newGenre).subscribe({
    next: () => {
      this.resetForm();
      this.loadGenres();
    },
    error: (err) => {
      console.log(err.error);
    }
  });
}

  // EDIT
  editGenre(g: any) {
    this.newGenre = { name: g.name, code: g.code };
    this.selectedId = g.id;
    this.editMode = true;
  }

  // DELETE
  deleteGenre(id: number) {
    this.genreService.softDeleteGenre(id).subscribe(() => {
      this.loadGenres();
    });
  }

  // HARD DELETE
  hardDelete(id: number) {
    this.genreService.hardDeleteGenre(id).subscribe(() => {
      this.loadGenres();
    });
  }

  // SEARCH
  search() {
    this.filteredGenres = this.genres.filter(g =>
      g.name.toLowerCase().includes(this.searchText.toLowerCase())
    );
  }

  // RESET
  resetForm() {
    this.newGenre = { name: '', code: '' };
    this.editMode = false;
    this.selectedId = null;
  }
}