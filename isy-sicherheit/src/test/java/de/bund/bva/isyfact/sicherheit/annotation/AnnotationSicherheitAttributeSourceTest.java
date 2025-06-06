package de.bund.bva.isyfact.sicherheit.annotation;

import java.util.Arrays;
import java.util.Collections;

import de.bund.bva.isyfact.sicherheit.annotation.bean.Service3Intf;
import de.bund.bva.isyfact.sicherheit.annotation.bean.ServiceImpl;
import de.bund.bva.isyfact.sicherheit.common.exception.FehlerhafteServiceKonfigurationRuntimeException;
import org.junit.Before;
import org.junit.Test;

import de.bund.bva.isyfact.sicherheit.annotation.bean.Service3Impl;
import de.bund.bva.isyfact.sicherheit.annotation.bean.ServiceIntf;

import static junit.framework.TestCase.assertEquals;

public class AnnotationSicherheitAttributeSourceTest {

	private AnnotationSicherheitAttributeSource source;
	
	@Before
	public void setUp(){
		source = new AnnotationSicherheitAttributeSource();
	}

	@Test(expected = FehlerhafteServiceKonfigurationRuntimeException.class)
	public void testMethodeNichtGesichert() throws Exception {
		ServiceIntf service = new ServiceImpl();
		source.getBenoetigeRechte(service.getClass().getMethod("nichtGesichert"), service.getClass());
	}
	
	@Test
	public void testMethodeGesichertAmInterface() throws Exception {
		ServiceIntf service = new ServiceImpl();
		String[] rechte = source.getBenoetigeRechte(ServiceIntf.class.getMethod("gesichertAmInterface"), service.getClass());
		assertEquals(Collections.singletonList("Recht_A"), Arrays.asList(rechte));
	}
	
	@Test
	public void testGesichertAmInterface() throws Exception {
		Service3Intf service3 = new Service3Impl();
		String[] rechte = source.getBenoetigeRechte(Service3Intf.class.getMethod("gesichertDurch_RechtB"), service3.getClass());
		assertEquals(Collections.singletonList("Recht_B"), Arrays.asList(rechte));
	}

}
