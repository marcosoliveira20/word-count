export interface Word {
  name: string;
}

export interface WordCountResponse {
  // suporte aos dois formatos comuns de backend:
  // { count: number }  OU  número puro
  count?: number;
}
