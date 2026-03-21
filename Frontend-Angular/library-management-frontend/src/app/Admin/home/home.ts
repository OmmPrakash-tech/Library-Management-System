import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class Home {

  stats = [
    { title: 'Total Books', value: 1240, icon: '📚' },
    { title: 'Registered Users', value: 320, icon: '👥' },
    { title: 'Books Issued', value: 210, icon: '📖' },
    { title: 'Active Subscriptions', value: 95, icon: '💳' }
  ];

}