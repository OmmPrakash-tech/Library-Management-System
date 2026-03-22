import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../profile/userService';


@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './edit.html',
  styleUrls: ['./edit.css']
})
export class EditComponent {

  @Input() user: any; // incoming user data
  @Output() close = new EventEmitter();
  @Output() updated = new EventEmitter();

  formData = {
    fullName: '',
    phone: '',
    password: ''
  };

  isSaving = false;

  constructor(private userService: UserService) {}

  ngOnInit() {
    if (this.user) {
      this.formData.fullName = this.user.fullName;
      this.formData.phone = this.user.phone;
    }
  }

  save() {
    this.isSaving = true;

    const payload: any = {
      fullName: this.formData.fullName,
      phone: this.formData.phone
    };

    // only send password if entered
    if (this.formData.password) {
      payload.password = this.formData.password;
    }

    this.userService.updateProfile(this.user.id, payload).subscribe({
      next: (res) => {
        this.isSaving = false;
        this.updated.emit(res);
        this.close.emit();
      },
      error: (err) => {
        console.error(err);
        this.isSaving = false;
      }
    });
  }

  cancel() {
    this.close.emit();
  }
}