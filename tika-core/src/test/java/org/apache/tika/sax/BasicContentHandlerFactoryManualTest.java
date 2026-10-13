/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.tika.sax;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import org.apache.tika.config.OutputLimits;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BasicContentHandlerFactory.HANDLER_TYPE;

/** Tests manuels, distincts des sorties ChatUniTest. */
public class BasicContentHandlerFactoryManualTest {
    @Test
    void aliasesAndFallbackAreExact() {
        String[][] aliases = {{"xml", "XML"}, {"text", "TEXT"}, {"txt", "TEXT"},
                {"html", "HTML"}, {"body", "BODY"}, {"ignore", "IGNORE"},
                {"markdown", "MARKDOWN"}, {"md", "MARKDOWN"}};
        Locale saved = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            for (String[] alias : aliases) {
                assertEquals(HANDLER_TYPE.valueOf(alias[1]),
                        BasicContentHandlerFactory.parseHandlerType(
                                alias[0].toUpperCase(Locale.ROOT), HANDLER_TYPE.BODY));
            }
            assertEquals(HANDLER_TYPE.HTML,
                    BasicContentHandlerFactory.parseHandlerType(null, HANDLER_TYPE.HTML));
            assertEquals(HANDLER_TYPE.XML,
                    BasicContentHandlerFactory.parseHandlerType("unknown", HANDLER_TYPE.XML));
        } finally {
            Locale.setDefault(saved);
        }
    }

    @Test
    void beanPropertiesAndDefaults() {
        BasicContentHandlerFactory f = new BasicContentHandlerFactory();
        assertEquals(HANDLER_TYPE.MARKDOWN, f.getType());
        assertEquals(-1, f.getWriteLimit());
        assertTrue(f.isThrowOnWriteLimitReached());
        assertFalse(f.isValidateXHTML());
        for (HANDLER_TYPE type : HANDLER_TYPE.values()) {
            f.setType(type);
            assertEquals(type, f.getType());
            assertEquals(type.name(), f.handlerTypeName());
        }
        f.setWriteLimit(7);
        assertEquals(7, f.getWriteLimit());
        f.setThrowOnWriteLimitReached(false);
        assertFalse(f.isThrowOnWriteLimitReached());
        f.setValidateXHTML(true);
        assertTrue(f.isValidateXHTML());
    }

    @Test
    void zeroLimitAndUnlimitedAreDistinct() throws Exception {
        BasicContentHandlerFactory zero = new BasicContentHandlerFactory(HANDLER_TYPE.TEXT, 0);
        ContentHandler h = zero.createHandler();
        assertThrows(SAXException.class, () -> h.characters(new char[]{'x'}, 0, 1));
        assertEquals("", h.toString());
        ContentHandler unlimited = new BasicContentHandlerFactory(HANDLER_TYPE.TEXT, -1)
                .createHandler();
        unlimited.characters(new char[]{'x'}, 0, 1);
        assertEquals("x", unlimited.toString());
    }

    @Test
    void validationWrapsBothCreationPaths() throws Exception {
        BasicContentHandlerFactory f = new BasicContentHandlerFactory(HANDLER_TYPE.IGNORE, -1);
        assertFalse(f.createHandler() instanceof StrictXHTMLValidator);
        assertEquals("", f.createHandler(new ByteArrayOutputStream(), UTF_8).toString());
        assertFalse(f.createHandler(new ByteArrayOutputStream(), UTF_8)
                instanceof StrictXHTMLValidator);
        f.setValidateXHTML(true);
        ContentHandler memory = f.createHandler();
        ContentHandler stream = f.createHandler(new ByteArrayOutputStream(), UTF_8);
        assertTrue(memory instanceof StrictXHTMLValidator);
        assertTrue(stream instanceof StrictXHTMLValidator);
        assertThrows(SAXException.class, () -> memory.endElement("", "p", "p"));
        assertThrows(SAXException.class, () -> stream.endElement("", "p", "p"));
    }

    @Test
    void silentLimitRequiresContextAndTruncates() throws Exception {
        assertThrows(IllegalArgumentException.class,
                () -> new BasicContentHandlerFactory(HANDLER_TYPE.TEXT, 2, false, null));
        ParseContext context = new ParseContext();
        BasicContentHandlerFactory f = new BasicContentHandlerFactory(HANDLER_TYPE.TEXT, 2);
        f.setThrowOnWriteLimitReached(false);
        f.setParseContext(context);
        ContentHandler h = f.createHandler();
        h.characters("abcd".toCharArray(), 0, 4);
        assertEquals("ab", h.toString());
        h.characters("ef".toCharArray(), 0, 2);
        assertEquals("ab", h.toString());
    }

    @Test
    void contextLimitsAreTransferred() {
        ParseContext context = new ParseContext();
        OutputLimits limits = new OutputLimits();
        limits.setWriteLimit(17);
        limits.setThrowOnWriteLimit(true);
        context.set(OutputLimits.class, limits);
        BasicContentHandlerFactory f = BasicContentHandlerFactory.newInstance(HANDLER_TYPE.XML,
                context);
        assertEquals(HANDLER_TYPE.XML, f.getType());
        assertEquals(17, f.getWriteLimit());
        assertTrue(f.isThrowOnWriteLimitReached());
        limits.setThrowOnWriteLimit(false);
        assertFalse(BasicContentHandlerFactory.newInstance(HANDLER_TYPE.TEXT, context)
                .isThrowOnWriteLimitReached());
    }

    @Test
    void equalityAndHashIncludeEveryConfigurationField() {
        BasicContentHandlerFactory a = new BasicContentHandlerFactory(HANDLER_TYPE.TEXT, 7);
        BasicContentHandlerFactory b = new BasicContentHandlerFactory(HANDLER_TYPE.TEXT, 7);
        assertEquals(a, a);
        assertEquals(a, b);
        assertEquals(b, a);
        assertNotEquals(a, null);
        assertNotEquals(a, "factory");
        assertNotEquals(a, new BasicContentHandlerFactory(HANDLER_TYPE.TEXT, 7) { });
        assertEquals(expectedHash(a), a.hashCode());
        assertEquals(a.hashCode(), b.hashCode());
        b.setWriteLimit(8);
        assertNotEquals(a, b);
        b.setWriteLimit(7);
        b.setType(HANDLER_TYPE.HTML);
        assertNotEquals(a, b);
        b.setType(HANDLER_TYPE.TEXT);
        b.setThrowOnWriteLimitReached(false);
        assertNotEquals(a, b);
        assertEquals(expectedHash(b), b.hashCode());
        b.setThrowOnWriteLimitReached(true);
        b.setValidateXHTML(true);
        assertNotEquals(a, b);
        assertEquals(expectedHash(b), b.hashCode());
        b.setType(null);
        assertEquals(expectedHash(b), b.hashCode());
    }

    private int expectedHash(BasicContentHandlerFactory f) {
        int type = f.getType() == null ? 0 : f.getType().hashCode();
        return ((31 * type + f.getWriteLimit()) * 31
                + (f.isThrowOnWriteLimitReached() ? 1 : 0)) * 31
                + (f.isValidateXHTML() ? 1 : 0);
    }

    @Test
    void markdownMemoryAndStreamPreserveHeading() throws Exception {
        BasicContentHandlerFactory f = new BasicContentHandlerFactory(HANDLER_TYPE.MARKDOWN, -1);
        ContentHandler memory = f.createHandler();
        assertTrue(memory instanceof ToMarkdownContentHandler);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ContentHandler stream = f.createHandler(out, UTF_8);
        assertTrue(stream instanceof ToMarkdownContentHandler);
        for (ContentHandler h : new ContentHandler[]{memory, stream}) {
            h.startDocument();
            h.startElement("http://www.w3.org/1999/xhtml", "h1", "h1", new AttributesImpl());
            h.characters("Title".toCharArray(), 0, 5);
            h.endElement("http://www.w3.org/1999/xhtml", "h1", "h1");
            h.endDocument();
        }
        assertTrue(memory.toString().contains("# Title"));
        assertEquals(memory.toString(), out.toString(UTF_8));
        f.setWriteLimit(0);
        ContentHandler limitedStream = f.createHandler(new ByteArrayOutputStream(), UTF_8);
        assertTrue(limitedStream instanceof WriteOutContentHandler);
        assertThrows(SAXException.class,
                () -> limitedStream.characters(new char[]{'x'}, 0, 1));
    }

    @Test
    void xmlStreamsSerializeAtBothLimits() throws Exception {
        for (int limit : new int[]{-1, 100}) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ContentHandler h = new BasicContentHandlerFactory(HANDLER_TYPE.XML, limit)
                    .createHandler(out, UTF_8);
            assertTrue(limit < 0 ? h instanceof ToXMLContentHandler
                    : h instanceof WriteOutContentHandler);
            h.startDocument();
            h.startPrefixMapping("", "http://www.w3.org/1999/xhtml");
            h.startElement("http://www.w3.org/1999/xhtml", "h1", "h1", new AttributesImpl());
            h.characters("Title".toCharArray(), 0, 5);
            h.endElement("http://www.w3.org/1999/xhtml", "h1", "h1");
            h.endPrefixMapping("");
            h.endDocument();
            String xml = out.toString(UTF_8);
            assertTrue(xml.contains("<h1"));
            assertTrue(xml.contains("Title</h1>"));
        }
    }
}
