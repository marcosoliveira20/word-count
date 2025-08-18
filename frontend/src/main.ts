import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app';
import { provideRouter } from '@angular/router';
import { routes } from './app/app.routes';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './app/core/auth.interceptor';
import { initKeycloak, scheduleTokenRefresh } from './app/keycloak.init';

(async () => {
  // *** sem isso, você não é redirecionado para o KC ***
  const ok = await initKeycloak();
  if (ok) scheduleTokenRefresh();

  await bootstrapApplication(AppComponent, {
    providers: [
      provideRouter(routes),
      provideHttpClient(withInterceptors([authInterceptor]))
    ]
  });
})();
