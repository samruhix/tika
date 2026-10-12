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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.apache.tika.parser.ParseContext;



@ExtendWith(MockitoExtension.class)
public class BasicContentHandlerFactory_hashCode_19_0_Test {

    @Mock
    private ParseContext parseContext;

    private BasicContentHandlerFactory factory;

    @BeforeEach
    public void setUp() {
        factory = new BasicContentHandlerFactory(
                BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, 1000, true, parseContext);
    }

    @Test
    public void testHashCode() {
        int hashCode = factory.hashCode();
        assertEquals(((31 * BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN.hashCode() + 1000) * 31 + 1) * 31 + 0, hashCode);
    }

    @Test
    public void testHashCodeWithNullType() {
        factory.setType(null);
        int hashCode = factory.hashCode();
        assertEquals(((31 * 0 + 1000) * 31 + 1) * 31 + 0, hashCode);
    }

    @Test
    public void testHashCodeWithDifferentType() {
        factory.setType(BasicContentHandlerFactory.HANDLER_TYPE.HTML);
        int hashCode = factory.hashCode();
        assertEquals(((31 * BasicContentHandlerFactory.HANDLER_TYPE.HTML.hashCode() + 1000) * 31 + 1) * 31 + 0, hashCode);
    }

    @Test
    public void testHashCodeWithDifferentWriteLimit() {
        factory.setWriteLimit(2000);
        int hashCode = factory.hashCode();
        assertEquals(((31 * BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN.hashCode() + 2000) * 31 + 1) * 31 + 0, hashCode);
    }

    @Test
    public void testHashCodeWithDifferentThrowOnWriteLimitReached() {
        factory.setThrowOnWriteLimitReached(false);
        int hashCode = factory.hashCode();
        assertEquals(((31 * BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN.hashCode() + 1000) * 31 + 0) * 31 + 0, hashCode);
    }

    @Test
    public void testHashCodeWithDifferentValidateXHTML() {
        factory.setValidateXHTML(true);
        int hashCode = factory.hashCode();
        assertEquals(((31 * BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN.hashCode() + 1000) * 31 + 1) * 31 + 1, hashCode);
    }
}
