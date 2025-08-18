import { HttpInterceptorFn } from '@angular/common/http';
import { keycloak } from '../keycloak.init';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (!req.url.startsWith('/api')) return next(req);

  const token = keycloak?.token;
  if (!token) return next(req); // se ainda não tem token, a API pode devolver 401

  return next(req.clone({ setHeaders: { Authorization: `Bearer ${token}` } }));
};
