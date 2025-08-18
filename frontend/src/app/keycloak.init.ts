import Keycloak, { KeycloakConfig, KeycloakProfile } from 'keycloak-js';

const config: KeycloakConfig = {
  url: 'http://localhost:8081/',      // ajuste p/ seu KC
  realm: 'english-realm',                // seu realm
  clientId: 'english-frontend'     // seu client (public)
};

export const keycloak = new Keycloak(config);

let _profile: KeycloakProfile | null = null;
export const getProfile = () => _profile;

export async function initKeycloak(): Promise<boolean> {
  // log p/ debug: veja no console se isso roda
  console.log('[Keycloak] init...');
  const authenticated = await keycloak.init({
    onLoad: 'login-required',   // redireciona p/ login
    checkLoginIframe: false,
    pkceMethod: 'S256',
    flow: 'standard'
  });
  console.log('[Keycloak] authenticated?', authenticated);

  if (authenticated) {
    try { _profile = await keycloak.loadUserProfile(); } catch { _profile = null; }
  }
  return authenticated;
}

let refreshTimer: any;
export function scheduleTokenRefresh() {
  clearInterval(refreshTimer);
  refreshTimer = setInterval(async () => {
    try { await keycloak.updateToken(30); } catch (e) { console.warn('Token refresh failed', e); }
  }, 20000);
}
