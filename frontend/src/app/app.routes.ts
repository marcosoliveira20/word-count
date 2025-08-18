// src/app/app.routes.ts
import { Routes } from '@angular/router';
import { ShellComponent } from './shared/shell/shell.component';
export const routes: Routes = [
  {
    path: '',
    component: ShellComponent,
    children: [
      { path: '', loadComponent: () => import('./pages/word/word.component').then(m => m.WordComponent) },
      { path: 'insights', loadComponent: () => import('./pages/insghts/insights.page').then(m => m.default) },
      { path: 'words', loadComponent: () => import('./pages/word/words.page').then(m => m.default) }, // << novo
      { path: 'profile', loadComponent: () => import('./shared/placeholder/placeholder.componet').then(m => m.default) },
      { path: 'settings', loadComponent: () => import('./shared/placeholder/placeholder.componet').then(m => m.default) }
    ]
  },
  { path: '**', redirectTo: '' }
];

