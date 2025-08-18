import { Component, OnDestroy, OnInit, computed, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';
import { WordService } from '../../core/word.service';

export type WordUsage = {
  name: string;
  used_times: number;
  first_use: string;   // ISO no backend; vamos formatar no template
  last_use: string;
};

@Component({
  selector: 'app-words',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe],
  templateUrl: './words.page.html',
  styleUrls: ['./words.page.scss']
})
export default class WordsPage implements OnInit, OnDestroy {
  level = signal<string>('A1');
  loading = signal<boolean>(true);
  error = signal<string>('');
  rows = signal<WordUsage[]>([]);

  private sub?: Subscription;

  constructor(private route: ActivatedRoute, private api: WordService) {}

  ngOnInit(): void {
    // re-carrega ao mudar o query param ?level=
    this.sub = this.route.queryParamMap.subscribe(params => {
      const lv = (params.get('level') || 'A1').toUpperCase();
      this.level.set(lv);
      this.fetch(lv);
    });
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  private fetch(level: string) {
    this.loading.set(true);
    this.error.set('');
    this.api.getWordsByLevel(level).subscribe({
      next: (resp) => {
        this.rows.set(resp.words ?? []);
        this.loading.set(false);
      },
      error: (e) => {
        console.error('[GET words by level] error', e);
        this.error.set('Falha ao carregar lista.');
        this.loading.set(false);
      }
    });
  }
}
