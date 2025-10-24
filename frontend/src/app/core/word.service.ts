import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Word, WordCountResponse } from './word.model';


@Injectable({ providedIn: 'root' })
export class WordService {
  private readonly base = '/api/words'; // vai passar pelo proxy p/ :8080

  constructor(private http: HttpClient) {}

  createWord(name: string){
    return this.http.post(`${this.base}/usages`, { name });
  }

  getLevels(): Observable<{levels: {level: string; count: number}[]}>{
    return this.http.get<{levels: {level: string; count: number}[]}>(`${this.base}/levels`);
  }

  getWordsByLevel(level: string){
    return this.http.get<{words: {name:string; used_times:number; first_use:string; last_use:string}[]}>(
      `${this.base}`,
      { params: { level } }
    );
  }

  // (opcional) detalhes e busca
  getWordDetail(id: number){
    return this.http.get(`${this.base}/${id}`);
  }

  searchWords(q: string){
    return this.http.get<{words: any[]}>(`${this.base}`, { params: { search: q }});
  }

  addWord(name: string): Observable<void> {
    const body: Word = { name };
    return this.http.post<void>(`${this.base}/usages`, body);
  }

  getCount(): Observable<number> {
    return this.http.get<WordCountResponse | number>(this.base + '/infos').pipe(
      map((resp: any) => {

        if(resp.number) return resp.number;

        if (typeof resp === 'number') return resp;

        if (resp && typeof resp.count === 'number') return resp.count;
        
        if (Array.isArray(resp)) return resp.length;
        return 0;
      })
    );
  }

  onUpload(selectedFile: File) {
    if (!selectedFile) return;

    const formData = new FormData();
    formData.append("file", selectedFile);

    this.http.post("/api/uploads", formData).subscribe({
      next: () => alert("Upload realizado com sucesso"),
      error: (err) => alert("Erro no upload: " + err.message),
    });
  }

}

