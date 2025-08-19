import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { WordService } from '../../core/word.service';

@Component({
  selector: 'app-word',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './word.component.html',
  styleUrls: ['./word.component.scss']
})
export class WordComponent implements OnInit {
  form = new FormGroup({
    word: new FormControl<string>('', { nonNullable: true, validators: [Validators.required] })
  });

  successMsg = signal('');
  errorMsg = signal('');
  total = signal<number | null>(null);

  ngOnInit(): void {
    this.refresh();
  }

  constructor(private api: WordService) {}

  onSubmit() {
    console.log('[onSubmit] fired');

    this.errorMsg.set('');

    if (this.form.invalid) {
      console.log('[onSubmit] form inválido', this.form.errors, this.form.value);
      return;
    }

    const value = this.form.value.word?.trim() ?? '';
    if (!value) {
      console.log('[onSubmit] vazio após trim');
      return;
    }

    this.api.addWord(value).subscribe({
      next: () => {
        console.log('[POST] ok');
        this.successMsg.set('Palavra cadastrada com sucesso!');
        this.form.reset({ word: '' });
        this.refresh();
      },
      error: (e) => {
        console.error('[POST] erro', e?.status, e);
        this.errorMsg.set(`Erro ao cadastrar (${e?.status ?? '??'}).`);
      }
    });
  }

  onClick() {
    console.log('[button click] fired');
  }

  private refresh() {
    this.api.getCount().subscribe({
      next: n => this.total.set(n),
      error: e => {
        console.error('[GET] erro', e?.status, e);
        this.errorMsg.set('Falha ao obter contagem.');
      }
    });
  }
  

  selectedFile: File | null = null;

    onFileSelected(event: Event) {
      const input = event.target as HTMLInputElement;
      if (input.files && input.files.length > 0) {
        this.selectedFile = input.files[0];
      }
    }

onUpload() {
  if (!this.selectedFile) return;
  this.api.onUpload(this.selectedFile); 
}
}

