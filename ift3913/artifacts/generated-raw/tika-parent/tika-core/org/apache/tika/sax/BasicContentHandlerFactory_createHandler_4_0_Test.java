package org.apache.tika.sax;

import org.apache.tika.parser.ParseContext;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.tika.annotation.TikaComponent;
import org.apache.tika.config.OutputLimits;
import java.lang.reflect.Method;
import org.mockito.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BasicContentHandlerFactory_createHandler_4_0_Test {

    @Mock
    private ParseContext parseContext;

    private BasicContentHandlerFactory factory;

    @BeforeEach
    public void setUp() {
        factory = new BasicContentHandlerFactory();
    }

    @Test
    public void testCreateHandler() throws Exception {
        ContentHandler handler = factory.createHandler();
        assertNotNull(handler);
    }

    @Test
    public void testCreateHandlerWithParameters() throws Exception {
        factory = new BasicContentHandlerFactory(BasicContentHandlerFactory.HANDLER_TYPE.BODY, 1000);
        ContentHandler handler = factory.createHandler();
        assertNotNull(handler);
    }

    @Test
    public void testCreateHandlerWithParametersAndParseContext() throws Exception {
        factory = new BasicContentHandlerFactory(BasicContentHandlerFactory.HANDLER_TYPE.BODY, 1000, false, parseContext);
        ContentHandler handler = factory.createHandler();
        assertNotNull(handler);
    }

    @Test
    public void testCreateHandlerWithParametersAndParseContextThrowsException() throws Exception {
        factory = new BasicContentHandlerFactory(BasicContentHandlerFactory.HANDLER_TYPE.BODY, 1000, false, null);
        assertThrows(IllegalArgumentException.class, () -> factory.createHandler());
    }

    @Test
    public void testCreateHandlerInner() throws Exception {
        Method method = BasicContentHandlerFactory.class.getDeclaredMethod("createHandlerInner");
        method.setAccessible(true);
        ContentHandler handler = (ContentHandler) method.invoke(factory);
        assertNotNull(handler);
    }

    @Test
    public void testCreateHandlerInnerWithIgnoreType() throws Exception {
        factory = new BasicContentHandlerFactory(BasicContentHandlerFactory.HANDLER_TYPE.IGNORE, 1000, false, parseContext);
        Method method = BasicContentHandlerFactory.class.getDeclaredMethod("createHandlerInner");
        method.setAccessible(true);
        ContentHandler handler = (ContentHandler) method.invoke(factory);
        assertNotNull(handler);
    }

    @Test
    public void testCreateHandlerInnerWithNoFormatHandler() throws Exception {
        factory = new BasicContentHandlerFactory(BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, 1000, false, parseContext);
        Method method = BasicContentHandlerFactory.class.getDeclaredMethod("createHandlerInner");
        method.setAccessible(true);
        ContentHandler handler = (ContentHandler) method.invoke(factory);
        assertNotNull(handler);
    }

    @Test
    public void testCreateHandlerInnerWithNegativeWriteLimit() throws Exception {
        factory = new BasicContentHandlerFactory(BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, -1, false, parseContext);
        Method method = BasicContentHandlerFactory.class.getDeclaredMethod("createHandlerInner");
        method.setAccessible(true);
        ContentHandler handler = (ContentHandler) method.invoke(factory);
        assertNotNull(handler);
    }

    @Test
    public void testCreateHandlerInnerWithPositiveWriteLimit() throws Exception {
        factory = new BasicContentHandlerFactory(BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, 1000, false, parseContext);
        Method method = BasicContentHandlerFactory.class.getDeclaredMethod("createHandlerInner");
        method.setAccessible(true);
        ContentHandler handler = (ContentHandler) method.invoke(factory);
        assertNotNull(handler);
    }

    @Test
    public void testCreateHandlerInnerWithThrowOnWriteLimitReached() throws Exception {
        factory = new BasicContentHandlerFactory(BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, 1000, true, parseContext);
        Method method = BasicContentHandlerFactory.class.getDeclaredMethod("createHandlerInner");
        method.setAccessible(true);
        ContentHandler handler = (ContentHandler) method.invoke(factory);
        assertNotNull(handler);
    }

    @Test
    public void testCreateHandlerInnerWithNoThrowOnWriteLimitReached() throws Exception {
        factory = new BasicContentHandlerFactory(BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, 1000, false, parseContext);
        Method method = BasicContentHandlerFactory.class.getDeclaredMethod("createHandlerInner");
        method.setAccessible(true);
        ContentHandler handler = (ContentHandler) method.invoke(factory);
        assertNotNull(handler);
    }
}
