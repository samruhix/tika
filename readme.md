# IFT3913 — tâche 2 : BasicContentHandlerFactory

Travail local du 11 octobre 2026, sans push ni PR. Ce rapport distingue les mesures
exécutées des limites et des actions non exécutées. Le README Apache est conservé sans changement
de contenu dans `README.apache.md` : Windows ne permet pas de conserver deux fichiers
distincts `README.md` et `readme.md` dans ce répertoire.

## Résultat vérifié

Le code de production et les tests originaux sont inchangés. ChatUniTest a exporté
32 tests; 22 passent sans correction d'oracle. Après dix corrections documentées,
les 32 passent. Neuf tests manuels supplémentaires portent la suite tika-core à
790 tests, 0 échec, 0 erreur, 3 ignorés. Les 41 nouveaux tests passent tous.

| Suite | Tests tika-core | KILLED | SURVIVED | NO_COVERAGE | TIMED_OUT | Autres | Score (dénominateur 73) | Lignes PIT | Test Strength |
|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| A : originaux | 749 | 28 | 4 | 41 | 0 | 0 | 28/73 = 38,36 % | 50/98 | 28/32 = 87,50 % |
| B0 auxiliaire : originaux + 22 générés valides inchangés | 771 | 41 | 4 | 28 | 0 | 0 | 41/73 = 56,16 % | 66/98 | 41/45 = 91,11 % |
| B : originaux + 32 générés après réparations humaines | 781 | 54 | 1 | 18 | 0 | 0 | 54/73 = 73,97 % | 76/98 | 54/55 = 98,18 % |
| C : B + 9 tests manuels | 790 | 70 | 0 | 3 | 0 | 0 | 70/73 = 95,89 % | 92/98 | 70/70 = 100 % |

Pour chacune de ces suites validées : 0 échec, 0 erreur, 3 tests originaux ignorés.
Le score inclut les mutants sans couverture; Test Strength les exclut.
Les améliorations sont attribuées séparément : +13 KILLED dus aux sorties valides
du modèle, +13 après réparations humaines des oracles, +16 par les tests manuels.
La génération brute de 32 tests échoue : 8 failures, 2 errors, 22 réussites.
PIT n'est pas lancé sur cette suite brute défaillante; B0 est la mesure complémentaire
qui isole l'apport des tests déjà valides, et B signale explicitement les réparations.

PIT identique à chaque étape : 1.30.0, pitest-junit5-plugin 1.2.3,
targetClasses=org.apache.tika.sax.BasicContentHandlerFactory, threads=2,
timestampedReports=false, HTML et XML. Aucun opérateur personnalisé n'est ajouté :
les opérateurs par défaut, observés dans les mêmes 73 identités de mutants, sont
ConditionalsBoundaryMutator, NegateConditionalsMutator, MathMutator,
EmptyObjectReturnValsMutator, BooleanFalseReturnValsMutator,
BooleanTrueReturnValsMutator, NullReturnValsMutator et PrimitiveReturnsMutator.
La classe sélectionnée conserve son empreinte SHA-256, et aucune
modification de production n'apparaît dans git diff.

Rapports HTML/XML et Surefire : `ift3913/artifacts/A-final-original/`,
`B0-generated-passing/`, `B-generated/`, `C-manual/`. Logs vérifiés :
`A-final-tests.log`, `A-final-pit.log`, `B0-passing-tests-verified.log`,
`B0-passing-pit.log`, `B-generated-tests-verified.log`, `B-generated-pit.log`,
`C-manual-tests-verified.log`, `C-manual-pit.log`.
Tableaux JSON : `suite-results.json`; comparaisons de tous les mutants :
`A-to-B0.csv`, `B0-to-B.csv`, `B-to-C.csv`, `A-to-C.csv`.

JaCoCo initial : instructions 232/459 = 50,54 %, branches 24/63 = 38,10 %;
final C : instructions 428/459 = 93,25 %, branches 60/63 = 95,24 %,
lignes 92/98 = 93,88 %. Le fichier jacoco.exec est remis à zéro avant les
mesures B/C retenues. Le premier archivage A après plusieurs essais gardait
un fichier cumulatif : `A-final-original/jacoco-cumulative-not-baseline/` est
explicitement exclu des comparaisons. Le script de reproduction remet aussi
JaCoCo à zéro à chaque phase et reconfirme les compteurs initiaux 232/459 et 24/63.

Le rejeu complet `artifacts/replay-20261011-230439/` a réussi pour les quatre
phases. `replay-verification.json` et ses quatre CSV confirment zéro différence
de statut sur les mêmes 73 mutants pour chaque phase; `replay-coverage.json`
conserve les compteurs JaCoCo frais. Ce script de reproduction a donc été
exécuté et vérifié, et pas seulement proposé. Les sources finales ont été restaurées.
Empreinte avant/après de la classe :
B3E85806AADE9468AC979AAFC16459F6D714664E06674D068AA3C86F8ADE3687.

## Fichiers livrés

Modifiés : `tika-core/pom.xml` (PIT préexistant préservé; starter adapté et profil
ChatUniTest local), `.gitignore` (binaires et modèles sous ift3913/tools exclus).
README Apache renommé en `README.apache.md`, contenu identique à HEAD
(objet Git e9cdf4ba47b5a8fbc2fa82ac99d823c3d7dc357b); seul ce renommage a été
indexé par git mv. Aucun commit n'est créé.

Créés : `readme.md`, `.github/workflows/ift3913-core-tests.yml`, et six classes
sous `tika-core/src/test/java/org/apache/tika/sax/` :
BasicContentHandlerFactory_createHandler_4_0_Test,
BasicContentHandlerFactory_equals_18_0_Test,
BasicContentHandlerFactory_handlerTypeName_11_0_Test,
BasicContentHandlerFactory_hashCode_19_0_Test,
BasicContentHandlerFactory_newInstance_2_0_Test,
BasicContentHandlerFactoryManualTest.

Scripts : `ift3913/record-local-api.py`, `prepare-generated.py`,
`compare-mutations.ps1`, `summarize-results.py`, `write-mutation-analysis.py`,
`run-comparison.ps1`; preuves et rapports dans `ift3913/artifacts/`.
Toute la documentation du cours se trouve dans ce seul readme.md; les autres
fichiers sont des sources, scripts ou preuves brutes.

## État initial vérifié

La seule modification préexistante était la configuration PIT dans
`tika-core/pom.xml`; elle est préservée. Aucun AGENTS.md n'a été trouvé dans le dépôt.
L'accès au répertoire parent est restreint. Aucun code de production n'est modifié.
Empreinte initiale : `ift3913/artifacts/production-hash-before.json`.

Avant toute nouvelle compilation, copies de PIT HTML/XML, JaCoCo HTML/XML, rapports
Surefire et POM dans `ift3913/artifacts/initial/`. Les rapports Surefire textuels
totalisent 749 tests, 0 échec, 0 erreur, 3 ignorés; le test sélectionné contient
5 méthodes. Cette somme est une lecture des rapports historiques, pas une nouvelle
exécution des 749 tests. Aucun nouveau test source n'existait à la sauvegarde.

Nouvelle exécution PIT A réussie avec les seuls tests originaux, avant introduction
de tests supplémentaires : `ift3913/artifacts/initial/rerun-pit.log` et
`ift3913/artifacts/A-original/` (HTML/XML). Les classes de test supplémentaires
n'étaient pas encore compilées. 73 mutants : KILLED 28, SURVIVED 4,
NO_COVERAGE 41, TIMED_OUT 0, autres 0. Score : 28/73 = 38,36 %.
Test Strength : 28/(73−41) = 87,50 %. Couverture PIT : 50/98 lignes.
JaCoCo historique : instructions 232/(227+232) = 50,54 %;
branches 24/(39+24) = 38,10 %; lignes 50/98 = 51,02 %.

## Outillage et configuration locale

Java Temurin 21.0.11+10-LTS, Maven 3.9.12. Matériel inspecté :
34 224 566 272 octets de RAM; Ollama confirme RTX 3060 Ti, 8 Gio VRAM.
Ollama n'était ni dans PATH ni disponible sur localhost:11434 au départ.
Version portable téléchargée : 0.40.3; provenance dans
`ift3913/artifacts/ollama-release.json`; binaires/modèles dans
`ift3913/tools/`, ignorés par Git.

Documentation officielle consultée :
[ChatUniTest](https://github.com/ZJU-ACES-ISE/ChatUniTest),
[plugin Maven](https://github.com/ZJU-ACES-ISE/chatunitest-maven-plugin),
[API locale Ollama](https://docs.ollama.com/api/openai-compatibility),
[Qwen2.5-Coder 7B](https://ollama.com/library/qwen2.5-coder:7b).
Le plugin officiel accepte une URL personnalisée. Sa version 2.1.1 refuse
cependant `qwen2.5-coder:7b` (preuve : `chatunitest-parse.log`). L'alias
`code-llama` a été créé dans Ollama pour le modèle Qwen réellement chargé;
le nom accepté par le plugin ne désigne donc pas ici un modèle Code Llama.

Profil Maven `chatunitest-local`, explicitement activé seulement pour la génération :
`io.github.zju-aces-ise:chatunitest-maven-plugin:2.1.1`; clé factice
`ollama-local`; modèle API `code-llama`; URL
`http://127.0.0.1:11435/v1/chat/completions`. Le relais
`ift3913/record-local-api.py` transmet les corps sans transformation vers
`127.0.0.1:11434` et sauvegarde requêtes/réponses dans `artifacts/api-raw/`.
Paramètres : testNumber=1, maxRounds=3, temperature=0.2, topP=1,
enableMultithreading=false, enableMerge=false, noExecution=false,
maxPromptTokens=12000, maxResponseTokens=2048. Réparation par règles activée
par défaut; stopWhenSuccess=true. Descripteur réel archivé dans
`ift3913/artifacts/chatunitest-plugin.xml`.
OLLAMA_CONTEXT_LENGTH=16384; OLLAMA_HOST=127.0.0.1:11434;
OLLAMA_MODELS pointe vers `ift3913/tools/models`.

La tentative de goal `help` échoue car ce plugin n'en fournit pas;
elle a néanmoins téléchargé le plugin. Le goal `parse` avec le nom Qwen échoue
avant génération; la relance avec l'alias est documentée séparément.

## Commandes reproductibles

Le wrapper original échoue dans cette session à `(Get-Item $MAVEN_M2_PATH).Target[0]`
avec « Indexation impossible dans un tableau Null ». Il n'a pas été modifié.
Les exécutions utilisent directement la distribution identique déjà installée :

```powershell
$mvn = 'C:\Users\Gaming Pc\.m2\wrapper\dists\apache-maven-3.9.12\59fe215c0ad6947fea90184bf7add084544567b927287592651fda3782e0e798\bin\mvn.cmd'
& $mvn -pl tika-core test-compile org.pitest:pitest-maven:mutationCoverage '-DskipTests=false' '-Dmaven.test.skip=false'
$env:OLLAMA_MODELS = "$PWD/ift3913/tools/models"
$env:OLLAMA_HOST = '127.0.0.1:11434'
$env:OLLAMA_CONTEXT_LENGTH = '16384'
# Lancer serve dans un terminal distinct ou par Start-Process -WindowStyle Hidden.
& ./ift3913/tools/ollama-local/ollama.exe serve
& ./ift3913/tools/ollama-local/ollama.exe pull qwen2.5-coder:7b
& ./ift3913/tools/ollama-local/ollama.exe cp qwen2.5-coder:7b code-llama
# Lancer le relais dans un autre processus.
python ./ift3913/record-local-api.py
& $mvn -pl tika-core -Pchatunitest-local io.github.zju-aces-ise:chatunitest-maven-plugin:2.1.1:class '-DselectClass=org.apache.tika.sax.BasicContentHandlerFactory'
# Rejouer les quatre suites isolées depuis l'état final, sans relancer le modèle :
powershell -NoProfile -ExecutionPolicy Bypass -File ./ift3913/run-comparison.ps1 -Maven $mvn
# Analyser les rapports déjà archivés :
powershell -NoProfile -ExecutionPolicy Bypass -File ./ift3913/compare-mutations.ps1 -Before ./ift3913/artifacts/A-final-original/pit-reports/mutations.xml -After ./ift3913/artifacts/C-manual/pit-reports/mutations.xml -Output ./ift3913/artifacts/A-to-C.csv
python ./ift3913/summarize-results.py
```

run-comparison.ps1 sauvegarde les six sources supplémentaires, les retire
temporairement pour A, supprime les anciennes classes de test et rapports dans
des chemins vérifiés sous le dépôt, prépare B0 puis B puis C, et restaure les
sources sauvegardées dans un finally. Les sorties de chaque rejeu sont séparées
dans `artifacts/replay-<date-heure>/`. Il exécute les mêmes tests et la même
commande PIT sans changer le code de production. La génération ne fait pas
partie du rejeu ni de la CI. Arrêter toute compilation concurrente avant ce script.

## CI et déclaration d'IA

Workflow ajouté : `.github/workflows/ift3913-core-tests.yml`, Java 21,
tests tika-core et modules nécessaires avec skipTests=false et maven.test.skip=false.
Il ne lance pas la génération ni Ollama. Il sauvegarde les rapports Surefire.
Aucune exécution distante de ce nouveau workflow n'a été observée; aucun push
ni PR n'est effectué. Le workflow n'est pas déclaré vert.

Codex a inspecté les fichiers, préparé la configuration, les scripts, le workflow,
le présent rapport et des tests manuels explicitement identifiés. Seules les sorties
obtenues par le plugin ChatUniTest avec le serveur local pourront être qualifiées
de tests générés. Les tests manuels ne sont pas placés dans `generated-raw`.
Les limites, erreurs intermédiaires et mesures retenues sont documentées ci-dessous.

### Ajustements d'environnement observés

Le téléchargement, le chargement local et la sonde HTTP ont réussi :
`api-probe-response.json` contient une réponse « OK. » et le format
`choices[0].message.content`. Le modèle réel a 7,6 milliards de paramètres,
GGUF Q4_K_M, digest
`dae161e27b0e90dd1856c8bb3209201fd6736d8eb66298e75ed87571486f4364`.
`model-tags.json` prouve l'identité de l'alias et du tag Qwen;
`model-show.json` conserve les métadonnées complètes.
Après validation, le modèle a été déchargé et les processus serveur Ollama/relais
créés pour cette tâche ont été arrêtés. Le binaire portable et le modèle restent
dans ift3913/tools pour reproduction; les archives dupliquées ont été supprimées.

Première tentative interrompue explicitement après des erreurs d'import Mockito :
`chatunitest-generation-without-starter.log`, `attempt1-errors/`,
`chatunitest-info/tika-parent/tika-core/history2026_10_11_22_40_22/`.
Une dépendance test `io.github.ZJU-ACES-ISE:chatunitest-starter:1.4.0`,
type pom, a ensuite été ajoutée conformément à la documentation.
Ce changement n'est pas une correction du code des tests générés.
Le parent Tika gère les versions de plusieurs dépendances : les versions du
starter ne constituent donc pas seules la liste des versions effectivement résolues.
Installation réussie dans `install-with-starter.log`; nouvelle mesure A dans
`A-original-with-starter/` et `A-with-starter.log`, identique mutant par mutant
à A (CSV `A-dependency-comparison.csv`). Les phases B/C utilisent cet environnement.

Les requêtes effectivement envoyées par ChatUniTest contiennent model,
messages, temperature, max_tokens, frequency_penalty et presence_penalty.
`top_p` est absent malgré topP=1 configuré : ce paramètre ne peut pas être
présenté comme effectivement transmis. La sonde indépendante, elle, l'envoie.
La relance de génération est journalisée dans `chatunitest-generation-with-starter.log`.

Le starter ancien ajoute Byte Buddy 1.10.20 et Mockito Jupiter 3.8.0,
alors que le parent résout Mockito Core 5.23.0 et JUnit 6.1.3.
`dependency-tree.log` conserve cet état. Il ajoute aussi Vintage/runner JUnit 4 :
la première vérification indépendante choisissait JUnit4Provider et comptait
**zéro** test (`generated-first-independent.log`), malgré BUILD SUCCESS.
Ce résultat n'est pas considéré comme une validation.
Le POM final exclut Byte Buddy ancien, Vintage et runner du starter et déclare
Mockito Jupiter 5.23.0 et Byte Buddy/core-agent 1.17.7 en scope test.
Arbre final : `dependency-tree-aligned.log`. Avec ces dépendances,
`generated-first-aligned.log` exécute réellement les quatre tests du premier
fichier inchangé : trois réussissent, un lève une IllegalArgumentException.
L'identité SHA-256 des copies brute/compilée a été contrôlée avant corrections.
La génération déjà démarrée utilisait l'état précédent du starter; son annonce
de succès contredit les résumés JUnit (4/4 puis 11/11 tests échoués).
Les mesures indépendantes ultérieures servent donc de preuve pour les tests,
et non ces annonces du plugin.

## Génération et corrections humaines

La génération officielle complète a terminé avec BUILD SUCCESS en 6 min 45 s,
le 11 octobre à 22:50:07 (Toronto). Sept méthodes ont été ciblées; cinq ont
produit un fichier exporté. `parseHandlerType` et la surcharge streaming
`createHandler(OutputStream, Charset)` échouent encore à la compilation après
les rounds 0, 1 et 2. Les erreurs et candidats restent dans les historiques et
`error-message/`; aucune sortie n'a été inventée pour ces deux méthodes.
Les erreurs restantes des alias portent notamment sur HANDLER_TYPE non résolu;
le candidat streaming appelle directement createHandlerInner, qui est privé.
Les constructeurs, accesseurs et méthodes privées ont été ignorés par le plugin.

Sorties exportées : `ift3913/artifacts/generated-raw/tika-parent/tika-core/org/apache/tika/sax/`.
Inventaire des fichiers, nombres et SHA-256 : `generated-source-inventory.json`.
Historique de la tentative complète :
`chatunitest-info/tika-parent/tika-core/history2026_10_11_22_43_26/class276/`.
`api-raw/` contient vingt échanges (une sonde, sept appels de la tentative
interrompue, douze appels de la tentative complète).
La réparation **automatique** utile est celle de createHandler sans argument,
round 1 : le plugin a obtenu un candidat qui compile après l'échec du round 0.
Les imports/package/nom de classe ajoutés par ses règles ne sont pas attribués
à une intervention humaine. L'annonce de réussite d'exécution du plugin reste
non fiable dans cette session et n'est jamais utilisée pour valider les suites.

| Fichier généré (suffixe de BasicContentHandlerFactory_) | Tests | Réussites inchangées | Échecs d'assertion | Erreurs |
|---|---:|---:|---:|---:|
| createHandler_4_0_Test.java | 11 | 10 | 0 | 1 |
| equals_18_0_Test.java | 9 | 7 | 2 | 0 |
| handlerTypeName_11_0_Test.java | 2 | 2 | 0 | 0 |
| hashCode_19_0_Test.java | 6 | 0 | 6 | 0 |
| newInstance_2_0_Test.java | 4 | 3 | 0 | 1 |
| Total | 32 | 22 | 8 | 2 |

Ces nombres viennent de `generated-uncorrected.log` et
`generated-uncorrected-reports/`, après copie identique sans modification de
code; checkstyle/spotless sont désactivés uniquement pour cette mesure brute.
Aucun test n'est ignoré dans cette mesure des 32 tests.

B0 conserve les 22 méthodes qui réussissent, sans modifier leurs données ni
assertions; les dix méthodes invalides sont exclues de cette mesure auxiliaire.
Ses quatre sources exactes sont sauvegardées dans
`B0-generated-passing/sources/`. Ce filtrage constitue une sélection humaine,
pas une réparation automatique. La préparation est reproductible via
`python ift3913/prepare-generated.py passing`. Les modifications de forme
communes aux sources compilées sont : licence Apache, imports explicites,
suppression des imports inutilisés, ordre des imports, LF et repli des lignes
longues. Un premier contrôle de style échouait avec les CRLF; l'échec est gardé
dans `B0-passing-tests.log`, la relance réussie dans `B0-passing-tests-verified.log`.

B contient les 32 méthodes après les corrections humaines suivantes, appliquées
aux seules copies compilées par `python ift3913/prepare-generated.py repaired` :

| Test corrigé | Correction exacte et justification |
|---|---|
| newInstance.testNewInstanceWithNullParseContext | Remplacement des assertions de retour par assertThrows(IllegalArgumentException). OutputLimits.get(null) retourne une politique false; le constructeur interdit false avec contexte null. Les trois stubs du BeforeEach sont déplacés dans configureLimits(), appelé seulement dans les trois tests à contexte non null : la première correction révélait sinon UnnecessaryStubbingException. |
| createHandler.testCreateHandlerWithParametersAndParseContextThrowsException | Le constructeur est placé dans la lambda assertThrows : il lève l'exception avant createHandler(), donc l'ancien test n'atteignait pas son assertion. |
| equals.testEqualsDifferentValidateXHTML | L'autre objet utilise les mêmes valeurs par défaut; seul factory.validateXHTML passe à true; assertTrue devient assertFalse. L'ancien oracle comparait aussi des limites différentes et attendait une égalité erronée. |
| equals.testEqualsSame | L'autre objet est créé avec le constructeur par défaut, comme factory : sa limite −1 correspond enfin à celle de factory au lieu de 100. L'assertion true est conservée. |
| hashCode.testHashCode | 1000 remplacé par ((31 × MARKDOWN.hashCode + 1000) × 31 + 1) × 31 + 0. |
| hashCode.testHashCodeWithNullType | 1000 remplacé par ((31 × 0 + 1000) × 31 + 1) × 31 + 0 = 961031. |
| hashCode.testHashCodeWithDifferentType | 1000 remplacé par le même polynôme avec HTML.hashCode. |
| hashCode.testHashCodeWithDifferentWriteLimit | 2000 remplacé par le polynôme avec MARKDOWN et limite 2000. |
| hashCode.testHashCodeWithDifferentThrowOnWriteLimitReached | 1000 remplacé par le polynôme avec indicateur throw = 0. |
| hashCode.testHashCodeWithDifferentValidateXHTML | 1000 remplacé par le polynôme avec indicateur validate = 1. |

Les six oracles de hash sont des **réparations humaines**, fortement liées à
l'implémentation actuelle; les gains correspondants ne sont pas attribués au
modèle. Le premier essai B corrigé échoue sur le stub inutile
(`B-generated-tests.log`); la correction de portée des stubs est explicitée
ci-dessus et sa relance est conservée dans `B-generated-tests-verified.log`.

## Tests manuels supplémentaires

Ces neuf tests sont préparés hors des sources compilées puis introduits seulement
après la mesure B dans `BasicContentHandlerFactoryManualTest.java`. Leurs noms et intentions :

| Test | Données et comportement ciblé | Oracle et justification |
|---|---|---|
| aliasesAndFallbackAreExact | Les huit alias, en majuscules, locale turque; null et nom inconnu avec deux défauts différents | Égalité avec chaque enum attendu; ROOT doit rendre IGNORE indépendant du cas turc. Le défaut doit être conservé exactement. |
| beanPropertiesAndDefaults | Constructeur sans argument, chaque type, limite 7, booléens vrais et faux | Valeurs documentées MARKDOWN/−1/true/false puis lecture exacte des propriétés; vise les retours vides et booléens constants. |
| zeroLimitAndUnlimitedAreDistinct | TEXT, limites 0 et −1, un caractère x | 0 doit rejeter le caractère et conserver une sortie vide; −1 doit conserver x. La limite 0 distingue précisément le mutant de frontière `< 0` → `<= 0`, ligne 171. |
| validationWrapsBothCreationPaths | IGNORE, sorties mémoire et flux, validation désactivée puis activée; fermeture p sans ouverture | Wrapper présent seulement si demandé; SAXException pour une séquence invalide sur les deux chemins. |
| silentLimitRequiresContextAndTruncates | false sans contexte; puis contexte fourni par setter, limite 2, abcd puis ef | IllegalArgumentException sans contexte; sortie ab et conservation de ab après la seconde écriture. Les données distinguent troncature, écriture complète et poursuite indue. |
| contextLimitsAreTransferred | OutputLimits avec XML, limite 17, true puis false | Type, limite et politique transférés exactement par newInstance; 17 distingue une valeur configurée des valeurs par défaut. |
| equalityAndHashIncludeEveryConfigurationField | Objets identiques, même instance, null, String, sous-classe; un seul champ changé à la fois; type null | Égalité symétrique seulement pour même classe et mêmes champs. Hash vérifié contre le polynôme actuel; ce dernier oracle est volontairement lié à l'implémentation pour les mutants arithmétiques, plus strict que le seul contrat Java. |
| markdownMemoryAndStreamPreserveHeading | MARKDOWN sans limite, événement h1 contenant Title; flux limité à 0 | Sortie contenant # Title, identique en mémoire et UTF-8; wrapper et exception avec limite 0. Le marqueur Markdown distingue une sortie texte triviale. |
| xmlStreamsSerializeAtBothLimits | XML en flux, limites −1 et 100, événement h1 contenant Title | Type concret avec/sans wrapper; sérialisation contenant une balise h1 et Title suivi de sa fermeture. La limite 100 dépasse les cinq caractères du titre, donc aucune exception n'est attendue. Vise les retours null aux lignes 220 et 238. |

Les tests originaux vérifient principalement IGNORE/TEXT/HTML/XML/BODY, les grandes
sorties (>110000 caractères), la présence/absence de balises, les types de handlers
et le dépassement de limite. Ils ne couvrent pas les alias, l'égalité et le hash,
ni toutes les nouvelles propriétés. Les oracles de type seuls ne suffisent pas à
prouver le contenu extrait : les tests manuels combinent, où pertinent, type,
contenu exact et exception observable. L'oracle de hash a une limite explicitée
ci-dessus; on ne généralise pas « objets différents ⇒ hashes différents ».

La première exécution C échoue dans le test manuel XML avec « Namespace ... not
declared » : sa séquence SAX omettait startPrefixMapping. La correction manuelle
ajoute startPrefixMapping("", XHTML) avant h1 et endPrefixMapping("") après h1,
sans modifier les assertions ni le code de production. L'échec est conservé dans
`C-manual-tests.log`; la relance complète réussit dans `C-manual-tests-verified.log`.

Les oracles générés ont des qualités variables : handlerTypeName compare des
noms exacts et newInstance vérifie type/limite/politique; les nombreux assertNotNull
de createHandler ne testent ni contenu, ni validation, ni effet de la limite.
Le test de symétrie equals pourrait passer avec deux résultats false; certains
cas « DifferentType » ou « DifferentThrow » changent aussi la limite, ce qui
masque le champ réellement visé. Les tests hash bruts confondent hash et limite
d'écriture. Les tests manuels utilisent des configurations différant d'un seul
champ, des résultats observables et les frontières 0/−1. Leurs garanties sont
plus spécifiques mais l'oracle polynomial de hash reste couplé à l'implémentation.

## Limites et actions non exécutées

Aucun serveur distant ni API commerciale n'a généré les tests. Les sorties
ChatUniTest sont réelles, mais son validateur signale ici des succès malgré des
résumés JUnit en échec; Surefire indépendant tranche. La génération automatique
des alias et du streaming a échoué; les tests manuels couvrent ces comportements
et les candidats ratés restent disponibles pour étude. Aucun résultat de PR,
push, exécution GitHub Actions, ou validation de tous les modules Tika n'est revendiqué.
Il reste à observer la CI distante après un push ultérieur décidé par l'utilisateur.
Pour tenter à nouveau une méthode ratée, utiliser le goal officiel method avec
`-DselectMethod=BasicContentHandlerFactory#parseHandlerType` ou
`-DselectMethod=BasicContentHandlerFactory#createHandler`, un testOutput distinct
et éventuellement une limite maxResponseTokens accrue; ce sont des propositions,
pas des résultats déjà obtenus.

<!-- MUTATION_ANALYSIS_START -->
## Mutants comparés individuellement

L'identité utilise classe, méthode, descripteur JVM, ligne, opérateur, indexes et blocks; les CSV conservent ces clés complètes et tous les 73 mutants. Les identifiants Mxx suivent l'ordre du XML A, sans se limiter au numéro de ligne. Les tests responsables sont ceux effectivement indiqués par PIT à la transition concernée. L'explication décrit l'oracle; une exception parasite peut également tuer un mutant avant l'assertion, ce qui est signalé.

### A → B0 : génération valide sans correction d'oracle (13 nouveaux KILLED)

| ID | Méthode/ligne/index | Opérateur | Avant | Test responsable | Assertion ou comportement détecteur |
|---|---|---|---|---|---|
| M02 | <init>:81 / 50 | NegateConditionalsMutator | NO_COVERAGE | testEqualsDifferentThrowOnWriteLimitReached | Construction false avec contexte non null doit réussir; la mutation provoque une exception avant assertFalse(equals). |
| M29 | equals:314 / 5 | NegateConditionalsMutator | NO_COVERAGE | testEqualsNull | assertFalse(factory.equals(null)); un retour true ou une exception contredit ce cas null. |
| M30 | equals:317 / 14 | NegateConditionalsMutator | NO_COVERAGE | testEqualsNull | assertFalse(factory.equals(null)); un retour true ou une exception contredit ce cas null. |
| M31 | equals:317 / 19 | NegateConditionalsMutator | NO_COVERAGE | testEqualsDifferentClass | assertFalse(equals(new Object())); une exception de cast ou true viole cet appel valide. |
| M32 | equals:321 / 37 | NegateConditionalsMutator | NO_COVERAGE | testEqualsDifferentWriteLimit | assertFalse : les limites −1 et 200 distinguent les objets. |
| M36 | equals:315 / 9 | BooleanFalseReturnValsMutator | NO_COVERAGE | testEqualsReflexive | assertTrue(factory.equals(factory)); un retour false viole la réflexivité. |
| M37 | equals:318 / 24 | BooleanTrueReturnValsMutator | NO_COVERAGE | testEqualsNull | assertFalse(factory.equals(null)); un retour true ou une exception contredit ce cas null. |
| M38 | equals:321 / 60 | BooleanTrueReturnValsMutator | NO_COVERAGE | testEqualsDifferentWriteLimit | assertFalse : les limites −1 et 200 distinguent les objets. |
| M44 | getType:255 / 5 | NullReturnValsMutator | NO_COVERAGE | testNewInstance | Type MARKDOWN et politique true lus exactement, plus assertNotNull du résultat. |
| M46 | handlerTypeName:260 / 6 | EmptyObjectReturnValsMutator | SURVIVED | testHandlerTypeName | assertEquals(MARKDOWN.name(), résultat); une chaîne vide est incorrecte. |
| M57 | isThrowOnWriteLimitReached:293 / 5 | BooleanFalseReturnValsMutator | SURVIVED | testNewInstance | Type MARKDOWN et politique true lus exactement, plus assertNotNull du résultat. |
| M58 | isThrowOnWriteLimitReached:293 / 5 | BooleanTrueReturnValsMutator | SURVIVED | testNewInstanceWithDifferentThrowOnWriteLimit | assertFalse de la politique transférée; retour null du résultat entraîne une erreur avant l'assertion. |
| M63 | newInstance:116 / 21 | NullReturnValsMutator | NO_COVERAGE | testNewInstanceWithDifferentThrowOnWriteLimit | assertFalse de la politique transférée; retour null du résultat entraîne une erreur avant l'assertion. |
### B0 → B : réparations humaines des sorties générées (13 nouveaux KILLED)

| ID | Méthode/ligne/index | Opérateur | Avant | Test responsable | Assertion ou comportement détecteur |
|---|---|---|---|---|---|
| M33 | equals:321 / 42 | NegateConditionalsMutator | SURVIVED | testEqualsSame | Après réparation humaine, assertTrue pour deux configurations par défaut identiques. |
| M34 | equals:321 / 47 | NegateConditionalsMutator | SURVIVED | testEqualsDifferentValidateXHTML | Après réparation humaine, assertFalse quand seul validateXHTML diffère. |
| M35 | equals:321 / 52 | NegateConditionalsMutator | SURVIVED | testEqualsSame | Après réparation humaine, assertTrue pour deux configurations par défaut identiques. |
| M47 | hashCode:330 / 20 | MathMutator | NO_COVERAGE | testHashCodeWithDifferentType | Oracle humain : égalité avec le polynôme de hash pour HTML, limite 1000, true, false. |
| M48 | hashCode:330 / 23 | MathMutator | NO_COVERAGE | testHashCodeWithDifferentType | Oracle humain : égalité avec le polynôme de hash pour HTML, limite 1000, true, false. |
| M49 | hashCode:331 / 29 | MathMutator | NO_COVERAGE | testHashCodeWithDifferentType | Oracle humain : égalité avec le polynôme de hash pour HTML, limite 1000, true, false. |
| M50 | hashCode:331 / 40 | MathMutator | NO_COVERAGE | testHashCodeWithDifferentType | Oracle humain : égalité avec le polynôme de hash pour HTML, limite 1000, true, false. |
| M51 | hashCode:332 / 46 | MathMutator | NO_COVERAGE | testHashCodeWithDifferentType | Oracle humain : égalité avec le polynôme de hash pour HTML, limite 1000, true, false. |
| M52 | hashCode:332 / 57 | MathMutator | NO_COVERAGE | testHashCodeWithDifferentValidateXHTML | Oracle humain : polynôme de hash avec le dernier indicateur à 1. |
| M53 | hashCode:329 / 5 | NegateConditionalsMutator | NO_COVERAGE | testHashCodeWithDifferentType | Oracle humain : égalité avec le polynôme de hash pour HTML, limite 1000, true, false. |
| M54 | hashCode:331 / 32 | NegateConditionalsMutator | NO_COVERAGE | testHashCodeWithDifferentType | Oracle humain : égalité avec le polynôme de hash pour HTML, limite 1000, true, false. |
| M55 | hashCode:332 / 49 | NegateConditionalsMutator | NO_COVERAGE | testHashCodeWithDifferentType | Oracle humain : égalité avec le polynôme de hash pour HTML, limite 1000, true, false. |
| M56 | hashCode:333 / 62 | PrimitiveReturnsMutator | NO_COVERAGE | testHashCodeWithDifferentType | Oracle humain : égalité avec le polynôme de hash pour HTML, limite 1000, true, false. |
### B → C : neuf tests manuels supplémentaires (16 nouveaux KILLED)

| ID | Méthode/ligne/index | Opérateur | Avant | Test responsable | Assertion ou comportement détecteur |
|---|---|---|---|---|---|
| M05 | createHandlerInner:171 / 48 | ConditionalsBoundaryMutator | SURVIVED | zeroLimitAndUnlimitedAreDistinct | À limite 0, assertThrows(SAXException) pour x et sortie vide; le mutant autorise x. |
| M16 | createHandlerInner:204 / 12 | NullReturnValsMutator | NO_COVERAGE | validationWrapsBothCreationPaths | Le handler IGNORE en flux doit exister et toString() être vide; null provoque une erreur lors de toString(). |
| M20 | createHandlerInner:220 / 100 | NullReturnValsMutator | NO_COVERAGE | xmlStreamsSerializeAtBothLimits | Type XML/wrapper selon limite, puis balise h1 et Title</h1> dans le flux; null échoue dès le contrôle de type. |
| M21 | createHandlerInner:223 / 119 | NullReturnValsMutator | NO_COVERAGE | markdownMemoryAndStreamPreserveHeading | Type Markdown/wrapper et sortie # Title identique mémoire/flux; null échoue au contrôle de type. |
| M26 | createHandlerInner:238 / 191 | NullReturnValsMutator | NO_COVERAGE | xmlStreamsSerializeAtBothLimits | Type XML/wrapper selon limite, puis balise h1 et Title</h1> dans le flux; null échoue dès le contrôle de type. |
| M27 | createHandlerInner:240 / 202 | NullReturnValsMutator | NO_COVERAGE | markdownMemoryAndStreamPreserveHeading | Type Markdown/wrapper et sortie # Title identique mémoire/flux; null échoue au contrôle de type. |
| M59 | isValidateXHTML:101 / 5 | BooleanFalseReturnValsMutator | NO_COVERAGE | beanPropertiesAndDefaults | isValidateXHTML doit être false par défaut et true après setter : chaque retour constant échoue. |
| M60 | isValidateXHTML:101 / 5 | BooleanTrueReturnValsMutator | NO_COVERAGE | beanPropertiesAndDefaults | isValidateXHTML doit être false par défaut et true après setter : chaque retour constant échoue. |
| M65 | parseHandlerType:132 / 8 | NullReturnValsMutator | NO_COVERAGE | aliasesAndFallbackAreExact | assertEquals avec enum attendu pour chaque alias et défaut exact pour null/inconnu; retour null est distingué. |
| M66 | parseHandlerType:138 / 104 | NullReturnValsMutator | NO_COVERAGE | aliasesAndFallbackAreExact | assertEquals avec enum attendu pour chaque alias et défaut exact pour null/inconnu; retour null est distingué. |
| M67 | parseHandlerType:140 / 109 | NullReturnValsMutator | NO_COVERAGE | aliasesAndFallbackAreExact | assertEquals avec enum attendu pour chaque alias et défaut exact pour null/inconnu; retour null est distingué. |
| M68 | parseHandlerType:142 / 114 | NullReturnValsMutator | NO_COVERAGE | aliasesAndFallbackAreExact | assertEquals avec enum attendu pour chaque alias et défaut exact pour null/inconnu; retour null est distingué. |
| M69 | parseHandlerType:144 / 119 | NullReturnValsMutator | NO_COVERAGE | aliasesAndFallbackAreExact | assertEquals avec enum attendu pour chaque alias et défaut exact pour null/inconnu; retour null est distingué. |
| M70 | parseHandlerType:146 / 124 | NullReturnValsMutator | NO_COVERAGE | aliasesAndFallbackAreExact | assertEquals avec enum attendu pour chaque alias et défaut exact pour null/inconnu; retour null est distingué. |
| M71 | parseHandlerType:148 / 129 | NullReturnValsMutator | NO_COVERAGE | aliasesAndFallbackAreExact | assertEquals avec enum attendu pour chaque alias et défaut exact pour null/inconnu; retour null est distingué. |
| M73 | parseHandlerType:153 / 139 | NullReturnValsMutator | NO_COVERAGE | aliasesAndFallbackAreExact | assertEquals avec enum attendu pour chaque alias et défaut exact pour null/inconnu; retour null est distingué. |

### Trois mutants restants

| ID | Ligne | Statut observé | Analyse de l'accessibilité |
|---|---:|---|---|
| M22 | 226 | NO_COVERAGE | default de la surcharge streaming : IGNORE retourne avant le switch; BODY/TEXT/HTML/XML/MARKDOWN ont chacun un case explicite. |
| M28 | 242 | NO_COVERAGE | default de la surcharge streaming : IGNORE retourne avant le switch; BODY/TEXT/HTML/XML/MARKDOWN ont chacun un case explicite. |
| M43 | 193 | NO_COVERAGE | default de getFormatHandler : BODY et IGNORE retournent avant cet appel; TEXT/HTML/XML/MARKDOWN ont chacun un case explicite. |

Ces trois mutations remplacent un retour par null dans une branche default. L'enum actuel possède exactement six valeurs; null déclenche une NullPointerException au switch, sans atteindre default. Elles paraissent donc équivalentes pour les chemins de l'API publique avec l'enum actuel. Cette conclusion est relative à ce domaine : une invocation réflexive directe de getFormatHandler avec BODY/IGNORE ou une future valeur de l'enum changerait l'analyse. Il ne s'agit pas d'une preuve globale d'équivalence, ni de mutants tués. Ils restent NO_COVERAGE dans le dénominateur 73 du score. Aucun TIMED_OUT, NON_VIABLE, RUN_ERROR, MEMORY_ERROR ou autre statut n'a été observé dans les mesures retenues.
<!-- MUTATION_ANALYSIS_END -->
