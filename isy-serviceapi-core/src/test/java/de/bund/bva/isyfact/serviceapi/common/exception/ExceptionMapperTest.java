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

import de.bund.bva.isyfact.exception.BusinessException;
import de.bund.bva.isyfact.exception.TechnicalException;
import de.bund.bva.isyfact.exception.TechnicalRuntimeException;
import de.bund.bva.isyfact.exception.service.BusinessToException;
import de.bund.bva.isyfact.exception.service.TechnicalToException;
import org.junit.Test;
import de.bund.bva.isyfact.serviceapi.common.exception.test.TestExceptionFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ExceptionMapperTest {

    @Test
    public void testMapException() {
        BusinessException exc = TestExceptionFactory.getBusinessException();
        BusinessToException toExc =
            ExceptionMapper.mapException(exc, TestExceptionFactory.MyBusinessToException.class);
        assertNotNull(toExc);
        assertEquals(TestExceptionFactory.ausnahmeId, toExc.getAusnahmeId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapException2() {
        BusinessException exc = TestExceptionFactory.getBusinessException();
        BusinessToException toExc = ExceptionMapper.mapException(exc, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapException3() {
        TechnicalException exc = TestExceptionFactory.getTechnicalException();
        TechnicalToException toExc =
            ExceptionMapper.mapException(exc, TestExceptionFactory.MyWrongConstructorToException.class);
    }

    @Test
    public void testMapException4() {
        TechnicalRuntimeException exc = TestExceptionFactory.getTechnicalRuntimeException();
        TestExceptionFactory.MyTechnicalToException toExc =
            ExceptionMapper.mapException(exc, TestExceptionFactory.MyTechnicalToException.class);
        assertNotNull(toExc);
        assertEquals(TestExceptionFactory.ausnahmeId, toExc.getAusnahmeId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapException5() {
        TechnicalRuntimeException exc = TestExceptionFactory.getTechnicalRuntimeException();
        TestExceptionFactory.MyTechnicalToException toExc = ExceptionMapper.mapException(exc, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapException6() {
        TechnicalRuntimeException exc = TestExceptionFactory.getTechnicalRuntimeException();
        TestExceptionFactory.MyWrongConstructorToException toExc =
            ExceptionMapper.mapException(exc, TestExceptionFactory.MyWrongConstructorToException.class);
    }

    @Test
    public void testCreateException() {
        TestExceptionFactory.MyBusinessToException toExc = ExceptionMapper
            .createToException(TestExceptionFactory.ausnahmeId, TestExceptionFactory.provider,
                TestExceptionFactory.MyBusinessToException.class, TestExceptionFactory.parameter);
        assertNotNull(toExc);
        assertEquals(TestExceptionFactory.ausnahmeId, toExc.getAusnahmeId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateException1() {
        TestExceptionFactory.MyBusinessToException toExc = ExceptionMapper
            .createToException(TestExceptionFactory.ausnahmeId, TestExceptionFactory.provider, null,
                TestExceptionFactory.parameter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateException2() {
        TestExceptionFactory.MyWrongConstructorToException toExc = ExceptionMapper
            .createToException(TestExceptionFactory.ausnahmeId, TestExceptionFactory.provider,
                TestExceptionFactory.MyWrongConstructorToException.class, TestExceptionFactory.parameter);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateException3() {
        TestExceptionFactory.MyWrongParameterToException toExc = ExceptionMapper
            .createToException("foo bar", TestExceptionFactory.provider,
                TestExceptionFactory.MyWrongParameterToException.class, TestExceptionFactory.parameter);
    }

}
