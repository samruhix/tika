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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.apache.tika.parser.ParseContext;



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
        BasicContentHandlerFactory other = new BasicContentHandlerFactory(
                BasicContentHandlerFactory.HANDLER_TYPE.XML, 100);
        assertFalse(factory.equals(other));
    }

    @Test
    public void testEqualsDifferentWriteLimit() {
        BasicContentHandlerFactory other = new BasicContentHandlerFactory(
                BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, 200);
        assertFalse(factory.equals(other));
    }

    @Test
    public void testEqualsDifferentThrowOnWriteLimitReached() {
        BasicContentHandlerFactory other = new BasicContentHandlerFactory(
                BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, 100, false, parseContext);
        assertFalse(factory.equals(other));
    }

    @Test
    public void testEqualsDifferentValidateXHTML() {
        BasicContentHandlerFactory other = new BasicContentHandlerFactory();
        factory.setValidateXHTML(true);
        assertFalse(factory.equals(other));
    }

    @Test
    public void testEqualsSame() {
        BasicContentHandlerFactory other = new BasicContentHandlerFactory();
        assertTrue(factory.equals(other));
    }
}
