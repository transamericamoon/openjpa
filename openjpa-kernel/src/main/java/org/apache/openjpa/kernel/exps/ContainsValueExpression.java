/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.openjpa.kernel.exps;

import java.util.Collection;
import java.util.Map;

/**
 * Tests that a Map value collection contains a value.
 *
 * @author Abe White
 */
class ContainsValueExpression
    extends ContainsExpression {

    /**
     * Constructor.
     *
     * @param val1 the container value
     * @param val2 the containee to test
     */
    public ContainsValueExpression(Val val1, Val val2) {
        super(val1, val2);
    }

    protected Collection getCollection(Object obj) {
        return (obj == null) ? null : ((Map) obj).values();
    }
}

