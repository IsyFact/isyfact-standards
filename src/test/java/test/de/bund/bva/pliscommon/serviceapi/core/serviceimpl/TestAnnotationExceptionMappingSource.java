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
package test.de.bund.bva.pliscommon.serviceapi.core.serviceimpl;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import de.bund.bva.pliscommon.exception.PlisTechnicalException;
import de.bund.bva.pliscommon.exception.service.PlisTechnicalToException;
import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.AnnotationExceptionMappingSource;
import test.de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.RemoteBean;
import test.de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.impl.RemoteBeanImpl;
import de.bund.bva.pliscommon.sicherheit.common.exception.SicherheitTechnicalException;

public class TestAnnotationExceptionMappingSource {

	private Method method;
	private AnnotationExceptionMappingSource mappingSource;
	
	@Before
	public void setUp() throws NoSuchMethodException, SecurityException{
		method = RemoteBean.class.getMethod("eineMethode");
		mappingSource = new AnnotationExceptionMappingSource();
	}
	
	@Test
	public void testGetToExceptionClass() throws NoSuchMethodException, SecurityException {
		// Braucht man, damit der ClassLoader das entsprechende Package und Implementierungsklasse lädt.
		Class<?> implClass = RemoteBeanImpl.class;		
				
		Class<?> exceptionClass = mappingSource.getToExceptionClass(method, PlisTechnicalException.class);
		assertEquals(PlisTechnicalToException.class, exceptionClass);
	}
	
	@Test
	public void testGetToExceptionClassExceptionNotInMapping() throws NoSuchMethodException, SecurityException {
		// Braucht man, damit der ClassLoader das entsprechende Package und Implementierungsklasse lädt.
		Class<?> implClass = RemoteBeanImpl.class;
				
		Class<?> exceptionClass = mappingSource.getToExceptionClass(method, SicherheitTechnicalException.class);
		assertEquals(null, exceptionClass);
	}
	
	@Test(expected = IllegalStateException.class)
	public void testGetToExceptionClassPackageNotExists() throws NoSuchMethodException, SecurityException {
		mappingSource.getToExceptionClass(Object.class.getMethod("toString"), PlisTechnicalException.class);
	}
	
	@Test
	public void testGetGenericTechnicalToException() throws NoSuchMethodException, SecurityException {
		// Braucht man, damit der ClassLoader das entsprechende Package und Implementierungsklasse lädt.
		Class<?> implClass = RemoteBeanImpl.class;
				
		Class<?> exceptionClass = mappingSource.getGenericTechnicalToException(method);
		assertEquals(PlisTechnicalToException.class, exceptionClass);
	}

}
