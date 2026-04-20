import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  credentials = { username: '', password: '', mfaCode: 0 };
  mfaRequired = false;
  errorMessage = '';
  showPassword = false;

  constructor(private authService: AuthService, private router: Router) {}

  onSubmit() {
    this.errorMessage = '';
    this.authService.login(this.credentials).subscribe({
      next: (res) => {
        if (res.mfaRequired) {
          this.mfaRequired = true; // Demande à l'utilisateur d'entrer son code Google Authenticator
          this.errorMessage = res.message;
        } else {
          // JWT récupéré, redirection
          this.router.navigate(['/']);
        }
      },
      error: (err) => {
        this.errorMessage = "Échec de connexion. Vérifiez vos identifiants.";
      }
    });
  }
}
