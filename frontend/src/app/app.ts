import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

// Módulos do Material necessários
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatButtonModule } from '@angular/material/button';

// Importe seu componente de sidebar (se ele for standalone)
import { SidebarComponent } from './shared/sidebar/sidebar.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    MatSidenavModule,   // ← precisa para <mat-sidenav-*>
    MatButtonModule,    // ← precisa para mat-button
    SidebarComponent    // ← o componente usado dentro do sidenav
  ],
  styles: [
    `
      .content-container {
        min-height: 100vh;
        box-sizing: border-box;
        padding: 25px;
      }
    `,
  ],
  template: `
    <mat-sidenav-container autosize>
      <mat-sidenav #sidenav fixedInViewport="true" mode="side" opened>
        <app-sidebar></app-sidebar>
      </mat-sidenav>

      <mat-sidenav-content>
        <div class="content-container">
          <button type="button" mat-button (click)="sidenav.toggle()">
            Menu
          </button>
          <router-outlet></router-outlet>
        </div>
      </mat-sidenav-content>
    </mat-sidenav-container>
  `,
})
export class AppComponent {
  async logout() {
    const { keycloak } = await import('./keycloak.init');
    keycloak.logout({ redirectUri: window.location.origin });
  }
}
