package de.bund.bva.isyfact.serviceapi.core.serviceimpl;

import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import de.bund.bva.isyfact.exception.BusinessException;
import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.RemoteBean;
import de.bund.bva.pliscommon.exception.service.PlisBusinessToException;
import de.bund.bva.pliscommon.exception.service.PlisTechnicalToException;

public class TestReflectiveExceptionMappingSource {

	private ReflectiveExceptionMappingSource source;

	@Before
	public void setUp(){
		source = new ReflectiveExceptionMappingSource();
	}

	@Test
	public void testGetToExceptionClass() throws Exception {
		Class<?> clazz = source.getToExceptionClass(RemoteBean.class.getMethod("methodeMitZweiToExceptions"), BusinessException.class);
        assertSame(PlisBusinessToException.class, clazz);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetToExceptionClassMethodWiothoutException() throws Exception {
		source.getToExceptionClass(RemoteBean.class.getMethod("eineMethode"), BusinessException.class);
	}

	@Test
	public void testGetGenericTechnicalToException() throws Exception {
		Class<?> clazz = source.getGenericTechnicalToException(RemoteBean.class.getMethod("methodeMitZweiToExceptions"));
        assertSame(PlisTechnicalToException.class, clazz);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetGenericTechnicalToExceptionMehrereToExceptions() throws Exception {
		source.getGenericTechnicalToException(RemoteBean.class.getMethod("methodeMitZweiTechnicalToExceptions"));
	}

	@Test(expected = IllegalStateException.class)
	public void testGetGenericTechnicalToExceptionMethodeOhneException() throws Exception {
		source.getGenericTechnicalToException(RemoteBean.class.getMethod("eineMethode"));
	}

}
