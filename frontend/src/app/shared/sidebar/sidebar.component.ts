import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { NgClass } from '@angular/common';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, NgClass],
  template: `
  <aside class="sidebar">
    <nav>
      <a routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{exact:true}">Cadastrar nova palavra</a>
      <a routerLink="/profile" class="disabled">Ver Perfil</a>
      <a routerLink="/insights" routerLinkActive="active">Insights</a>
      <a routerLink="/words" routerLinkActive="active">Lista</a> <!-- novo -->
      <a routerLink="/settings" class="disabled">Configurações</a>
    </nav>

    <div class="logout">Sair</div>
  </aside>
  `,
  styles: [`
    .sidebar{
      width: 260px; min-height: 100vh; background:#1F271B; color:#FCFFFC; display:flex; flex-direction:column; gap:24px; padding:28px 20px;
      position: sticky; top:0;
    }
    nav{ display:flex; flex-direction:column; gap:12px; }
    a{ color:#FCFFFC; text-decoration:none; padding:10px 12px; border-radius:8px; display:block; }
    a:hover{ background:#19647E; }
    a.active{ background:#19647E; }
    .disabled{ opacity:.6; pointer-events:none; }
    .logout{ margin-top:auto; font-size:.9rem; opacity:.8; }
    @media (max-width: 960px){
      .sidebar{ width: 220px; }
    }
  `]
})
export class SidebarComponent {}
