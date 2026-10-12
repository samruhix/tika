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

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xml.sax.ContentHandler;

import org.apache.tika.parser.ParseContext;



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
        factory = new BasicContentHandlerFactory(
                BasicContentHandlerFactory.HANDLER_TYPE.BODY, 1000);
        ContentHandler handler = factory.createHandler();
        assertNotNull(handler);
    }

    @Test
    public void testCreateHandlerWithParametersAndParseContext() throws Exception {
        factory = new BasicContentHandlerFactory(
                BasicContentHandlerFactory.HANDLER_TYPE.BODY, 1000, false, parseContext);
        ContentHandler handler = factory.createHandler();
        assertNotNull(handler);
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
        factory = new BasicContentHandlerFactory(
                BasicContentHandlerFactory.HANDLER_TYPE.IGNORE, 1000, false, parseContext);
        Method method = BasicContentHandlerFactory.class.getDeclaredMethod("createHandlerInner");
        method.setAccessible(true);
        ContentHandler handler = (ContentHandler) method.invoke(factory);
        assertNotNull(handler);
    }

    @Test
    public void testCreateHandlerInnerWithNoFormatHandler() throws Exception {
        factory = new BasicContentHandlerFactory(
                BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, 1000, false, parseContext);
        Method method = BasicContentHandlerFactory.class.getDeclaredMethod("createHandlerInner");
        method.setAccessible(true);
        ContentHandler handler = (ContentHandler) method.invoke(factory);
        assertNotNull(handler);
    }

    @Test
    public void testCreateHandlerInnerWithNegativeWriteLimit() throws Exception {
        factory = new BasicContentHandlerFactory(
                BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, -1, false, parseContext);
        Method method = BasicContentHandlerFactory.class.getDeclaredMethod("createHandlerInner");
        method.setAccessible(true);
        ContentHandler handler = (ContentHandler) method.invoke(factory);
        assertNotNull(handler);
    }

    @Test
    public void testCreateHandlerInnerWithPositiveWriteLimit() throws Exception {
        factory = new BasicContentHandlerFactory(
                BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, 1000, false, parseContext);
        Method method = BasicContentHandlerFactory.class.getDeclaredMethod("createHandlerInner");
        method.setAccessible(true);
        ContentHandler handler = (ContentHandler) method.invoke(factory);
        assertNotNull(handler);
    }

    @Test
    public void testCreateHandlerInnerWithThrowOnWriteLimitReached() throws Exception {
        factory = new BasicContentHandlerFactory(
                BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, 1000, true, parseContext);
        Method method = BasicContentHandlerFactory.class.getDeclaredMethod("createHandlerInner");
        method.setAccessible(true);
        ContentHandler handler = (ContentHandler) method.invoke(factory);
        assertNotNull(handler);
    }

    @Test
    public void testCreateHandlerInnerWithNoThrowOnWriteLimitReached() throws Exception {
        factory = new BasicContentHandlerFactory(
                BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, 1000, false, parseContext);
        Method method = BasicContentHandlerFactory.class.getDeclaredMethod("createHandlerInner");
        method.setAccessible(true);
        ContentHandler handler = (ContentHandler) method.invoke(factory);
        assertNotNull(handler);
    }
}
