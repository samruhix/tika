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
public class BasicContentHandlerFactory_handlerTypeName_11_0_Test {

    @Mock
    private ParseContext parseContext;

    private BasicContentHandlerFactory factory;

    @BeforeEach
    public void setUp() {
        factory = new BasicContentHandlerFactory();
        factory.setParseContext(parseContext);
    }

    @Test
    public void testHandlerTypeName() {
        String expected = BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN.name();
        String result = factory.handlerTypeName();
        assertEquals(expected, result);
    }

    @Test
    public void testHandlerTypeNameWithCustomType() {
        factory.setType(BasicContentHandlerFactory.HANDLER_TYPE.XML);
        String expected = BasicContentHandlerFactory.HANDLER_TYPE.XML.name();
        String result = factory.handlerTypeName();
        assertEquals(expected, result);
    }
}
