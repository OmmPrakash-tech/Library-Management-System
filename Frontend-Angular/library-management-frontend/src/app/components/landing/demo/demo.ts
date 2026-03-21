import { Component } from '@angular/core';
import emailjs from '@emailjs/browser';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-demo',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './demo.html',
  styleUrls: ['./demo.css'],
})
export class Demo {

  name = '';
  email = '';
  mobile = '';
  date = '';
  slot = '';

  bookDemo() {

    const templateParams = {
      name: this.name,
      email: this.email,
      mobile: this.mobile,
      date: this.date,
      slot: this.slot
    };

    emailjs.send(
      'service_ot3m0fj',     // your service id
      'template_wltofh5',      // replace with your template id
      templateParams,
      'BAk0IHRx1nVjVw-c0'      // replace with your public key
    ).then(() => {

      alert("Demo request sent successfully!");

      this.name='';
      this.email='';
      this.mobile='';
      this.date='';
      this.slot='';

    }).catch(() => {

      alert("Failed to send request");

    });

  }

}