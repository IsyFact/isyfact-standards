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
import static org.junit.Assert.assertSame;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import de.bund.bva.isyfact.exception.TechnicalException;
import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.RemoteBean;
import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.impl.RemoteBeanImpl;
import de.bund.bva.isyfact.sicherheit.common.exception.SicherheitTechnicalException;
import de.bund.bva.pliscommon.exception.service.PlisTechnicalToException;

public class TestAnnotationExceptionMappingSource {

	private Method method;
	private AnnotationExceptionMappingSource mappingSource;

	@Before
	public void setUp() throws Exception {
		method = RemoteBean.class.getMethod("eineMethode");
		mappingSource = new AnnotationExceptionMappingSource();
	}

	@Test
	public void testGetToExceptionClass() {
		// Braucht man, damit der ClassLoader das entsprechende Package und Implementierungsklasse lädt.
		Class<?> implClass = RemoteBeanImpl.class;

		Class<?> exceptionClass = mappingSource.getToExceptionClass(method, TechnicalException.class);
        assertSame(PlisTechnicalToException.class, exceptionClass);
	}

	@Test
	public void testGetToExceptionClassExceptionNotInMapping() {
		// Braucht man, damit der ClassLoader das entsprechende Package und Implementierungsklasse lädt.
		Class<?> implClass = RemoteBeanImpl.class;

		Class<?> exceptionClass = mappingSource.getToExceptionClass(method, SicherheitTechnicalException.class);
		assertEquals(null, exceptionClass);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetToExceptionClassPackageNotExists() throws Exception {
		mappingSource.getToExceptionClass(Object.class.getMethod("toString"), TechnicalException.class);
	}

	@Test
	public void testGetGenericTechnicalToException() {
		// Braucht man, damit der ClassLoader das entsprechende Package und Implementierungsklasse lädt.
		Class<?> implClass = RemoteBeanImpl.class;

		Class<?> exceptionClass = mappingSource.getGenericTechnicalToException(method);
        assertSame(PlisTechnicalToException.class, exceptionClass);
	}

}
