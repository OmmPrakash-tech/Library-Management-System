import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from './userService'; // ✅ adjust if needed
import { Router } from '@angular/router';

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
  userId: number = 0;

  showModal: boolean = false;
  imageLink: string = '';
  isEditMode: boolean = false;

  constructor(
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadProfile();
  }

  // ✅ Load profile
  loadProfile() {
    this.userService.getProfile().subscribe({
      next: (data: any) => {
        console.log("Profile Data:", data);

        this.userId = data.id;
        this.userName = data.fullName || '';
        this.email = data.email || '';
        this.imageUrl = data.profileImage || 'https://via.placeholder.com/150';
      },
      error: (err: any) => console.error(err)
    });
  }

  // ✏️ Toggle edit mode
  toggleEdit() {
    this.isEditMode = !this.isEditMode;
  }

  // 💾 Save profile
  saveProfile() {
    const payload = {
      fullName: this.userName,
      // ⚠️ Only send email if backend allows update
      email: this.email,
      profileImage: this.imageUrl
    };

    this.userService.updateProfile(this.userId, payload).subscribe({
      next: () => {
        alert('Profile Updated ✅');
        this.isEditMode = false;
      },
      error: (err: any) => console.error(err)
    });
  }

  // 🚀 Navigate
  goToProfilePage() {
    this.router.navigate(['/user/profile-view']);
  }

  // 📂 Modal control
  openModal() {
    this.showModal = true;
  }

  closeModal() {
    this.showModal = false;
  }

  // 📁 Upload image (preview only)
  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.imageUrl = URL.createObjectURL(file);
      this.closeModal();
    }
  }

  // 🔗 Use image URL
  useImageLink() {
    if (this.imageLink?.trim()) {
      this.imageUrl = this.imageLink;
      this.closeModal();
    }
  }
}