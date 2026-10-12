"""Summarize archived PIT XML and Surefire text reports without inferring results."""
import collections
import json
from pathlib import Path
import re
import xml.etree.ElementTree as ET

ROOT = Path(__file__).parent / "artifacts"
result = {}
for name in ("A-final-original", "B0-generated-passing", "B-generated", "C-manual"):
    xml = ROOT / name / "pit-reports" / "mutations.xml"
    if not xml.exists():
        continue
    mutations = ET.parse(xml).getroot().findall("mutation")
    counts = collections.Counter(m.attrib["status"] for m in mutations)
    test_counts = collections.Counter()
    for report in (ROOT / name / "surefire-reports").glob("*.txt"):
        match = re.search(r"Tests run: (\d+), Failures: (\d+), Errors: (\d+), Skipped: (\d+)",
                          report.read_text(encoding="utf-8", errors="replace"))
        if match:
            test_counts.update(dict(zip(("tests", "failures", "errors", "skipped"),
                                        map(int, match.groups()))))
    result[name] = {"mutants": len(mutations), "statuses": dict(counts),
                    "mutation_score": counts["KILLED"] / len(mutations),
                    "surefire": dict(test_counts)}
(ROOT / "suite-results.json").write_text(json.dumps(result, indent=2), encoding="utf-8")
print(json.dumps(result, indent=2))
