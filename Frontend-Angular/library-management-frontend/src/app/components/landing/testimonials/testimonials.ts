import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-testimonials',
  imports: [CommonModule],
  templateUrl: './testimonials.html',
  styleUrls: ['./testimonials.css']
})
export class Testimonials {

  currentIndex = 0;

  testimonials = [
    {
      text: "Libraro is a very good cloud-based software. It has amazing analytics features.",
      name: "Neeraj Ji",
      role: "Solitude Library Kota"
    },
    {
      text: "As the Founder & Director, I built Libraro to simplify library automation.",
      name: "Pawan Rathore",
      role: "Founder"
    },
    {
      text: "We've been using Libraro for a year and it exceeded expectations.",
      name: "Sandeep Rathor",
      role: "Library Manager"
    },
    {
      text: "Seat booking and management is amazing.",
      name: "Rahul Sharma",
      role: "Reading Room Owner"
    },
    {
      text: "Very easy to use interface.",
      name: "Anita Verma",
      role: "Librarian"
    },
    {
      text: "Support team is very helpful.",
      name: "Amit Kumar",
      role: "Library Admin"
    },
    {
      text: "Managing books and reports became super easy.",
      name: "Rohit Gupta",
      role: "Library Staff"
    }
  ];

  next(){
    if(this.currentIndex < this.testimonials.length - 3){
      this.currentIndex++;
    }
  }

  prev(){
    if(this.currentIndex > 0){
      this.currentIndex--;
    }
  }

}