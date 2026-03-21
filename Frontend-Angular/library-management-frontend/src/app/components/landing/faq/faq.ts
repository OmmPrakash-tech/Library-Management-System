import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-faq',
  imports: [CommonModule],   // enables *ngFor and *ngIf
  templateUrl: './faq.html',
  styleUrls: ['./faq.css']
})
export class Faq {

  faqs = [
    {
      question: "What is Libraro and how does it work?",
      answer: "Libraro is a cloud-based library management system that helps manage books, users, seat bookings and reports.",
      open: true
    },
    {
      question: "Who can use Libraro?",
      answer: "Schools, colleges, reading rooms and public libraries can use Libraro.",
      open: false
    },
    {
      question: "Is Libraro compatible with different devices?",
      answer: "Yes. Libraro works on desktops, tablets and mobile devices.",
      open: false
    },
    {
      question: "Can I import my existing library data?",
      answer: "Yes, Libraro supports importing your existing records.",
      open: false
    },
    {
      question: "Is my library data secure?",
      answer: "Yes. All data is stored securely with proper access control.",
      open: false
    },
    {
      question: "How do I get support if I face issues?",
      answer: "You can contact our support team through email or phone.",
      open: false
    }
  ];

  toggleFAQ(index:number){
    this.faqs[index].open = !this.faqs[index].open;
  }

}