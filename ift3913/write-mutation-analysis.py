"""Render the individual, observed mutant transitions into the single course report."""
import csv
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parent.parent
ART = ROOT / "ift3913/artifacts"
all_rows = list(csv.DictReader((ART / "A-to-C.csv").open(encoding="utf-8-sig")))
ids = {row["Key"]: f"M{i:02d}" for i, row in enumerate(all_rows, 1)}
oracles = {
    "testEqualsDifferentThrowOnWriteLimitReached": "Construction false avec contexte non null doit réussir; la mutation provoque une exception avant assertFalse(equals).",
    "testEqualsNull": "assertFalse(factory.equals(null)); un retour true ou une exception contredit ce cas null.",
    "testEqualsDifferentClass": "assertFalse(equals(new Object())); une exception de cast ou true viole cet appel valide.",
    "testEqualsDifferentWriteLimit": "assertFalse : les limites −1 et 200 distinguent les objets.",
    "testEqualsReflexive": "assertTrue(factory.equals(factory)); un retour false viole la réflexivité.",
    "testNewInstance": "Type MARKDOWN et politique true lus exactement, plus assertNotNull du résultat.",
    "testHandlerTypeName": "assertEquals(MARKDOWN.name(), résultat); une chaîne vide est incorrecte.",
    "testNewInstanceWithDifferentThrowOnWriteLimit": "assertFalse de la politique transférée; retour null du résultat entraîne une erreur avant l'assertion.",
    "testEqualsSame": "Après réparation humaine, assertTrue pour deux configurations par défaut identiques.",
    "testEqualsDifferentValidateXHTML": "Après réparation humaine, assertFalse quand seul validateXHTML diffère.",
    "testHashCodeWithDifferentType": "Oracle humain : égalité avec le polynôme de hash pour HTML, limite 1000, true, false.",
    "testHashCodeWithDifferentValidateXHTML": "Oracle humain : polynôme de hash avec le dernier indicateur à 1.",
    "zeroLimitAndUnlimitedAreDistinct": "À limite 0, assertThrows(SAXException) pour x et sortie vide; le mutant autorise x.",
    "validationWrapsBothCreationPaths": "Le handler IGNORE en flux doit exister et toString() être vide; null provoque une erreur lors de toString().",
    "xmlStreamsSerializeAtBothLimits": "Type XML/wrapper selon limite, puis balise h1 et Title</h1> dans le flux; null échoue dès le contrôle de type.",
    "markdownMemoryAndStreamPreserveHeading": "Type Markdown/wrapper et sortie # Title identique mémoire/flux; null échoue au contrôle de type.",
    "beanPropertiesAndDefaults": "isValidateXHTML doit être false par défaut et true après setter : chaque retour constant échoue.",
    "aliasesAndFallbackAreExact": "assertEquals avec enum attendu pour chaque alias et défaut exact pour null/inconnu; retour null est distingué.",
}
parts = ["\n<!-- MUTATION_ANALYSIS_START -->\n## Mutants comparés individuellement\n\n",
    "L'identité utilise classe, méthode, descripteur JVM, ligne, opérateur, indexes et blocks; "
    "les CSV conservent ces clés complètes et tous les 73 mutants. Les identifiants Mxx "
    "suivent l'ordre du XML A, sans se limiter au numéro de ligne. "
    "Les tests responsables sont ceux effectivement indiqués par PIT à la transition "
    "concernée. L'explication décrit l'oracle; une exception parasite peut également "
    "tuer un mutant avant l'assertion, ce qui est signalé.\n\n"]
for filename, title in (("A-to-B0.csv", "A → B0 : génération valide sans correction d'oracle"),
                        ("B0-to-B.csv", "B0 → B : réparations humaines des sorties générées"),
                        ("B-to-C.csv", "B → C : neuf tests manuels supplémentaires")):
    rows = [r for r in csv.DictReader((ART / filename).open(encoding="utf-8-sig"))
            if r["NewlyKilled"] == "True"]
    parts.append(f"### {title} ({len(rows)} nouveaux KILLED)\n\n")
    parts.append("| ID | Méthode/ligne/index | Opérateur | Avant | Test responsable | Assertion ou comportement détecteur |\n"
                 "|---|---|---|---|---|---|\n")
    for row in rows:
        method = re.search(r"\[method:([^\]]+)\]", row["KillingTest"]).group(1)
        name = method.split("(")[0]
        if name not in oracles:
            raise ValueError("Missing oracle: " + name)
        index = row["Key"].split("|")[-2]
        operator = row["Mutator"].rsplit(".", 1)[-1]
        parts.append(f"| {ids[row['Key']]} | {row['Method']}:{row['Line']} / {index} | "
                     f"{operator} | {row['Before']} | {name} | {oracles[name]} |\n")
parts.append("\n### Trois mutants restants\n\n"
    "| ID | Ligne | Statut observé | Analyse de l'accessibilité |\n|---|---:|---|---|\n")
for row in all_rows:
    if row["After"] != "KILLED":
        reason = ("default de getFormatHandler : BODY et IGNORE retournent avant cet appel; "
                  "TEXT/HTML/XML/MARKDOWN ont chacun un case explicite." if row["Line"] == "193"
                  else "default de la surcharge streaming : IGNORE retourne avant le switch; "
                  "BODY/TEXT/HTML/XML/MARKDOWN ont chacun un case explicite.")
        parts.append(f"| {ids[row['Key']]} | {row['Line']} | {row['After']} | {reason} |\n")
parts.append("\nCes trois mutations remplacent un retour par null dans une branche default. "
    "L'enum actuel possède exactement six valeurs; null déclenche une NullPointerException "
    "au switch, sans atteindre default. Elles paraissent donc équivalentes pour les "
    "chemins de l'API publique avec l'enum actuel. Cette conclusion est relative à ce "
    "domaine : une invocation réflexive directe de getFormatHandler avec BODY/IGNORE "
    "ou une future valeur de l'enum changerait l'analyse. Il ne s'agit pas d'une preuve "
    "globale d'équivalence, ni de mutants tués. Ils restent NO_COVERAGE dans le "
    "dénominateur 73 du score. Aucun TIMED_OUT, NON_VIABLE, RUN_ERROR, MEMORY_ERROR "
    "ou autre statut n'a été observé dans les mesures retenues.\n"
    "<!-- MUTATION_ANALYSIS_END -->\n")
report = ROOT / "readme.md"
text = report.read_text(encoding="utf-8")
text = re.sub(r"\n<!-- MUTATION_ANALYSIS_START -->.*?<!-- MUTATION_ANALYSIS_END -->\n", "", text, flags=re.S)
report.write_text(text + "".join(parts), encoding="utf-8", newline="\n")
print("Rendered individual mutant transitions into readme.md")
