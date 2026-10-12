package org.apache.tika.sax;

import org.apache.tika.sax.BasicContentHandlerFactory;
import org.apache.tika.parser.ParseContext;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.tika.annotation.TikaComponent;
import org.apache.tika.config.OutputLimits;

@ExtendWith(MockitoExtension.class)
public class BasicContentHandlerFactory_equals_18_0_Test {

    @Mock
    private ParseContext parseContext;

    private BasicContentHandlerFactory factory;

    @BeforeEach
    public void setUp() {
        factory = new BasicContentHandlerFactory();
        factory.setParseContext(parseContext);
    }

    @Test
    public void testEqualsReflexive() {
        assertTrue(factory.equals(factory));
    }

    @Test
    public void testEqualsSymmetric() {
        BasicContentHandlerFactory other = new BasicContentHandlerFactory();
        assertTrue(factory.equals(other) == other.equals(factory));
    }

    @Test
    public void testEqualsNull() {
        assertFalse(factory.equals(null));
    }

    @Test
    public void testEqualsDifferentClass() {
        assertFalse(factory.equals(new Object()));
    }

    @Test
    public void testEqualsDifferentType() {
        BasicContentHandlerFactory other = new BasicContentHandlerFactory(BasicContentHandlerFactory.HANDLER_TYPE.XML, 100);
        assertFalse(factory.equals(other));
    }

    @Test
    public void testEqualsDifferentWriteLimit() {
        BasicContentHandlerFactory other = new BasicContentHandlerFactory(BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, 200);
        assertFalse(factory.equals(other));
    }

    @Test
    public void testEqualsDifferentThrowOnWriteLimitReached() {
        BasicContentHandlerFactory other = new BasicContentHandlerFactory(BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, 100, false, parseContext);
        assertFalse(factory.equals(other));
    }

    @Test
    public void testEqualsDifferentValidateXHTML() {
        BasicContentHandlerFactory other = new BasicContentHandlerFactory(BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, 100, true, parseContext);
        factory.setValidateXHTML(true);
        assertTrue(factory.equals(other));
    }

    @Test
    public void testEqualsSame() {
        BasicContentHandlerFactory other = new BasicContentHandlerFactory(BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, 100, true, parseContext);
        assertTrue(factory.equals(other));
    }
}
