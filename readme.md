# IFT3913 — Tâche 3

## 1. Workflow : détecter les nouvelles vulnérabilités avec Semgrep

### Objectif

Le but est d'ajouter au workflow un job qui détecte, grâce à Semgrep, les nouvelles
vulnérabilités introduites par chaque commit, et qui fait échouer le build s'il en trouve.

En bref :
- un nouveau job `semgrep`, ajouté dans le même fichier que le job de tests de la tâche 2 ;
- un déclenchement élargi aux cinq modules du cas d'étude ;
- Semgrep exécuté dans son conteneur officiel ;
- une comparaison avec le commit précédent, pour ne bloquer que les vulnérabilités nouvelles.

### Choix de conception

#### Élargissement du filtre `paths`

Avant, le workflow ne se lançait que si `tika-core` changeait. Comme ce filtre s'applique à tout
le fichier, une vulnérabilité ajoutée dans un autre module n'aurait jamais été analysée. Le filtre
couvre maintenant les cinq modules du cas d'étude : `tika-core`, `tika-serialization`, et les
modules PDF, Microsoft et OCR.

#### Conteneur Semgrep

Le job s'exécute dans l'image Docker officielle `semgrep/semgrep`, où Semgrep est déjà installé.
On évite ainsi de l'installer à chaque exécution.

#### Comparaison avec un commit de référence

Tika contient déjà des alertes Semgrep. Si le build échouait dès qu'il y a une alerte, il serait
rouge sur chaque commit, même propre. Avec `--baseline-commit`, Semgrep compare le commit avec un
commit de référence et ne signale que les alertes nouvelles. La référence est :
- pour un push, le commit précédent ;
- pour une pull request, la base de la PR ;
- si aucun commit précédent n'existe (nouvelle branche ou lancement manuel), le point de
  séparation avec `tache2-final`.

#### `fetch-depth: 0`

Par défaut, GitHub ne télécharge que la dernière version du code. Ce réglage télécharge tout
l'historique, sinon le commit de référence n'existe pas sur la machine et la comparaison est
impossible.

#### `--config p/java`

C'est la liste de règles que Semgrep utilise pour chercher les failles : la liste officielle pour
Java, le langage de Tika.

#### `--error`

Sans cette option, Semgrep affiche les failles mais le job reste vert. Avec elle, le job échoue
dès qu'une nouvelle faille est trouvée : c'est ce qui bloque le build.

#### Sauvegarde du rapport

Le résultat de Semgrep est conservé comme fichier téléchargeable dans GitHub Actions, même quand
le job échoue.

### Validation

Pour valider la modification, nous avons poussé trois commits sur la branche `tache3` et observé
à chaque fois le résultat du job `semgrep` dans GitHub Actions.

#### Test 1 : commit sans vulnérabilité

Ce commit ne modifie que le fichier du workflow, sans aucun code Java. Il n'y a donc aucune
nouvelle vulnérabilité, et le job `semgrep` doit réussir. Résultat : le job a réussi ✅.

#### Test 2 : ajout d'une vulnérabilité

Nous avons ajouté dans `tika-core` une classe temporaire, `TestSemgrep.java`, qui chiffre des
données avec `Cipher.getInstance("DES/ECB/PKCS5Padding")`. DES est un chiffrement obsolète, et le
mode ECB laisse voir les motifs du message. Le job `semgrep` doit échouer. Résultat : Semgrep a
détecté deux nouvelles alertes sur cette ligne (`des-is-deprecated` et `ecb-cipher`), et le job a
échoué ❌. Le job `core-tests` a réussi sur le même commit : le code compilait et passait tous les
tests, mais le build a bien été bloqué.

#### Test 3 : suppression de la vulnérabilité

Nous avons supprimé `TestSemgrep.java`. Plus aucune nouvelle alerte n'existe, et le job `semgrep`
doit à nouveau réussir. Résultat : le job a réussi ✅.

Ces trois tests montrent que le job ne bloque le build que lorsqu'un commit ajoute une
vulnérabilité.

## 2. Rickroll en cas d'échec des tests

Le job `core-tests` exécute les tests de `tika-core`. Si un test unitaire échoue
(`failure() && steps.maven-tests.outcome == 'failure'`), l'action composite locale écrite en
Rust (`.github/actions/rickroll`) est compilée et exécutée. Elle affiche une animation ASCII dans
le terminal de la CI, ainsi qu'un GIF Rickroll dans le résumé GitHub Actions
(`$GITHUB_STEP_SUMMARY`).

## 3. Les 7 vulnérabilités introduites dans `tika-core`

Nous avons introduit 7 vulnérabilités de types différents, détectables par Semgrep (`p/java`),
dans des classes pertinentes de `tika-core` :

1. **Transmission en clair par socket non chiffré (CWE-319)** dans `ProcessUtils.java`
   (`openUnencryptedSocket`) : ouverture d'un `new Socket(host, port)` standard au lieu d'un
   `SSLSocket`, ce qui expose le trafic réseau à l'interception (règle `unencrypted-socket`).
2. **Entités externes XML / XXE (CWE-611)** dans `XMLReaderUtils.java`
   (`createUnsafeDocumentBuilder`) : création d'un `DocumentBuilderFactory` par défaut sans
   désactiver `disallow-doctype-decl`, ce qui expose aux attaques XXE (règle
   `documentbuilderfactory-disallow-doctype-decl-missing`).
3. **Hachage cryptographique faible (CWE-328)** dans `StringUtils.java` (`unsafeMd5Hash`) :
   utilisation de l'algorithme obsolète `MessageDigest.getInstance("MD5")`, vulnérable aux
   collisions (règle `use-of-md5`).
4. **Vecteur d'initialisation (IV) statique (CWE-329)** dans `Metadata.java` (`createStaticIv`) :
   création d'un `IvParameterSpec` avec un tableau d'octets constant, écrit en dur, au lieu d'un
   IV aléatoire, ce qui rend le chiffrement prévisible (règle `no-static-initialization-vector`).
5. **Injection SQL (CWE-89)** dans `Tika.java` (`queryDocumentUnsafe`) : concaténation directe du
   paramètre `docName` dans une requête SQL exécutée avec `Statement.executeQuery()`, au lieu
   d'un `PreparedStatement` (règle `formatted-sql-string`).
6. **Chiffrement faible en mode ECB (CWE-327)** dans `TikaInputStream.java`
   (`createWeakCipher`) : utilisation de `Cipher.getInstance("AES/ECB/PKCS5Padding")`, qui chiffre
   les blocs de façon déterministe, sans confidentialité sémantique (règle `use-of-aes-ecb`).
7. **Contexte SSL/TLS obsolète (CWE-326)** dans `FilenameUtils.java` (`createWeakSslContext`) :
   utilisation de `SSLContext.getInstance("SSL")`, un protocole déprécié, au lieu de `TLSv1.2`
   ou `TLSv1.3` (règle `weak-ssl-context`).

## Usage de l'IA

Nous avons utilisé Claude pour mieux comprendre GitHub Actions et Semgrep, et pour corriger
certaines erreurs.
