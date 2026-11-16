# IFT3913 — Tâche 3 :

## 1. Workflow GitHub Actions (Semgrep & Rickroll)
- **Job `semgrep`** : Analyse différentielle (`semgrep scan --config p/java --baseline-commit ... --error`) qui fait échouer le build dès qu'un commit introduit une nouvelle vulnérabilité, et exporte `semgrep-report.json` en artefact.
  - Validation : Échec confirmé avec une vulnérabilité de test (commit `53dd29be1`) et retour au vert après suppression (commit `9f614d9fd`).
- **Job `core-tests` & Action `rickroll`** : Exécute les tests de `tika-core`. En cas d'échec d'un test unitaire (`failure() && steps.maven-tests.outcome == 'failure'`), l'action composite locale en Rust (`.github/actions/rickroll`) compile et affiche une animation ASCII dans le terminal CI ainsi qu'un GIF Rickroll dans le résumé GitHub Actions (`$GITHUB_STEP_SUMMARY`).

## 2. Les 7 vulnérabilités introduites dans `tika-core`
Nous avons introduit 7 vulnérabilités de types distincts détectables par Semgrep (`p/java`) dans des classes pertinentes de `tika-core` :

1. **Transmission en clair par Socket non chiffré (CWE-319)** dans `ProcessUtils.java` (`openUnencryptedSocket`) : ouverture d'un `new Socket(host, port)` standard au lieu d'un `SSLSocket`, exposant le trafic réseau à l'interception (règle `unencrypted-socket`).
2. **Entités externes XML / XXE (CWE-611)** dans `XMLReaderUtils.java` (`createUnsafeDocumentBuilder`) : instanciation d'un `DocumentBuilderFactory` par défaut sans désactiver `disallow-doctype-decl`, exposant aux attaques XXE (règle `documentbuilderfactory-disallow-doctype-decl-missing`).
3. **Hachage cryptographique faible (CWE-328)** dans `StringUtils.java` (`unsafeMd5Hash`) : utilisation de l'algorithme obsolète `MessageDigest.getInstance("MD5")`, vulnérable aux collisions (règle `use-of-md5`).
4. **Vecteur d'initialisation (IV) statique (CWE-329)** dans `Metadata.java` (`createStaticIv`) : initialisation d'un `IvParameterSpec` avec un tableau d'octets constant codé en dur au lieu d'un IV aléatoire, rendant le chiffrement prévisible (règle `no-static-initialization-vector`).
5. **Injection SQL (CWE-89)** dans `Tika.java` (`queryDocumentUnsafe`) : concaténation directe du paramètre `docName` dans une requête SQL exécutée via `Statement.executeQuery()` au lieu d'un `PreparedStatement` (règle `formatted-sql-string`).
6. **Chiffrement faible en mode ECB (CWE-327)** dans `TikaInputStream.java` (`createWeakCipher`) : utilisation de `Cipher.getInstance("AES/ECB/PKCS5Padding")`, qui chiffre les blocs de façon déterministe sans confidentialité sémantique (règle `use-of-aes-ecb`).
7. **Contexte SSL/TLS obsolète (CWE-326)** dans `FilenameUtils.java` (`createWeakSslContext`) : utilisation de `SSLContext.getInstance("SSL")` (protocole déprécié) au lieu de `TLSv1.2` ou `TLSv1.3` (règle `weak-ssl-context`).
