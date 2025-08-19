#!/bin/bash
set -euo pipefail

OUT="backend-dump.txt"
BK="."

# tenta localizar o backend
[ -d "./backend/src/main/java" ] && BK="./backend"
[ -d "./server/src/main/java" ] && BK="./server"

echo "# Backend Dump" > "$OUT"
echo "- Data: $(date)" >> "$OUT"
echo "- SO: $(uname -a)" >> "$OUT"
echo "- Java: $(java -version 2>&1 | head -n1)" >> "$OUT"
echo "- Raiz: $BK" >> "$OUT"

echo -e "\n===== Estrutura =====" >> "$OUT"
find "$BK/src" -maxdepth 3 -type d >> "$OUT" 2>/dev/null || true

echo -e "\n===== Build =====" >> "$OUT"
[ -f "$BK/pom.xml" ] && head -n 40 "$BK/pom.xml" >> "$OUT"
[ -f "$BK/build.gradle" ] && head -n 40 "$BK/build.gradle" >> "$OUT"

echo -e "\n===== Configs =====" >> "$OUT"
for f in $(find "$BK/src/main/resources" -maxdepth 1 -type f -name "application*" 2>/dev/null); do
  echo "--- $f ---" >> "$OUT"
  head -n 60 "$f" >> "$OUT"
done

echo -e "\n===== Controllers =====" >> "$OUT"
grep -RIn --include="*.java" "@RestController" "$BK/src/main/java" 2>/dev/null >> "$OUT" || true

echo -e "\n===== Endpoints =====" >> "$OUT"
grep -RIn --include="*.java" "@\(Get\|Post\|Put\|Delete\)Mapping" "$BK/src/main/java" 2>/dev/null >> "$OUT" || true

echo -e "\n===== Entities =====" >> "$OUT"
grep -RIn --include="*.java" "@Entity" "$BK/src/main/java" 2>/dev/null >> "$OUT" || true

echo "===== FIM =====" >> "$OUT"

echo "✅ Arquivo gerado: $OUT"
