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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.apache.tika.config.OutputLimits;
import org.apache.tika.parser.ParseContext;



@ExtendWith(MockitoExtension.class)
public class BasicContentHandlerFactory_newInstance_2_0_Test {

    @Mock
    private ParseContext parseContext;

    @Mock
    private OutputLimits outputLimits;

    private BasicContentHandlerFactory factory;

    @BeforeEach
    public void setUp() {
        factory = new BasicContentHandlerFactory();
    }

    private void configureLimits() {
        when(parseContext.get(OutputLimits.class)).thenReturn(outputLimits);
        when(outputLimits.getWriteLimit()).thenReturn(100);
        when(outputLimits.isThrowOnWriteLimit()).thenReturn(true);
    }

    @Test
    public void testNewInstance() {
        configureLimits();
        BasicContentHandlerFactory result = BasicContentHandlerFactory.newInstance(BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, parseContext);
        assertNotNull(result);
        assertEquals(BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, result.getType());
        assertEquals(100, result.getWriteLimit());
        assertTrue(result.isThrowOnWriteLimitReached());
    }

    @Test
    public void testNewInstanceWithNullParseContext() {
        assertThrows(IllegalArgumentException.class, () ->
                BasicContentHandlerFactory.newInstance(
                        BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, null));
    }

    @Test
    public void testNewInstanceWithDifferentWriteLimit() {
        configureLimits();
        when(outputLimits.getWriteLimit()).thenReturn(200);
        BasicContentHandlerFactory result = BasicContentHandlerFactory.newInstance(BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, parseContext);
        assertEquals(200, result.getWriteLimit());
    }

    @Test
    public void testNewInstanceWithDifferentThrowOnWriteLimit() {
        configureLimits();
        when(outputLimits.isThrowOnWriteLimit()).thenReturn(false);
        BasicContentHandlerFactory result = BasicContentHandlerFactory.newInstance(BasicContentHandlerFactory.HANDLER_TYPE.MARKDOWN, parseContext);
        assertFalse(result.isThrowOnWriteLimitReached());
    }
}
