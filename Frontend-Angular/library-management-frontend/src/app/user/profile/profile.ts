import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from './userService';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.html',
  styleUrls: ['./profile.css']
})
export class Profile implements OnInit {

  imageUrl: string = '';
  userName: string = '';
  email: string = '';

  showModal: boolean = false;
  imageLink: string = '';
  isEditMode: boolean = false;

  constructor(private userService: UserService) {}

  ngOnInit() {
    this.loadProfile();
  }

  // 🔥 Load user profile from backend
  loadProfile() {
    this.userService.getProfile().subscribe((data: any) => {
      this.userName = data.name;
      this.email = data.email;
      this.imageUrl = data.image || 'https://via.placeholder.com/150';
    });
  }

  // ✏️ Enable edit mode
  enableEdit() {
    this.isEditMode = true;
  }

  // 💾 Save profile
  saveProfile() {
    const payload = {
      name: this.userName,
      email: this.email,
      image: this.imageUrl
    };

    this.userService.updateProfile(payload).subscribe(() => {
      alert('Profile Updated ✅');
      this.isEditMode = false;
    });
  }

  // IMAGE LOGIC (same as before)
  openModal() {
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.imageUrl = URL.createObjectURL(file);
      this.closeModal();
    }
  }

  useImageLink() {
    if (this.imageLink) {
      this.imageUrl = this.imageLink;
      this.closeModal();
    }
  }

  selectAvatar(path: string) {
    this.imageUrl = path;
    this.closeModal();
  }
}