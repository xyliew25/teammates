import { Component, OnInit } from '@angular/core';
import { AngularFireAuth } from '@angular/fire/compat/auth';
import { ActivatedRoute } from '@angular/router';
import { environment } from '../environments/environment';

/**
 * Login page component.
 */
@Component({
  selector: 'tm-login-page',
  templateUrl: './login-page.component.html',
  styleUrls: ['./login-page.component.scss'],
})
export class LoginPageComponent implements OnInit {

  private nextUrl: string = '';
  private mode: string = '';
  private backendUrl: string = environment.backendUrl;
  private frontendUrl: string = environment.frontendUrl;

  isSignInWithEmail: boolean = false;
  isSignInLinkEmailSent: boolean = false;
  email: string = 'test@example.com';
  loading: boolean = false;

  constructor(private route: ActivatedRoute, private afAuth: AngularFireAuth) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((queryParams: any) => {
      this.nextUrl = queryParams.nextUrl;
      this.mode = queryParams.mode;
    });
    if (this.mode === 'signIn') {
      let email = window.localStorage.getItem('emailForSignIn');
      if (!email) {
        email = window.prompt('Please provide your email for confirmation');
      }
      this.afAuth.signInWithEmailLink(email!, window.location.href)
          .then(authResult => {
            window.localStorage.removeItem('emailForSignIn');
            window.location.href = `${this.backendUrl}/oauth2callback?email=${authResult.user!.email}`
                + `&nextUrl=${this.nextUrl}`;
          })
          .catch((error) => {
            console.warn('signInWithEmailLinkError', error);
          });
    }
  }

  triggerEmailChange(newEmail: string): void {
    this.email = newEmail;
  }

  signIn(): void {
    this.loading = true;
    const actionCodeSettings = {
      url: `${this.frontendUrl}/web/login?nextUrl=${this.nextUrl}`,
      handleCodeInApp: true,
    };
    this.afAuth.sendSignInLinkToEmail(this.email, actionCodeSettings)
        .then(() => {
          this.isSignInLinkEmailSent = true;
          window.localStorage.setItem('emailForSignIn', this.email);
          this.loading = false;
        })
        .catch((error) => {
          console.warn('sendSignInLinkToEmailError', error);
        });
  }

  logout(): void {
    this.afAuth.signOut();
  }
}
