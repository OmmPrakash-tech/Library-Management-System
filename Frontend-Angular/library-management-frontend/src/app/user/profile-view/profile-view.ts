import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../profile/userService';
import { EditComponent } from './edit/edit';

@Component({
  selector: 'app-profile-view',
  standalone: true,
  imports: [CommonModule, EditComponent],
  templateUrl: './profile-view.html',
  styleUrls: ['./profile-view.css']
})
export class ProfileView implements OnInit {

  user: any;
  isLoading: boolean = true;
  isRefreshing: boolean = false;

  showEdit: boolean = false;

  constructor(
    private userService: UserService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  // ================= LOAD =================
  loadProfile() {
    this.userService.getProfile().subscribe({
      next: (res: any) => {
        this.user = res;
        this.isLoading = false;
        this.isRefreshing = false;

        this.cdr.detectChanges(); // 🔥 fix UI refresh issues
      },
      error: (err) => {
        console.error(err);
        this.isLoading = false;
        this.isRefreshing = false;
      }
    });
  }

  // ================= REFRESH =================
  refreshProfile() {
    this.isRefreshing = true;
    this.loadProfile();
  }

  // ================= OPEN EDIT =================
  openEdit() {
    this.showEdit = true;
  }

  // ================= CLOSE EDIT =================
  closeEdit() {
    this.showEdit = false;
  }

  // ================= AFTER UPDATE =================
  onUserUpdated(updatedUser: any) {
    this.user = updatedUser;
    this.showEdit = false; // 🔥 close modal
  }
}