import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { BookControlService } from '../../book-control.service';

@Component({
  selector: 'app-view-book',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './view-book.html',
  styleUrls: ['./view-book.css']
})
export class ViewBook implements OnInit {

  book: any = null;
  bookId: number = 0; // ✅ store id for reuse

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private bookService: BookControlService
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');

    console.log("ID:", id);

    if (id) {
      this.bookId = +id; // ✅ store once
      this.loadBook();   // ✅ reusable method
    }
  }

  // 🔥 LOAD BOOK (used for init + refresh)
  loadBook() {
    this.bookService.getBookById(this.bookId).subscribe({
      next: (res: any) => {
        console.log("API RESPONSE:", res);
        this.book = res;
      },
      error: (err: any) => { // ✅ fixed type error
        console.error("ERROR:", err);
      }
    });
  }

  // 🔄 REFRESH BUTTON
  refreshBook() {
    this.loadBook(); // ✅ reuse same logic
  }

  // 🔙 BACK
  goBack() {
    this.router.navigate(['/book-control']);
  }

  // 🗑 Soft delete
deleteBook() {
  if (!confirm("Are you sure to soft delete this book?")) return;

  this.bookService.softDeleteBook(this.bookId).subscribe({
    next: () => {
      alert("Book soft deleted!");
      this.router.navigate(['/book-control']); // 🔥 go back to list
    },
    error: (err: any) => {
      console.error(err);
    }
  });
}

// ❌ Hard delete
hardDeleteBook() {
  if (!confirm("Permanent delete?")) return;

  this.bookService.hardDeleteBook(this.bookId).subscribe({
    next: () => {
      alert("Book deleted permanently!");
      this.router.navigate(['/book-control']);
    },
    error: (err: any) => {
      console.error(err);
    }
  });
}
}