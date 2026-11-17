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
nouvelle vulnérabilité, et le job `semgrep` doit réussir. Résultat : le job a réussi ✅ (commit `c1b62d9`).

![Validation workflow Semgrep - Succès](screenshots/1-workflow-succes.png)

#### Test 2 : ajout d'une vulnérabilité

Nous avons ajouté dans `tika-core` une classe temporaire, `TestSemgrep.java`, qui chiffre des
données avec `Cipher.getInstance("DES/ECB/PKCS5Padding")`. DES est un chiffrement obsolète, et le
mode ECB laisse voir les motifs du message. Le job `semgrep` doit échouer. Résultat : Semgrep a
détecté deux nouvelles alertes sur cette ligne (`des-is-deprecated` et `ecb-cipher`), et le job a
échoué ❌ (commit `53dd29b`). Le job `core-tests` a réussi sur le même commit : le code compilait et passait tous les
tests, mais le build a bien été bloqué.

![Validation workflow Semgrep - Échec](screenshots/2-workflow-echec.png)

#### Test 3 : suppression de la vulnérabilité

Nous avons supprimé `TestSemgrep.java`. Plus aucune nouvelle alerte n'existe, et le job `semgrep`
doit à nouveau réussir. Résultat : le job a réussi ✅ (commit `9f614d9fd`).

Ces trois tests montrent que le job ne bloque le build que lorsqu'un commit ajoute une
vulnérabilité.

---

## 2. Les 7 vulnérabilités introduites dans `tika-core`

Nous avons ajouté 7 vulnérabilités de types différents dans `tika-core` pour vérifier que Semgrep (`p/java`) les détecte bien. Pour chaque faille, nous avons choisi une classe qui avait un lien logique avec ce que fait la méthode :

1. **Socket non chiffré (`ProcessUtils.java`)** : cette classe sert à gérer les processus et les communications locales. On y a ajouté la méthode `openUnencryptedSocket` qui ouvre un `new Socket(host, port)` normal au lieu d'un `SSLSocket`. C'est vulnérable parce que les données envoyées sur le réseau ne sont pas chiffrées et peuvent être interceptées en clair. Semgrep la bloque avec la règle `unencrypted-socket`.

2. **Entités externes XML / XXE (`XMLReaderUtils.java`)** : c'est la classe qui configure tous les parseurs XML de Tika. On a ajouté `createUnsafeDocumentBuilder` qui crée un `DocumentBuilderFactory.newInstance()` par défaut sans désactiver les entités externes (`disallow-doctype-decl`). Un fichier XML malveillant pourrait s'en servir pour lire des fichiers locaux sur la machine. Semgrep la bloque avec `documentbuilderfactory-disallow-doctype-decl-missing`.

3. **Hachage faible MD5 (`StringUtils.java`)** : comme cette classe contient des fonctions utilitaires pour manipuler du texte, on y a mis `unsafeMd5Hash` qui calcule un hash avec `MessageDigest.getInstance("MD5")`. L'algorithme MD5 est aujourd'hui obsolète car il est facile de générer des collisions. Semgrep la bloque avec `use-of-md5`.

4. **Vecteur d'initialisation statique (`Metadata.java`)** : cette classe gère les métadonnées des documents. On a ajouté `createStaticIv` qui crée un `IvParameterSpec` avec une suite d'octets fixe écrite directement dans le code, au lieu d'utiliser de l'aléatoire. Si on utilise toujours le même vecteur d'initialisation, le chiffrement devient prévisible. Semgrep la bloque avec `no-static-initialization-vector`.

5. **Injection SQL (`Tika.java`)** : c'est la classe principale pour interroger et analyser des documents. Dans `queryDocumentUnsafe`, on a construit une requête SQL en concaténant directement la variable `docName` avec des `+` dans `stmt.executeQuery(...)` au lieu d'utiliser une requête préparée (`PreparedStatement`). Un utilisateur pourrait injecter du code SQL dans le nom du document. Semgrep la bloque avec `formatted-sql-string`.

6. **Chiffrement faible AES/ECB (`TikaInputStream.java`)** : cette classe gère la lecture des flux de fichiers. On y a ajouté `createWeakCipher` qui utilise `Cipher.getInstance("AES/ECB/PKCS5Padding")`. Le problème du mode ECB est que deux blocs de données identiques donnent exactement le même bloc chiffré, ce qui ne cache pas bien l'information. Semgrep la bloque avec `use-of-aes-ecb` et `ecb-cipher`.

7. **Protocole SSL obsolète (`FilenameUtils.java`)** : cette classe traite les noms de fichiers et les URLs. On a ajouté `createWeakSslContext` qui appelle `SSLContext.getInstance("SSL")`. Le protocole SSL est trop vieux et possède des failles connues, il faut utiliser TLS (comme `TLSv1.2` ou `TLSv1.3`) à la place. Semgrep la bloque avec `weak-ssl-context`.

---

## 3. Comparaison des rapports Semgrep (Avant vs Après)

Avant nos modifications (sur le commit de base `98a33b42e`), le scan différentiel de Semgrep ne signalait aucune nouvelle vulnérabilité. Il y avait déjà une vieille alerte dans `XMLReaderUtils.java`, mais grâce à l'option `--baseline-commit`, Semgrep l'a ignorée puisque ce n'était pas nous qui l'avions ajoutée. Le job `semgrep` passait donc au vert.

Après avoir ajouté nos 7 vulnérabilités dans les 7 fichiers de `tika-core` (commit `691e14e`), Semgrep a analysé uniquement nos changements et a trouvé toutes les nouvelles failles d'un coup. Comme l'option `--error` est activée, ces alertes sont considérées comme bloquantes et le job `semgrep` a échoué comme prévu.

![Exécution Semgrep sur les 7 vulnérabilités](screenshots/4-semgrep-7-vulns.png)

---

## 4. Rickroll en cas d'échec des tests (`core-tests`)

Dans le job `core-tests` qui lance les tests unitaires de `tika-core`, nous avons ajouté une étape qui se déclenche seulement si un test échoue (`failure() && steps.maven-tests.outcome == 'failure'`). Elle compile et lance notre petite action locale en Rust (`.github/actions/rickroll`), qui affiche un Rickroll en ASCII directement dans les logs du terminal ainsi qu'un GIF dans le résumé de GitHub Actions (testé sur le commit `0ea3ee2`).

![Rickroll GitHub Actions](screenshots/3-rickroll.png)

---

## Usage de l'IA

Nous avons utilisé l'IA pour mieux comprendre GitHub Actions et Semgrep, et pour corriger
certaines erreurs.

