import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-library',
  imports: [CommonModule,FormsModule],
  templateUrl: './library.html',
  styleUrls: ['./library.css']
})
export class Library {

  selectedCity = '';
  filteredLibraries: any[] = [];

  libraries = [

    {
      name: "The Learning Point Library",
      city: "Kota",
      address: "Near Amber Dairy",
      seats: 121,
      type: "Public"
    },

    {
      name: "Gyan Vatika Library",
      city: "Kota",
      address: "Sunful Complex Service Road",
      seats: 52,
      type: "Public"
    },

    {
      name: "Sanidhya Library",
      city: "Kota",
      address: "Vinoba Bhave Nagar",
      seats: 50,
      type: "Public"
    },

    {
      name: "City Study Library",
      city: "Delhi",
      address: "Connaught Place",
      seats: 80,
      type: "Private"
    },

    {
      name: "Knowledge Hub Library",
      city: "Mumbai",
      address: "Andheri West",
      seats: 95,
      type: "Public"
    }

  ];


  searchLibraries(){

    this.filteredLibraries = this.libraries.filter(
      lib => lib.city === this.selectedCity
    );

  }

}