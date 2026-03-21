import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-feature',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './feature.html',
  styleUrls: ['./feature.css']
})
export class Feature {

  constructor(private router: Router){}

  features = [

    {
      title: "Interactive & Insightful Dashboard",
      desc: "Get a complete overview of your library with an intuitive dashboard.",
      icon: "📊",
      route: "/feature/dashboard"
    },

    {
      title: "Seamless & Intuitive User Interface",
      desc: "Our platform is designed for effortless user experience.",
      icon: "🧭",
      route: "/feature/ui"
    },

    {
      title: "One-Click Data Import",
      desc: "Effortlessly migrate your existing data into our system.",
      icon: "☁️",
      route: "/feature/data-import"
    },

    {
      title: "Smart Seat Management",
      desc: "Easily track expired and extended seats.",
      icon: "💺",
      route: "/feature/seat-management"
    },

    {
      title: "End-to-End Encryption & Data Security",
      desc: "Only the library owner has access to sensitive information.",
      icon: "🔒",
      route: "/feature/security"
    },

    {
      title: "Attendance & ID Card Management",
      desc: "Track attendance seamlessly and manage ID cards.",
      icon: "🪪",
      route: "/feature/attendance"
    },

    {
      title: "Comprehensive Reports",
      desc: "Generate detailed reports in seconds.",
      icon: "📈",
      route: "/feature/reports"
    },

    {
      title: "Free Directory Listing",
      desc: "Boost your library visibility with a free listing.",
      icon: "📚",
      route: "/feature/directory"
    }

  ];

  /* CARD CLICK */

  openFeature(route:string){
    this.router.navigate([route]);
  }

  /* CTA BUTTON */

  goToDemo(){

    const demoSection = document.getElementById('demo');

    if(demoSection){

      demoSection.scrollIntoView({
        behavior: 'smooth'
      });

    }

  }

}