import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { WordService } from '../../../core/word.service';

// Angular Material
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-word',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule,
    MatCardModule, MatFormFieldModule, MatInputModule,
    MatButtonModule, MatIconModule, MatDividerModule
  ],
  templateUrl: './word.component.html',
  styleUrls: ['./word.component.scss']
})
export class WordComponent implements OnInit {
  form = new FormGroup({
    word: new FormControl<string>('', { nonNullable: true, validators: [Validators.required] })
  });

  successMsg = signal('');
  errorMsg   = signal('');
  total      = signal<number | null>(null);

  // estados de UI
  isSubmitting = signal(false);
  isUploading  = signal(false);

  selectedFile: File | null = null;

  constructor(private api: WordService) {}

  ngOnInit(): void { this.refresh(); }

onSubmit() {
  this.errorMsg.set('');
  this.successMsg.set('');

  if (this.form.invalid) return;

  const value = this.form.value.word!.trim();
  if (!value) return;

  this.isSubmitting.set(true);

  this.api.addWord(value).pipe(
    finalize(() => this.isSubmitting.set(false)) // sempre executa
  ).subscribe({
    next: () => {
      this.successMsg.set('Palavra cadastrada com sucesso!');
      this.form.reset({ word: '' });
      this.refresh();
    },
    error: (e) => {
      this.errorMsg.set(`Erro ao cadastrar (${e?.status ?? '??'}).`);
    }
  });
}
  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    this.selectedFile = input.files && input.files.length ? input.files[0] : null;
    this.errorMsg.set('');
    this.successMsg.set('');
  }

  onUpload() {
    if (!this.selectedFile) return;
    this.isUploading.set(true);
    this.api.onUpload(this.selectedFile);
    // se seu serviço for assíncrono/retornar observable, troque por subscribe e limpe no complete
    this.successMsg.set('Arquivo enviado (processamento iniciado).');
    this.isUploading.set(false);
  }

  private refresh() {
    this.api.getCount().subscribe({
      next: n => this.total.set(n),
      error: () => this.errorMsg.set('Falha ao obter contagem.')
    });
  }
}
