import { Component, OnInit, AfterViewInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Chart } from 'chart.js/auto';

@Component({
  selector: 'app-performance',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './performance.html',
  styleUrls: ['./performance.css']
})
export class Performance implements OnInit, AfterViewInit {

  chart: any;
  stats: any = {};
  libraryStats: any = {};
  viewReady = false;

  private apiUrl = 'http://library-backend-docker.onrender.com/api/book-loans/my';
  private statsApi = 'http://library-backend-docker.onrender.com/api/books/stats';

  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.fetchUserData();
    this.fetchLibraryStats();
  }

  ngAfterViewInit() {
    this.viewReady = true;
  }

  fetchUserData() {
    this.http.get<any>(this.apiUrl).subscribe({
      next: (res) => {
        const loans = res?.content || [];
        this.stats = this.calculateStats(loans);

        this.cdr.detectChanges();
        this.renderChart();
      }
    });
  }

  fetchLibraryStats() {
    this.http.get<any>(this.statsApi).subscribe({
      next: (res) => {
        this.libraryStats = res || {};
        this.cdr.detectChanges();
      }
    });
  }

  renderChart() {
    setTimeout(() => {
      if (!this.viewReady) return;

      this.loadBarChart(
        this.stats.totalBooks || 0,
        this.stats.readBooks || 0
      );
    }, 200);
  }

  getUniqueBooks(loans: any[]) {
    const map = new Map();
    loans.forEach(l => map.set(l.bookId, l));
    return Array.from(map.values());
  }

  calculateStats(loans: any[]) {
    const unique = this.getUniqueBooks(loans);

    const totalBooks = unique.length;
    const readBooks = unique.filter(b => b.status === 'RETURNED').length;
    const currentBooks = unique.filter(b => b.status === 'CHECKED_OUT').length;

    const progress = totalBooks > 0
      ? Math.round((readBooks / totalBooks) * 100)
      : 0;

    return { totalBooks, readBooks, currentBooks, progress };
  }

  loadBarChart(total: number, read: number) {

    if (this.chart) this.chart.destroy();

    const canvas = document.getElementById('barChart') as HTMLCanvasElement;
    if (!canvas) return;

    this.chart = new Chart(canvas, {
      type: 'bar',
      data: {
        labels: ['Total Books', 'Books Read'],
        datasets: [{
          label: 'Reading Activity',
          data: [total, read],
          backgroundColor: ['#00e5ff', '#00c853'],
          borderRadius: 12,
          barThickness: 90
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false, // 🔥 allows height control
        plugins: {
          legend: {
            labels: {
              color: 'white',
              font: { size: 16 }
            }
          }
        },
        scales: {
          x: {
            ticks: { color: 'white', font: { size: 14 } },
            grid: { color: 'rgba(255,255,255,0.1)' }
          },
          y: {
            ticks: { color: 'white', font: { size: 14 } },
            grid: { color: 'rgba(255,255,255,0.1)' }
          }
        }
      }
    });
  }
}