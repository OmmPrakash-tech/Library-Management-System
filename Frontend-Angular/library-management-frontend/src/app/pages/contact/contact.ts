import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-contact',
  imports: [CommonModule, FormsModule],
  templateUrl: './contact.html',
  styleUrls: ['./contact.css']
})
export class Contact {

  name = '';
  email = '';
  message = '';

  submitForm() {

    if(!this.name || !this.email || !this.message){
      alert("Please fill all fields");
      return;
    }

    alert("Message sent successfully!");

    this.name = '';
    this.email = '';
    this.message = '';

  }

}