/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 * The Federal Office of Administration (Bundesverwaltungsamt, BVA)
 * licenses this file to you under the Apache License, Version 2.0 (the
 * License). You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package de.bund.bva.isyfact.serviceapi.common.exception;

import de.bund.bva.isyfact.exception.PlisBusinessException;
import de.bund.bva.isyfact.exception.PlisTechnicalException;
import de.bund.bva.isyfact.exception.PlisTechnicalRuntimeException;
import de.bund.bva.isyfact.exception.service.PlisBusinessToException;
import de.bund.bva.isyfact.exception.service.PlisTechnicalToException;
import org.junit.Test;
import de.bund.bva.isyfact.serviceapi.common.exception.test.TestExceptionFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PlisExceptionMapperTest {

    @Test
    public void testMapException() {
        PlisBusinessException exc = TestExceptionFactory.getPlisBusinessException();
        PlisBusinessToException toExc =
            PlisExceptionMapper.mapException(exc, TestExceptionFactory.MyPlisBusinessToException.class);
        assertNotNull(toExc);
        assertEquals(TestExceptionFactory.ausnahmeId, toExc.getAusnahmeId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapException2() {
        PlisBusinessException exc = TestExceptionFactory.getPlisBusinessException();
        PlisBusinessToException toExc = PlisExceptionMapper.mapException(exc, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapException3() {
        PlisTechnicalException exc = TestExceptionFactory.getPlisTechnicalException();
        PlisTechnicalToException toExc =
            PlisExceptionMapper.mapException(exc, TestExceptionFactory.MyWrongConstructorToException.class);
    }

    @Test
    public void testMapException4() {
        PlisTechnicalRuntimeException exc = TestExceptionFactory.getPlisTechnicalRuntimeException();
        TestExceptionFactory.MyPlisTechnicalToException toExc =
            PlisExceptionMapper.mapException(exc, TestExceptionFactory.MyPlisTechnicalToException.class);
        assertNotNull(toExc);
        assertEquals(TestExceptionFactory.ausnahmeId, toExc.getAusnahmeId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapException5() {
        PlisTechnicalRuntimeException exc = TestExceptionFactory.getPlisTechnicalRuntimeException();
        TestExceptionFactory.MyPlisTechnicalToException toExc = PlisExceptionMapper.mapException(exc, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapException6() {
        PlisTechnicalRuntimeException exc = TestExceptionFactory.getPlisTechnicalRuntimeException();
        TestExceptionFactory.MyWrongConstructorToException toExc =
            PlisExceptionMapper.mapException(exc, TestExceptionFactory.MyWrongConstructorToException.class);
    }

    @Test
    public void testCreateException() {
        TestExceptionFactory.MyPlisBusinessToException toExc = PlisExceptionMapper
            .createToException(TestExceptionFactory.ausnahmeId, TestExceptionFactory.provider,
                TestExceptionFactory.MyPlisBusinessToException.class, TestExceptionFactory.parameter);
        assertNotNull(toExc);
        assertEquals(TestExceptionFactory.ausnahmeId, toExc.getAusnahmeId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateException1() {
        TestExceptionFactory.MyPlisBusinessToException toExc = PlisExceptionMapper
            .createToException(TestExceptionFactory.ausnahmeId, TestExceptionFactory.provider, null,
                TestExceptionFactory.parameter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateException2() {
        TestExceptionFactory.MyWrongConstructorToException toExc = PlisExceptionMapper
            .createToException(TestExceptionFactory.ausnahmeId, TestExceptionFactory.provider,
                TestExceptionFactory.MyWrongConstructorToException.class, TestExceptionFactory.parameter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateException3() {
        TestExceptionFactory.MyWrongParameterToException toExc = PlisExceptionMapper
            .createToException("foo bar", TestExceptionFactory.provider,
                TestExceptionFactory.MyWrongParameterToException.class, TestExceptionFactory.parameter);
    }

}
