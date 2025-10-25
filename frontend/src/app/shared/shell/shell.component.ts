import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { getProfile, keycloak } from '../../keycloak.init';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent],
  template: `
<div class="shell">
  <main class="content">
    <section class="page">
      <router-outlet> </router-outlet>
    </section>
  </main>
</div>
  `,
  styles:[`
    .shell{ display:flex; min-height:100vh; background:#F5F7F5; }
    .content{ flex:1; padding:40px 32px; }
  `]
})

export class ShellComponent {
  userName = getProfile()?.firstName ?? '';

  async logout() {
    await keycloak.logout({ redirectUri: window.location.origin });
  }
}
