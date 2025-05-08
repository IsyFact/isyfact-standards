package de.bund.bva.isyfact.serviceapi.core.serviceimpl;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.RemoteBean;
import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.impl.RemoteBeanImpl;
import de.bund.bva.pliscommon.serviceapi.service.httpinvoker.v1_0_0.AufrufKontextTo;

public class TestReflectiveMethodMappingSource {

	private ReflectiveMethodMappingSource source;

	@Before
	public void setUp(){
		source = new ReflectiveMethodMappingSource();
	}

	@Test
	public void testGetTargetMethod() throws NoSuchMethodException, SecurityException {
		Method intfM = RemoteBean.class.getMethod("eineMethode");
		Method implM = source.getTargetMethod(intfM, RemoteBeanImpl.class);
		assertEquals(implM, source.getTargetMethod(intfM, RemoteBeanImpl.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetTargetMethodMultipleMatches() throws NoSuchMethodException, SecurityException {
		Method intfM = RemoteBeanImpl.class.getMethod("eineAndereMethode", AufrufKontextTo.class, Integer.class);
		source.getTargetMethod(intfM, RemoteBeanImpl.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetTargetMethodNoMatches() throws NoSuchMethodException, SecurityException {
		Method intfM = RemoteBeanImpl.class.getMethod("nichtImInterfaceDeklariert");
		source.getTargetMethod(intfM, RemoteBeanImpl.class);
	}

}
