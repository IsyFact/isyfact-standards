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
package de.bund.bva.isyfact.serviceapi.core.serviceimpl;

import static org.junit.Assert.assertEquals;

import de.bund.bva.isyfact.exception.service.BusinessToException;
import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.RemoteBean;
import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.SomeOtherBusinessException;
import org.junit.Before;
import org.junit.Test;

import de.bund.bva.isyfact.exception.BusinessException;
import de.bund.bva.isyfact.exception.service.TechnicalToException;

import java.lang.reflect.Method;

public class TestReflectiveExceptionMappingSource {

	ReflectiveExceptionMappingSource source;

	@Before
	public void setUp(){
		source = new ReflectiveExceptionMappingSource();
	}

	@Test
	public void testGetToExceptionClass() throws NoSuchMethodException, SecurityException {
		Class<?> clazz = source.getToExceptionClass(RemoteBean.class.getMethod("methodeMitZweiToExceptions"), BusinessException.class);
		assertEquals(BusinessToException.class, clazz);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetToExceptionClassMethodWiothoutException() throws NoSuchMethodException, SecurityException {
		source.getToExceptionClass(RemoteBean.class.getMethod("eineMethode"), BusinessException.class);
	}

	@Test
	public void testGetGenericTechnicalToException() throws NoSuchMethodException, SecurityException{
		Class<?> clazz = source.getGenericTechnicalToException(RemoteBean.class.getMethod("methodeMitZweiToExceptions"));
		assertEquals(TechnicalToException.class, clazz);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetGenericTechnicalToExceptionMehrereToExceptions() throws NoSuchMethodException, SecurityException{
		source.getGenericTechnicalToException(RemoteBean.class.getMethod("methodeMitZweiTechnicalToExceptions"));
	}

	@Test(expected = IllegalStateException.class)
	public void testGetGenericTechnicalToExceptionMethodeOhneException() throws NoSuchMethodException, SecurityException{
		source.getGenericTechnicalToException(RemoteBean.class.getMethod("eineMethode"));
	}

	@Test(expected = IllegalStateException.class)
	public void testGetToExceptionClassWithNoMatchingToException() throws NoSuchMethodException {
		Method method = RemoteBean.class.getMethod("methodeMitZweiToExceptions");
		source.getToExceptionClass(method, SomeOtherBusinessException.class);
	}

	@Test
	public void testGetToExceptionClassWithMultipleToExceptions() throws NoSuchMethodException {
		Method method = RemoteBean.class.getMethod("methodeMitZweiToExceptions");
		Class<?> expectedClass = BusinessToException.class;
		Class<?> actualClass = source.getToExceptionClass(method, BusinessException.class);
		assertEquals(expectedClass, actualClass);
	}

	@Test
	public void testRemoveEnd() throws Exception {
		Method method = ReflectiveExceptionMappingSource.class.getDeclaredMethod("removeEnd", String.class, String.class);
		method.setAccessible(true);
		String result = (String) method.invoke(source, "BusinessException", "Exception");
		assertEquals("Business", result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetToExceptionClassWithNullMethod() throws NoSuchMethodException {
		source.getToExceptionClass(null, BusinessException.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetToExceptionClassWithNullExceptionClass() throws NoSuchMethodException {
		Method method = RemoteBean.class.getMethod("methodeMitZweiToExceptions");
		source.getToExceptionClass(method, null);
	}

}
