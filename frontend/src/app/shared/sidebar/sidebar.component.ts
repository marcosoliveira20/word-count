import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';

import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';

import { keycloak } from '../../keycloak.init';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink, RouterLinkActive,
    MatIconModule, MatListModule, MatTooltipModule, MatButtonModule, MatDividerModule
  ],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent {
  @Input() isExpanded = true;
  @Output() toggleMenu = new EventEmitter<void>();

  // Deixei só rotas "de navegação" aqui
  routeLinks = [
    { link: '',    name: 'Cadastrar Palavra(s) ',    icon: 'article'    },
    { link: 'words',    name: 'Listar Palavras',    icon: 'article'    },
    { link: 'insights', name: 'Insights', icon: 'dashboard' },
  ];

  async logout() {
    await keycloak.logout({ redirectUri: window.location.origin });
  }
}
