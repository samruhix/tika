package org.apache.tika.sax;

import org.apache.tika.sax.BasicContentHandlerFactory;
import org.apache.tika.parser.ParseContext;
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
import org.apache.tika.config.OutputLimits;

@ExtendWith(MockitoExtension.class)
public class BasicContentHandlerFactory_hashCode_19_0_Test {

    @Mock
    private ParseContext parseContext;

    private BasicContentHandlerFactory factory;

    @BeforeEach
    public void setUp() {
        factory = new BasicContentHandlerFactory(BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, 1000, true, parseContext);
    }

    @Test
    public void testHashCode() {
        int hashCode = factory.hashCode();
        assertEquals(1000, hashCode);
    }

    @Test
    public void testHashCodeWithNullType() {
        factory.setType(null);
        int hashCode = factory.hashCode();
        assertEquals(1000, hashCode);
    }

    @Test
    public void testHashCodeWithDifferentType() {
        factory.setType(BasicContentHandlerFactory.HANDLER_TYPE.HTML);
        int hashCode = factory.hashCode();
        assertEquals(1000, hashCode);
    }

    @Test
    public void testHashCodeWithDifferentWriteLimit() {
        factory.setWriteLimit(2000);
        int hashCode = factory.hashCode();
        assertEquals(2000, hashCode);
    }

    @Test
    public void testHashCodeWithDifferentThrowOnWriteLimitReached() {
        factory.setThrowOnWriteLimitReached(false);
        int hashCode = factory.hashCode();
        assertEquals(1000, hashCode);
    }

    @Test
    public void testHashCodeWithDifferentValidateXHTML() {
        factory.setValidateXHTML(true);
        int hashCode = factory.hashCode();
        assertEquals(1000, hashCode);
    }
}
