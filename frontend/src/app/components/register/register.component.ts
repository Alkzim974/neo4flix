import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  userData = { username: '', email: '', password: '', enableMfa: false };
  errorMessage = '';
  successMessage = '';
  qrCodeUrl = '';
  showPassword = false;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    this.errorMessage = '';
    this.successMessage = '';
    this.qrCodeUrl = '';

    if (this.userData.password.length < 8) {
      this.errorMessage = "Le mot de passe doit contenir au moins 8 caractères.";
      return;
    }

    this.authService.register(this.userData).subscribe({
      next: (res) => {
        this.successMessage = "Inscription réussie !";
        if (res.qrCodeUrl) {
          this.qrCodeUrl = res.qrCodeUrl;
        } else {
          // Immediately redirect to home if no QR code needs to be shown
          this.router.navigate(['/']);
        }
      },
      error: (err) => {
        this.errorMessage = err.error?.message || "Échec de l'inscription.";
      }
    });
  }

  getBarcodeUrl(data: string): string {
    // Utilisation d'une API de génération de QR Code rapide
    return `https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=${encodeURIComponent(data)}`;
  }
}
