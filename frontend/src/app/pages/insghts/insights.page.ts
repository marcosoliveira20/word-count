import { Component, OnInit, inject, signal } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { RouterLink } from '@angular/router';
import { WordService } from '../../core/word.service';

type Level = 'A1' | 'A2' | 'B1' | 'B2' | 'C1' | 'C2';
type LevelCount = { level: Level; count: number };
const isLevel = (v: string): v is Level =>
  (['A1','A2','B1','B2','C1','C2'] as const).includes(v as Level);

@Component({
  selector: 'app-insights',
  standalone: true,
  imports: [NgFor, NgIf, RouterLink],
  templateUrl: './insights.page.html',
  styleUrls: ['./insights.page.scss']
})
export default class InsightsPage implements OnInit {
  private svc = inject(WordService);
  levels = signal<LevelCount[]>([]);
  placeholders = Array.from({ length: 5 });

  ngOnInit(): void {
    this.svc.getLevels().subscribe({
      next: (res) => {
        const normalized: LevelCount[] = (res.levels ?? [])
          .map(x => ({ level: (x.level ?? '').toUpperCase(), count: x.count }))
          .filter(x => isLevel(x.level))
          .map(x => ({ level: x.level as Level, count: x.count }));
        this.levels.set(normalized);
      },
      error: () => this.levels.set([])
    });
  }

  trackByLevel = (_: number, item: LevelCount) => item.level;
}