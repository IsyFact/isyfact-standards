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
		// Needed to make the ClassLoader load the appropriate package and implementation class.
		Class<?> implClass = RemoteBeanImpl.class;

		Class<?> exceptionClass = mappingSource.getToExceptionClass(method, TechnicalException.class);
        assertSame(PlisTechnicalToException.class, exceptionClass);
	}

	@Test
	public void testGetToExceptionClassExceptionNotInMapping() {
		// Needed to make the ClassLoader load the appropriate package and implementation class.
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
		// Needed to make the ClassLoader load the appropriate package and implementation class.
		Class<?> implClass = RemoteBeanImpl.class;

		Class<?> exceptionClass = mappingSource.getGenericTechnicalToException(method);
        assertSame(PlisTechnicalToException.class, exceptionClass);
	}

}
