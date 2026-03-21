import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-blog',
  imports: [CommonModule],
  templateUrl: './blog.html',
  styleUrls: ['./blog.css']
})
export class Blog {

  blogs = [

    {
      title: "How Library Automation Improves Efficiency",
      desc: "Discover how modern library software helps manage books, seats, and users easily.",
      image: "/blog1.png"
    },

    {
      title: "Top 5 Features Every Library System Should Have",
      desc: "From seat booking to analytics dashboards, explore the must-have features.",
      image: "/blog2.png"
    },

    {
      title: "Why Digital Libraries Are the Future",
      desc: "Technology is transforming traditional libraries into smart learning hubs.",
      image: "/blog3.png"
    },

    {
      title: "How to Manage Library Seats Efficiently",
      desc: "Seat management helps libraries improve productivity and space utilization.",
      image: "/blog4.png"
    }

  ];

}