import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService, User } from './userService';
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
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  // ================= LOAD PROFILE =================
  loadProfile(): void {
    this.userService.getProfile().subscribe({
      next: (data: User) => {
        this.userId = data.id || 0;
        this.userName = data.fullName || '';
        this.email = data.email || '';
        this.imageUrl = data.profileImage || '';

        this.cdr.detectChanges(); // 🔥 fix UI sync
      },
      error: (err) => console.error('Profile load error:', err)
    });
  }

  // ================= TOGGLE EDIT =================
  toggleEdit(): void {
    this.isEditMode = !this.isEditMode;
  }

  // ================= SAVE PROFILE =================
  saveProfile(): void {

    // 🔥 USE PARTIAL TYPE (FIX ERROR)
    const payload: Partial<User> = {
      fullName: this.userName,
      email: this.email,
      profileImage: this.imageUrl
    };

    this.userService.updateProfile(this.userId, payload as User).subscribe({
      next: () => {
        alert('Profile Updated ✅');

        this.isEditMode = false;

        // 🔥 reload fresh data
        this.loadProfile();
      },
      error: (err) => console.error('Update error:', err)
    });
  }

  // ================= NAVIGATION =================
  goToProfilePage(): void {
    this.router.navigate(['/user/profile-view']);
  }

  // ================= MODAL =================
  openModal(): void {
    if (!this.isEditMode) return; // 🔥 restrict edit
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

  // ================= FILE UPLOAD =================
  onFileSelected(event: any): void {
    const file = event.target.files[0];

    if (!file) return;

    const reader = new FileReader();

    reader.onload = () => {
      this.imageUrl = reader.result as string; // 🔥 base64
      this.cdr.detectChanges();
    };

    reader.readAsDataURL(file);

    this.closeModal();
  }

  // ================= IMAGE URL =================
  useImageLink(): void {
    if (!this.imageLink?.trim()) return;

    this.imageUrl = this.imageLink.trim();
    this.closeModal();
  }
}