# IFT3913 — Tâche 2 : génération de tests et analyse de mutation

**Classe étudiée :** `org.apache.tika.sax.BasicContentHandlerFactory` (module `tika-core`)
**Branche :** [`tache2-final`](https://github.com/samruhix/tika/tree/tache2-final)

Le README original d'Apache Tika a été renommé `README.apache.md`, car Windows ne
permet pas d'avoir à la fois `README.md` et `readme.md` dans le même répertoire.

## Résumé

| Suite | Tests | KILLED | SURVIVED | NO_COVERAGE | Score de mutation |
|---|---:|---:|---:|---:|---:|
| A : tests originaux | 749 | 28 | 4 | 41 | 28/73 = **38,4 %** |
| B0 : A + 22 tests générés valides (non modifiés) | 771 | 41 | 4 | 28 | 41/73 = **56,2 %** |
| B : A + 32 tests générés (après corrections) | 781 | 54 | 1 | 18 | 54/73 = **74,0 %** |
| C : B + 9 tests manuels | 790 | 70 | 0 | 3 | 70/73 = **95,9 %** |

Couverture JaCoCo des instructions : 50,5 % au départ (A), 93,3 % à la fin (C).
Toutes les suites passent : 0 échec, 0 erreur, 3 tests originaux ignorés.
Le code de production n'a pas été modifié.

---

## 1. Choix de la classe

`BasicContentHandlerFactory` crée les handlers SAX (texte, HTML, XML, Markdown…)
utilisés par les parseurs Tika. Elle a déjà un fichier de tests,
`BasicContentHandlerFactoryTest` (5 méthodes), mais celui-ci ne couvre pas toute la
classe :

- **Couverture initiale :** 232/459 instructions (50,5 %), 24/63 branches (38,1 %).
- **Méthodes non couvertes :** `equals`, `hashCode`, `parseHandlerType` (les alias),
  `newInstance`, `isValidateXHTML`, `getType` et la surcharge en flux
  `createHandler(OutputStream, Charset)`.
- **Mutants vivants :** sur 73 mutants, 4 survivent (M05, M46, M57, M58) et 41 ne
  sont pas couverts.

La classe remplit donc les deux conditions de l'énoncé : elle a des tests, mais pas
une couverture de 100 %, et elle a des mutants vivants.

## 2. Installation de ChatUniTest

ChatUniTest est intégré à Maven par un profil dans `tika-core/pom.xml`, qu'on active
seulement pour la génération :

```xml
<profile>
  <id>chatunitest-local</id>
  <!-- io.github.zju-aces-ise:chatunitest-maven-plugin:2.1.1 -->
  <model>code-llama</model>
  <url>http://127.0.0.1:11435/v1/chat/completions</url>
  <selectClass>org.apache.tika.sax.BasicContentHandlerFactory</selectClass>
  <testNumber>1</testNumber> <maxRounds>3</maxRounds> <temperature>0.2</temperature>
</profile>
```

- **Modèle :** Qwen2.5-Coder 7B, exécuté localement avec **Ollama** sur un GPU
  RTX 3060 Ti. La version 2.1.1 du plugin refuse le nom `qwen2.5-coder:7b`. Le
  modèle a donc été copié sous l'alias `code-llama`, un nom que le plugin accepte.
  Ce n'est **pas** le modèle Code Llama.
- **Dépendance de test :** `chatunitest-starter:1.4.0`, comme le demande la
  documentation. Ses anciennes versions de Byte Buddy et de JUnit Vintage ont été
  exclues : avec JUnit Vintage, Surefire choisissait le moteur JUnit 4 et exécutait
  **0 test** tout en affichant BUILD SUCCESS. Mockito Jupiter 5.23.0 et
  Byte Buddy 1.17.7 sont déclarés explicitement.
- **Relais :** un petit proxy Python (`ift3913/record-local-api.py`) transmet les
  requêtes à Ollama et enregistre les échanges dans `ift3913/artifacts/api-raw/`.

Commande de génération :

```
mvn -pl tika-core -Pchatunitest-local io.github.zju-aces-ise:chatunitest-maven-plugin:2.1.1:class -DselectClass=org.apache.tika.sax.BasicContentHandlerFactory
```

## 3. Tests générés

**Emplacement :** `tika-core/src/test/java/org/apache/tika/sax/BasicContentHandlerFactory_*_Test.java`
(5 fichiers). Les sorties brutes du plugin, avant toute correction, sont dans
`ift3913/artifacts/generated-raw/`.

Le plugin a ciblé 7 méthodes et produit 5 fichiers. Pour `parseHandlerType` et
`createHandler(OutputStream, Charset)`, le code généré ne compilait toujours pas
après 3 rondes de réparation : le modèle utilisait un symbole inexistant
(`HANDLER_TYPE` non résolu) ou appelait une méthode privée.

### Compilent-ils et s'exécutent-ils sans intervention ?

- **Compilation :** oui, après des modifications de forme exigées par Checkstyle :
  en-tête de licence Apache, imports explicites et triés, fins de ligne LF et lignes
  coupées. Ces modifications ne changent pas la logique des tests.
- **Exécution :** non. Sur 32 tests, **22 passent** tels quels, **8 échouent** et
  **2 produisent une erreur**.

| Fichier | Tests | Passent tels quels | Échecs | Erreurs |
|---|---:|---:|---:|---:|
| `createHandler_4_0` | 11 | 10 | 0 | 1 |
| `equals_18_0` | 9 | 7 | 2 | 0 |
| `handlerTypeName_11_0` | 2 | 2 | 0 | 0 |
| `hashCode_19_0` | 6 | 0 | 6 | 0 |
| `newInstance_2_0` | 4 | 3 | 0 | 1 |
| **Total** | **32** | **22** | **8** | **2** |

### Les 10 corrections manuelles

| Test | Correction |
|---|---|
| `newInstance.testNewInstanceWithNullParseContext` | Le test attendait un objet avec les valeurs par défaut. Il attend maintenant `IllegalArgumentException`, car le constructeur refuse `throwOnWriteLimitReached=false` avec un contexte `null`. Les stubs ont aussi été déplacés dans `configureLimits()`, pour éviter `UnnecessaryStubbingException`. |
| `createHandler.testCreateHandlerWithParametersAndParseContextThrowsException` | Le constructeur a été déplacé à l'intérieur de `assertThrows` : l'exception était levée avant l'assertion. |
| `equals.testEqualsDifferentValidateXHTML` | Le test attendait l'égalité alors que `validateXHTML` diffère. L'assertion devient `assertFalse`, et l'autre objet utilise les valeurs par défaut pour que seul ce champ diffère. |
| `equals.testEqualsSame` | L'autre objet avait une limite de 100 au lieu de −1, la valeur par défaut. Il est maintenant créé avec le constructeur par défaut. |
| `hashCode` (6 tests) | L'oracle `hashCode() == writeLimit` a été remplacé par le vrai calcul `((31·type + limite)·31 + throw)·31 + validate`. |

## 4. Critique des tests générés

### Une erreur commune : la limite par défaut

Plusieurs oracles faux ont la même cause : le modèle croit que la limite par défaut
est **100**, alors qu'elle vaut **−1** (illimitée). Cette erreur explique
`testEqualsSame` et les données de `testEqualsDifferentType` et de
`testEqualsDifferentThrowOnWriteLimitReached`. Le modèle invente une valeur
« plausible » au lieu de lire l'état réel de l'objet.

### `createHandler_4_0` : 11 tests presque triviaux (0 mutant tué)

- Les 11 tests se terminent par `assertNotNull(handler)`. Aucun ne vérifie le type
  du handler, le texte produit, l'effet de la limite ou la validation XHTML.
- Trois tests (`PositiveWriteLimit`, `NoThrowOnWriteLimitReached`,
  `NoFormatHandler`) utilisent **exactement la même configuration**. Le nom
  `NoFormatHandler` est aussi trompeur : MARKDOWN a bien un handler de format.
- Sept tests appellent la méthode **privée** `createHandlerInner` par réflexion. Ils
  sont fragiles : un simple renommage les casse.
- Ils ne tuent aucun nouveau mutant. Les mutants du chemin en mémoire étaient déjà
  tués par les tests originaux, qui vérifient le contenu produit. Le chemin en flux
  n'est jamais atteint.

Ces tests augmentent la couverture sans vraiment vérifier le comportement.

### `equals_18_0` : bons cas limites, données mal isolées

- **Bon :** les tests de réflexivité, de `null` et d'une autre classe sont précis.
  Ils tuent 5 mutants qui n'étaient pas couverts (M29, M30, M31, M36, M37).
- **Symétrie trop faible :** `equals(a,b) == equals(b,a)` est vrai aussi si les deux
  appels retournent `false`. Un `equals` qui retournerait toujours `false` passerait
  ce test.
- **Deux champs changés à la fois :** `testEqualsDifferentType` change le type
  **et** la limite. Un mutant qui ignore le type survivrait donc, puisque la limite
  suffit à rendre les objets différents.
- **Oracle faux :** le modèle a supposé que `validateXHTML` ne compte pas dans
  `equals`, ce qui est faux.

### `handlerTypeName_11_0` : simple et juste (1 mutant tué)

L'oracle `assertEquals(MARKDOWN.name(), result)` est exact. Il tue M46, qui
survivait aux tests originaux. Le cas `type == null` n'est pas testé ; dans ce cas,
la méthode lève une `NullPointerException`.

### `hashCode_19_0` : oracle inventé (0 mutant avant correction)

- Les 6 tests attendaient `hashCode() == writeLimit`. Les 6 échouent.
- L'oracle se contredit : il attend la même valeur (1000) pour deux types
  différents.
- Après correction humaine, ces tests tuent 10 mutants (M47 à M56). Ce mérite
  revient à la correction, pas au modèle. L'oracle corrigé reste aussi très lié à
  l'implémentation : une réécriture avec `Objects.hash` le casserait.

### `newInstance_2_0` : bon usage des mocks (4 mutants tués)

Le mock de `OutputLimits` isole bien la méthode, et les assertions vérifient
exactement le type, la limite et la politique. Ces tests tuent M44, M57, M58 et M63.
Le cas `null` était faux : le modèle a suivi la Javadoc (« default values are
used ») sans voir la contrainte du constructeur.

### Tests générés contre tests écrits à la main

| Aspect | Tests générés | Tests écrits à la main |
|---|---|---|
| Source de l'oracle | Valeur supposée (`assertNotNull`, `hash == limite`, limite 100) | Spécification (Javadoc, contrat Java) ou calcul |
| Spécificité | Faible pour `createHandler`, bonne pour les accesseurs | Contenu exact, type concret, exception précise |
| Données | Plusieurs champs changés à la fois | Un seul champ changé par cas |
| Valeurs limites | Aucune | Limites 0 et −1 |
| Fiabilité | 10 tests sur 32 faux | Fiables (un seul ajustement SAX) |

**Conclusion :** ChatUniTest est utile pour les méthodes à contrat simple (`equals`,
accesseurs). Il est peu fiable dès que l'oracle dépend de l'état réel de l'objet ou
d'un calcul, et il tombe dans les vérifications triviales quand le comportement est
riche. Aucun test généré ne devrait être gardé sans relecture.

## 5. Analyse de mutation avec PIT

PIT 1.30.0 a été ajouté à `tika-core/pom.xml`, avec `pitest-junit5-plugin` 1.2.3, la
cible `BasicContentHandlerFactory` et les opérateurs par défaut. La même
configuration a servi pour les quatre suites.

```
mvn -pl tika-core test-compile org.pitest:pitest-maven:mutationCoverage -DskipTests=false -Dmaven.test.skip=false
```

Les rapports HTML et XML sont dans `ift3913/artifacts/` : `A-final-original/`,
`B0-generated-passing/`, `B-generated/` et `C-manual/`.

Score avec les **tests originaux : 38,4 %**. Avec les **tests générés : 56,2 %**
sans correction, puis **74,0 %** après corrections.

**Les tests générés ne détectent pas tous les mutants :** même après correction,
1 mutant survit (M05) et 18 ne sont pas couverts.

## 6. Mutants détectés par les tests générés

### Sans correction (A → B0) : +13 mutants tués

| Mutant | Méthode:ligne | Mutation | Test qui le tue | Pourquoi il est détecté |
|---|---|---|---|---|
| M02 | constructeur:81 | condition inversée | `testEqualsDifferentThrowOnWriteLimitReached` | La construction valide (`false` avec contexte) lève maintenant une exception. |
| M29, M30 | equals:314, 317 | condition inversée | `testEqualsNull` | `equals(null)` doit retourner `false`. |
| M31 | equals:317 | condition inversée | `testEqualsDifferentClass` | Comparer avec un `Object` doit retourner `false`, sans exception. |
| M32, M38 | equals:321 | condition inversée / retour `true` | `testEqualsDifferentWriteLimit` | Les limites −1 et 200 doivent rendre les objets différents. |
| M36 | equals:315 | retour `false` | `testEqualsReflexive` | `x.equals(x)` doit retourner `true`. |
| M37 | equals:318 | retour `true` | `testEqualsNull` | Retourner `true` pour `null` viole le contrat. |
| M44 | getType:255 | retour `null` | `testNewInstance` | Le type MARKDOWN est vérifié exactement. |
| M46 | handlerTypeName:260 | retour `""` | `testHandlerTypeName` | Le nom attendu est `"MARKDOWN"`. |
| M57, M58 | isThrowOnWriteLimitReached:293 | retour `false` / `true` | `testNewInstance`, `…DifferentThrowOnWriteLimit` | La politique est vérifiée à `true` puis à `false`. |
| M63 | newInstance:116 | retour `null` | `testNewInstanceWithDifferentThrowOnWriteLimit` | L'appel sur le résultat lève une exception. |

### Après corrections (B0 → B) : +13 mutants tués

| Mutant | Méthode:ligne | Test qui le tue | Pourquoi il est détecté |
|---|---|---|---|
| M33, M35 | equals:321 | `testEqualsSame` | Deux objets par défaut identiques doivent être égaux. |
| M34 | equals:321 | `testEqualsDifferentValidateXHTML` | Seul `validateXHTML` diffère, donc les objets doivent être différents. |
| M47–M56 | hashCode:329–333 | `testHashCode*` | Le hash est comparé au calcul exact : changer un `*`, un `+` ou une condition change la valeur. |

## 7. Tests ajoutés manuellement

Fichier : `tika-core/src/test/java/org/apache/tika/sax/BasicContentHandlerFactoryManualTest.java`
(9 tests). Ils tuent les **16 mutants restants** qu'on peut atteindre par l'API
publique.

| Test | Intention | Données choisies et pourquoi | Oracle | Mutants tués |
|---|---|---|---|---|
| `aliasesAndFallbackAreExact` | Vérifier la conversion des alias en types | Les 8 alias en majuscules, avec la locale turque, plus `null` et un nom inconnu | Chaque alias donne l'enum documentée ; un nom inconnu donne la valeur par défaut fournie | M65–M71, M73 |
| `beanPropertiesAndDefaults` | Vérifier les valeurs par défaut et les accesseurs | Constructeur sans argument, puis setters à `true` et `false` | Valeurs documentées : MARKDOWN, −1, `true`, `false` | M59, M60 |
| `zeroLimitAndUnlimitedAreDistinct` | Distinguer la limite 0 de « illimité » (−1) | Limites 0 et −1, un seul caractère `x` | Avec 0, `x` est rejeté ; avec −1, `x` est conservé | M05 (`<` → `<=`) |
| `validationWrapsBothCreationPaths` | Vérifier la validation XHTML en mémoire et en flux | Une balise `</p>` fermée sans avoir été ouverte | Une `SAXException` seulement quand la validation est active | M16 |
| `silentLimitRequiresContextAndTruncates` | Vérifier la troncature silencieuse | Limite 2, texte `abcd` puis `ef` | La sortie vaut exactement `ab` | — |
| `contextLimitsAreTransferred` | Vérifier que `newInstance` lit `OutputLimits` | Limite 17, une valeur différente des valeurs par défaut | Type, limite et politique transférés exactement | — |
| `equalityAndHashIncludeEveryConfigurationField` | Vérifier que `equals` et `hashCode` utilisent chaque champ | Un seul champ changé à la fois | Objets différents ; même hash pour des objets égaux | — |
| `markdownMemoryAndStreamPreserveHeading` | Vérifier la sortie Markdown | Un `<h1>Title</h1>` | La sortie contient `# Title`, identique en mémoire et en flux | M21, M27 |
| `xmlStreamsSerializeAtBothLimits` | Vérifier la sortie XML en flux | Limites −1 et 100 (100 dépasse les 5 caractères du titre) | `<h1…>Title</h1>` est présent, sans exception | M20, M26 |

### Les 3 mutants restants (NO_COVERAGE)

M22 (ligne 226), M28 (ligne 242) et M43 (ligne 193) remplacent un retour par `null`
dans une branche `default` de `switch`. Chacune des 6 valeurs de l'enum a son propre
`case` ou un retour anticipé, donc la branche `default` n'est jamais atteinte. Ces
mutants sont **équivalents** pour l'API publique actuelle.

## 8. Exécution dans GitHub Actions

Workflow : `.github/workflows/ift3913-core-tests.yml` (Java 21,
`./mvnw -pl tika-core -am test`).

✅ **Succès** sur la branche `tache2-final` (commit `226ab5c`), qui contient les
32 tests générés et les 9 tests manuels :
[run #3 — 38433158752](https://github.com/samruhix/tika/actions/runs/38433158752).
Les rapports Surefire sont téléversés comme artefact `core-test-reports`.

## Déclaration d'utilisation de l'IA

En plus de ChatUniTest, un assistant de code (Codex) a aidé à préparer la
configuration, les scripts, le workflow, ce rapport et les tests manuels. Seules les
sorties du plugin ChatUniTest sont appelées « tests générés ».
