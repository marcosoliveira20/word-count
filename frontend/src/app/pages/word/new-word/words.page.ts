import { Component, OnDestroy, OnInit, ViewChild, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { WordService } from '../../../core/word.service';

// Angular Material
import { MatTableModule } from '@angular/material/table';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';

export type WordUsage = {
  name: string;
  used_times: number;
  first_use: string;
  last_use: string;
};

@Component({
  selector: 'app-words',
  standalone: true,
  imports: [CommonModule, DatePipe, MatTableModule, MatPaginatorModule],
  templateUrl: './words.page.html',
  styleUrls: ['./words.page.scss'],
})
export default class WordsPage implements OnInit, OnDestroy {
  level = signal<string>('A1');
  loading = signal<boolean>(true);
  error = signal<string>('');
  rows = signal<WordUsage[]>([]);

  // tabela + paginação
  displayedColumns: (keyof WordUsage)[] = ['name', 'used_times', 'first_use', 'last_use'];
  pageIndex = signal(0);
  readonly pageSize = 10;

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  private sub?: Subscription;

  constructor(private route: ActivatedRoute, private api: WordService) {}

  ngOnInit(): void {
    this.sub = this.route.queryParamMap.subscribe((params) => {
      const lv = (params.get('level') || 'A1').toUpperCase();
      this.level.set(lv);
      this.fetch(lv);
    });
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }

  // fatia os dados para a página atual (sem mudar sua API)
  get pagedRows(): WordUsage[] {
    const start = this.pageIndex() * this.pageSize;
    return this.rows().slice(start, start + this.pageSize);
  }

  onPageChange(event: { pageIndex: number; pageSize: number; length: number }) {
    this.pageIndex.set(event.pageIndex);
  }

  private fetch(level: string) {
    this.loading.set(true);
    this.error.set('');
    this.pageIndex.set(0); // sempre volta para a 1ª página ao trocar o nível

    this.api.getWordsByLevel(level).subscribe({
      next: (resp) => {
        this.rows.set(resp.words ?? []);
        this.loading.set(false);
      },
      error: (e) => {
        console.error('[GET words by level] error', e);
        this.error.set('Falha ao carregar lista.');
        this.loading.set(false);
      },
    });
  }
}
