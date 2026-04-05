import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
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
export class EditComponent implements OnChanges {

  @Input() user: any;
  @Output() close = new EventEmitter<void>();
  @Output() updated = new EventEmitter<any>();

  formData = {
    fullName: '',
    phone: '',
    password: '',
    profileImage: '' // 🔥 NEW
  };

  isSaving = false;

  constructor(private userService: UserService) {}

  // 🔥 HANDLE INPUT CHANGES (BETTER THAN ngOnInit)
  ngOnChanges(changes: SimpleChanges) {
    if (changes['user'] && this.user) {
      this.formData.fullName = this.user.fullName || '';
      this.formData.phone = this.user.phone || '';
      this.formData.profileImage = this.user.profileImage || '';
    }
  }

  // ================= SAVE =================
  save() {
    this.isSaving = true;

    const payload: any = {
      fullName: this.formData.fullName,
      phone: this.formData.phone,
      profileImage: this.formData.profileImage // 🔥 include image
    };

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

  // ================= IMAGE URL =================
  setImageUrl(url: string) {
    this.formData.profileImage = url;
  }

  // ================= FILE UPLOAD =================
  onFileSelected(event: any) {
    const file = event.target.files[0];

    if (file) {
      const reader = new FileReader();

      reader.onload = () => {
        this.formData.profileImage = reader.result as string; // 🔥 base64
      };

      reader.readAsDataURL(file);
    }
  }

  cancel() {
    this.close.emit();
  }
}