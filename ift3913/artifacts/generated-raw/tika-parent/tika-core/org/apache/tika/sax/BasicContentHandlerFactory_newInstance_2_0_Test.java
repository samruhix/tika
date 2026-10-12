package org.apache.tika.sax;

import org.apache.tika.sax.BasicContentHandlerFactory;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.config.OutputLimits;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.tika.annotation.TikaComponent;

@ExtendWith(MockitoExtension.class)
public class BasicContentHandlerFactory_newInstance_2_0_Test {

    @Mock
    private ParseContext parseContext;

    @Mock
    private OutputLimits outputLimits;

    private BasicContentHandlerFactory factory;

    @BeforeEach
    public void setUp() {
        when(parseContext.get(OutputLimits.class)).thenReturn(outputLimits);
        when(outputLimits.getWriteLimit()).thenReturn(100);
        when(outputLimits.isThrowOnWriteLimit()).thenReturn(true);
        factory = new BasicContentHandlerFactory();
    }

    @Test
    public void testNewInstance() {
        BasicContentHandlerFactory result = BasicContentHandlerFactory.newInstance(BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, parseContext);
        assertNotNull(result);
        assertEquals(BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, result.getType());
        assertEquals(100, result.getWriteLimit());
        assertTrue(result.isThrowOnWriteLimitReached());
    }

    @Test
    public void testNewInstanceWithNullParseContext() {
        BasicContentHandlerFactory result = BasicContentHandlerFactory.newInstance(BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, null);
        assertNotNull(result);
        assertEquals(BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, result.getType());
        assertEquals(-1, result.getWriteLimit());
        assertFalse(result.isThrowOnWriteLimitReached());
    }

    @Test
    public void testNewInstanceWithDifferentWriteLimit() {
        when(outputLimits.getWriteLimit()).thenReturn(200);
        BasicContentHandlerFactory result = BasicContentHandlerFactory.newInstance(BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, parseContext);
        assertEquals(200, result.getWriteLimit());
    }

    @Test
    public void testNewInstanceWithDifferentThrowOnWriteLimit() {
        when(outputLimits.isThrowOnWriteLimit()).thenReturn(false);
        BasicContentHandlerFactory result = BasicContentHandlerFactory.newInstance(BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, parseContext);
        assertFalse(result.isThrowOnWriteLimitReached());
    }
}
