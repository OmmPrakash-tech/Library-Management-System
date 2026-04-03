import { Routes } from '@angular/router';

import { Landing } from './pages/landing/landing';
import { About } from './pages/about/about';
import { Pricing } from './pages/pricing/pricing';
import { Blog } from './pages/blog/blog';
import { Contact } from './pages/contact/contact';
import { Library } from './pages/library/library';
import { Signup } from './pages/signup/signup';
import { Login } from './pages/login/login';
 
import { Home as UserHome } from './user/home/home';
import { Home as AdminHome } from './Admin/home/home';

import { DashboardFeature } from './components/landing/feature2/dashboard-feature/dashboard-feature';
import { UiFeature } from './components/landing/feature2/ui-feature/ui-feature';
import { DataImportFeature } from './components/landing/feature2/data-import-feature/data-import-feature';
import { SeatManagementFeature } from './components/landing/feature2/seat-management-feature/seat-management-feature';
import { SecurityFeature } from './components/landing/feature2/security-feature/security-feature';
import { AttendanceFeature } from './components/landing/feature2/attendance-feature/attendance-feature';
import { ReportsFeature } from './components/landing/feature2/reports-feature/reports-feature';
import { DirectoryListingFeature } from './components/landing/feature2/directory-listing-feature/directory-listing-feature';
import { ManageBooks } from './Admin/book/book-control/manage-books/manage-books';

import { Book } from './user/home/book/book';
import { ProfileView } from './user/profile-view/profile-view';

import { AllUser } from './Admin/user/alluser/alluser';
import { BookControl } from './Admin/book/book-control/book-control';
  
/* ✅ IMPORT EDIT USER */
import { EditUser } from './Admin/user/alluser/edituser/edituser';
import { GenreControl } from './Admin/genre/genre-control/genre-control';
import { AddBook } from './Admin/book/book-control/add-book/add-book';
import { ViewBook } from './Admin/book/book-control/view-book/view-book';
import { PaymentComponent } from './payment/payment';
import { SubscriptionComponent } from './user/home/subscribe/subscribe';
import { WishlistComponent } from './user/home/wishlist/wishlist';
import { BookViewComponent } from './user/home/book/view/view';
import { BookLoanFrontComponent } from './user/home/book-loan-front/book-loan-front';
import { IssueComponent } from './user/home/book-loan-front/issue/issue';
import { RenewReturnComponent } from './user/home/book-loan-front/renew-return/renew-return';
import { FineComponent } from './Admin/fine/fine';
import { FineControllerPageComponent } from './Admin/fine/fine-controller/fine-controller';
import { SubscriptionControllerComponent } from './Admin/subscription/subscription-contoll/subscription-contoll';
import { SubscriptionPlanComponent } from './Admin/subscription/subscription-plan/subscription-plan';
import { ReviewUserComponent } from './user/home/book-loan-front/renew-return/review-user/review-user';
import { FineUserComponent } from './user/home/book-loan-front/fine-user/fine-user';

export const routes: Routes = [

  { path:'', component: Landing },

  { path:'about', component: About },

  { path:'pricing', component: Pricing },

  { path:'library', component: Library },

  { path:'blog', component: Blog },

  { path:'contact', component: Contact },

  { path:'signup', component: Signup },

  { path:'login', component: Login },

  { path: 'user/home', component: UserHome },

  { path: 'admin/home', component: AdminHome },

  {
    path: 'feature/dashboard',
    component: DashboardFeature
  },
  {
    path: 'feature/ui',
    component: UiFeature
  },
  {
    path: 'feature/data-import',
    component: DataImportFeature
  },
  {
    path: 'feature/seat-management',
    component: SeatManagementFeature
  },
  {
    path: 'feature/security',
    component: SecurityFeature
  },
  {
    path: 'feature/attendance',
    component: AttendanceFeature
  },
  {
    path: 'feature/reports',
    component: ReportsFeature
  },
  {
    path: 'feature/directory',
    component: DirectoryListingFeature
  },

  { path: 'book', component: Book },

  { path: 'user/profile-view', component: ProfileView },

  /* ✅ ALL USERS PAGE */
  { path: 'admin/alluser', component: AllUser },

  /* 🔥 ADD THIS (EDIT USER ROUTE) */
  { path: 'admin/edit-user/:id', component: EditUser },

  { path: 'book-control', component: BookControl },

    { path: 'genre-control', component: GenreControl },

    { path: 'add-book', component: AddBook },

    { path: 'view-book/:id', component: ViewBook },

    { path: 'manage-books', component: ManageBooks },

    {
  path: 'payment',
  component: PaymentComponent
},

{ path: 'subscription', component: SubscriptionComponent },

  { path: 'wishlist', component: WishlistComponent },

{ path: 'book/:id', component: BookViewComponent },

{ path: 'book-loan-front', component: BookLoanFrontComponent },

{
    path: 'issue-book',
    loadComponent: () =>
      import('./user/home/book-loan-front/issue/issue')
        .then(m => m.IssueComponent)
  },

  { path: 'renew-return', component: RenewReturnComponent },

   // 🔹 Fine landing page
  { path: 'fine', component: FineComponent },

  // 🔹 Fine controller page (table page)
  { path: 'fines', component: FineControllerPageComponent },

  {
    path: 'subscriptions',
    component: SubscriptionControllerComponent
  },

{
    path: 'subscription-plans',
    component: SubscriptionPlanComponent
  },

  {
  path: 'review-user/:bookId',
  component: ReviewUserComponent
},

{
    path: 'my-fines',
    component: FineUserComponent
  }

  
];