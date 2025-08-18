import { HttpInterceptorFn } from '@angular/common/http';
import { keycloak } from './keycloak.init';

export const tokenInterceptor: HttpInterceptorFn = (req, next) => {
  const token = keycloak.token;
  if (token && req.url.startsWith('/api')) {
    req = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }
  return next(req);
};
