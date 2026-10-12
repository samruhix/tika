"""Reproduce selection/repairs from immutable ChatUniTest exports; see root readme.md."""
import argparse
import json
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parent.parent
RAW = ROOT / "ift3913/artifacts/generated-raw/tika-parent/tika-core/org/apache/tika/sax"
DEST = ROOT / "tika-core/src/test/java/org/apache/tika/sax"
parser = argparse.ArgumentParser()
parser.add_argument("mode", choices=("passing", "repaired"))
mode = parser.parse_args().mode
HEADER = (ROOT / "tika-core/src/main/java/org/apache/tika/sax/BasicContentHandlerFactory.java").read_text().split("package ")[0]
REJECTED = {
    "newInstance": ["testNewInstanceWithNullParseContext"],
    "createHandler": ["testCreateHandlerWithParametersAndParseContextThrowsException"],
    "equals": ["testEqualsDifferentValidateXHTML", "testEqualsSame"],
}


def replace_method(text, name, body=None):
    match = re.search(r"    @Test\s+public void " + name + r"\([^)]*\)[^{]*\{", text)
    if not match:
        raise ValueError(name)
    start, depth, end = match.end(), 1, match.end()
    while depth:
        char = text[end]
        depth += (char == "{") - (char == "}")
        end += 1
    if body is None:
        return text[:match.start()] + text[end:]
    return text[:start] + "\n" + body + "\n    }" + text[end:]


inventory = []
for source in sorted(RAW.glob("*.java")):
    text = source.read_text(encoding="utf-8")
    kind = source.name.split("_")[1]
    if mode == "passing":
        if kind == "hashCode":
            continue
        for name in REJECTED.get(kind, []):
            text = replace_method(text, name)
    elif kind == "newInstance":
        text = replace_method(text, "testNewInstanceWithNullParseContext",
            "        assertThrows(IllegalArgumentException.class, () ->\n"
            "                BasicContentHandlerFactory.newInstance(\n"
            "                        BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, null));")
        stubs = """        when(parseContext.get(OutputLimits.class)).thenReturn(outputLimits);
        when(outputLimits.getWriteLimit()).thenReturn(100);
        when(outputLimits.isThrowOnWriteLimit()).thenReturn(true);
"""
        if stubs not in text:
            raise ValueError("Missing original stubs")
        text = text.replace(stubs, "", 1)
        text = text.replace("    @Test\n    public void testNewInstance() {",
                            "    private void configureLimits() {\n" + stubs + "    }\n\n"
                            "    @Test\n    public void testNewInstance() {\n        configureLimits();")
        for name in ("testNewInstanceWithDifferentWriteLimit", "testNewInstanceWithDifferentThrowOnWriteLimit"):
            text = text.replace("public void " + name + "() {", "public void " + name + "() {\n        configureLimits();")
    elif kind == "createHandler":
        text = replace_method(text, "testCreateHandlerWithParametersAndParseContextThrowsException",
            "        assertThrows(IllegalArgumentException.class, () ->\n"
            "                new BasicContentHandlerFactory(\n"
            "                        BasicContentHandlerFactory.HANDLER_TYPE.BODY, 1000, false, null));")
    elif kind == "equals":
        text = replace_method(text, "testEqualsDifferentValidateXHTML",
            "        BasicContentHandlerFactory other = new BasicContentHandlerFactory();\n"
            "        factory.setValidateXHTML(true);\n"
            "        assertFalse(factory.equals(other));")
        text = replace_method(text, "testEqualsSame",
            "        BasicContentHandlerFactory other = new BasicContentHandlerFactory();\n"
            "        assertTrue(factory.equals(other));")
    elif kind == "hashCode":
        cases = {"testHashCode": ("MARKDOWN", 1000, True, False),
            "testHashCodeWithNullType": (None, 1000, True, False),
            "testHashCodeWithDifferentType": ("HTML", 1000, True, False),
            "testHashCodeWithDifferentWriteLimit": ("MARKDOWN", 2000, True, False),
            "testHashCodeWithDifferentThrowOnWriteLimitReached": ("MARKDOWN", 1000, False, False),
            "testHashCodeWithDifferentValidateXHTML": ("MARKDOWN", 1000, True, True)}
        for name, (kind_value, limit, throwing, validate) in cases.items():
            match = re.search(r"public void " + name + r"\(\)\s*\{(.*?)\n    }", text, re.S)
            body = match.group(1)
            initial = "0" if kind_value is None else f"BasicContentHandlerFactory.HANDLER_TYPE.{kind_value}.hashCode()"
            expression = f"((31 * {initial} + {limit}) * 31 + {int(throwing)}) * 31 + {int(validate)}"
            body = re.sub(r"assertEquals\(\d+, hashCode\);", f"assertEquals({expression}, hashCode);", body)
            text = replace_method(text, name, body.strip("\n"))
    text = re.sub(r"^import .*;\n", "", text, flags=re.M)
    imports = """import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xml.sax.ContentHandler;

import org.apache.tika.config.OutputLimits;
import org.apache.tika.parser.ParseContext;
"""
    text = text.replace("package org.apache.tika.sax;", "package org.apache.tika.sax;\n\n" + imports)
    # Wrap the long constructor calls from the LLM without changing expressions.
    text = text.replace("new BasicContentHandlerFactory(BasicContentHandlerFactory.HANDLER_TYPE.",
                        "new BasicContentHandlerFactory(\n                BasicContentHandlerFactory.HANDLER_TYPE.")
    (DEST / source.name).write_text(HEADER + text, encoding="utf-8", newline="\n")
    inventory.append({"file": source.name, "tests": len(re.findall(r"@Test\b", text))})
(ROOT / f"ift3913/artifacts/generated-{mode}-inventory.json").write_text(
    json.dumps(inventory, indent=2), encoding="utf-8")
print(json.dumps(inventory, indent=2))
