import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../profile/userService';
import { EditComponent } from './edit/edit'; // ✅ import edit component

@Component({
  selector: 'app-profile-view',
  standalone: true,
  imports: [CommonModule, EditComponent], // ✅ add here
  templateUrl: './profile-view.html',
  styleUrls: ['./profile-view.css']
})
export class ProfileView implements OnInit {

  user: any;
  isLoading: boolean = true;
  isRefreshing: boolean = false;

  // ✅ ADD THIS (for modal control)
  showEdit: boolean = false;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  loadProfile() {
    this.userService.getProfile().subscribe({
      next: (res: any) => {
        console.log("Profile Data:", res);
        this.user = res;
        this.isLoading = false;
        this.isRefreshing = false;
      },
      error: (err) => {
        console.error("Error:", err);
        this.isLoading = false;
        this.isRefreshing = false;
      }
    });
  }

  refreshProfile() {
    this.isRefreshing = true;
    this.loadProfile();
  }

  // ✅ HANDLE UPDATED USER
  onUserUpdated(updatedUser: any) {
    this.user = updatedUser;
  }
}