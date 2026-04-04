import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { BookControlService } from '../../book-control.service';

@Component({
  selector: 'app-view-book',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './view-book.html',
  styleUrls: ['./view-book.css'],
  changeDetection: ChangeDetectionStrategy.OnPush // 🔥 ENABLE ONPUSH
})
export class ViewBook implements OnInit {

  book: any = null;
  bookId: number = 0;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private bookService: BookControlService,
    private cdr: ChangeDetectorRef // 🔥 ADD THIS
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');

    console.log("ID:", id);

    if (id) {
      this.bookId = +id;
      this.loadBook();
    }
  }

  // 🔥 LOAD BOOK
  loadBook() {
    this.bookService.getBookById(this.bookId).subscribe({
      next: (res: any) => {
        console.log("API RESPONSE:", res);
        this.book = res;

        this.cdr.markForCheck(); // 🔥 IMPORTANT
      },
      error: (err: any) => {
        console.error("ERROR:", err);
      }
    });
  }

  // 🔄 REFRESH
  refreshBook() {
    this.loadBook();
  }

  // 🔙 BACK
  goBack() {
    this.router.navigate(['/book-control']);
  }

  // ❌ HARD DELETE
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