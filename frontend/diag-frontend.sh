#!/usr/bin/env bash
set -euo pipefail

OUT="diagnostico_frontend_$(date +%Y%m%d_%H%M%S).txt"
sep(){ printf "\n===== %s =====\n" "$1" >> "$OUT"; }

echo "Coletando diagnóstico do FRONTEND em $OUT ..."
: > "$OUT"

# 0) Versões / ambiente
sep "VERSOES / AMBIENTE"
{
  echo "uname: $(uname -a || true)"
  echo "node:  $(node -v 2>/dev/null || echo 'N/A')"
  echo "npm:   $(npm -v 2>/dev/null || echo 'N/A')"
  echo "pnpm:  $(pnpm -v 2>/dev/null || echo 'N/A')"
  echo "yarn:  $(yarn -v 2>/dev/null || echo 'N/A')"
  echo "ng version:"
  npx -y @angular/cli@latest ng version 2>/dev/null || true
} >> "$OUT"

# 1) Árvore (sem node_modules/dist/.angular)
sep "ARVORE DE ARQUIVOS (src/, top 400)"
find src -maxdepth 6 \
  \( -path 'src/**/node_modules' -o -path 'src/**/.angular' \) -prune -o -type f -print \
  | head -n 400 >> "$OUT"

# 2) Arquivos-chave do Angular
sep "ARQUIVOS ANGULAR – CHAVES"
FILES=(
  "src/main.ts"
  "src/app/app.ts" "src/app/app.html" "src/app/app.config.ts" "src/app/app.routes.ts"
  "src/app/keycloak.init.ts"
  "src/app/core/auth.interceptor.ts" "src/app/core/auth.guard.ts" "src/app/core/word.service.ts"
  "src/app/shared/shell/shell.component.ts" "src/app/shared/shell/shell.component.html" "src/app/shared/shell/shell.component.scss"
  "src/app/shared/sidebar/sidebar.component.ts" "src/app/shared/sidebar/sidebar.component.html" "src/app/shared/sidebar/sidebar.component.scss"
  "src/app/pages/word/word.component.ts" "src/app/pages/word/word.component.html" "src/app/pages/word/word.component.scss"
  "src/app/pages/insights/insights.page.ts" "src/app/pages/insights/insights.page.html" "src/app/pages/insights/insights.page.scss"
  "src/environments/environment.ts" "src/environments/environment.development.ts"
  "proxy.conf.json" "angular.json" "tsconfig.json" "tsconfig.app.json" "package.json"
)
for f in "${FILES[@]}"; do
  [ -f "$f" ] || continue
  sep "FILE: $f"
  nl -ba "$f" | sed -e 's/\t/    /g' | head -n 500 >> "$OUT"
done

# 3) Greps úteis (rotas, router, provideRouter, tokens, API)
sep "GREP – provideRouter/Routes/RouterOutlet"
grep -RIn -E "provideRouter\\(|Routes\\s*=|RouterOutlet" src 2>/dev/null | head -n 200 >> "$OUT"

sep "GREP – endpoints /api e HttpClient"
grep -RIn -E "http\\.(get|post|put|delete)\\(|/api/" src 2>/dev/null | head -n 200 >> "$OUT"

sep "GREP – keycloak / Authorization / logout/login"
grep -RIn -E "keycloak|Authorization|Bearer|logout\\(|login\\(" src 2>/dev/null | head -n 200 >> "$OUT"

echo "OK: $OUT"
