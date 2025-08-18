

import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: `<router-outlet></router-outlet>`
})
export class AppComponent {
  async logout() {
    // importa o keycloak sem criar dependência cíclica
    const { keycloak } = await import('./keycloak.init');
    keycloak.logout({ redirectUri: window.location.origin });
  }
}
