package de.bund.bva.isyfact.serviceapi.core.serviceimpl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import de.bund.bva.isyfact.serviceapi.core.serviceimpl.test.impl.RemoteBeanImpl;
import de.bund.bva.isyfact.sicherheit.common.exception.FehlerhafteServiceKonfigurationRuntimeException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class TestMethodMapSicherheitAttributeSource {

	private MethodMapSicherheitAttributeSource source;
	
	@Before
	public void setUp(){
		source = new MethodMapSicherheitAttributeSource();
	}
	
	@Test(expected = FehlerhafteServiceKonfigurationRuntimeException.class)
	public void testAfterPropertiesSet() throws NoSuchMethodException, SecurityException {		
		Method m1 = RemoteBeanImpl.class.getMethod("eineMethode");
		source.setMethodMap(null);
		source.afterPropertiesSet();
		source.getBenoetigeRechte(m1, RemoteBeanImpl.class);		
	}
	
	@Test
	public void testGetBenoetigteRechte() throws NoSuchMethodException, SecurityException {
		String[] rechte1 = new String[]{"Recht_A", "Recht_B"};
		String[] rechte2 = new String[]{"Recht_A", "Recht_C"};
		
		Method m1 = RemoteBeanImpl.class.getMethod("eineMethode");
		Method m2 = RemoteBeanImpl.class.getMethod("eineAndereMethode");
		
		Map<String, String[]> map = new HashMap<>();
		map.put(RemoteBeanImpl.class.getName() + "." + m1.getName(), rechte1);
		map.put(RemoteBeanImpl.class.getName() + "." + m2.getName(), rechte2);
		
		source.setMethodMap(map);
		source.afterPropertiesSet();
		
		assertArrayEquals(rechte1, source.getBenoetigeRechte(m1, RemoteBeanImpl.class));
		assertArrayEquals(rechte2, source.getBenoetigeRechte(m2, RemoteBeanImpl.class));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAddGesichertMethodNameNotFQN(){
		String[] rechte1 = new String[]{"Recht_A", "Recht_B"};
		source.addGesichertMethod("name", rechte1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAddGesichertMethodMothodNotInClass(){
		String[] rechte1 = new String[]{"Recht_A", "Recht_B"};
		source.addGesichertMethod(RemoteBeanImpl.class, "name", rechte1);
	}
	
	@Test
	public void testAddGesichertReregisterMethod(){
		String[] rechte1 = new String[]{"Recht_A", "Recht_B"};
		String[] rechte2 = new String[]{"Recht_A", "Recht_C"};
		source.addGesichertMethod(RemoteBeanImpl.class, "eineMethode", rechte1);
		source.addGesichertMethod(RemoteBeanImpl.class, "eineMetho*", rechte2);
	}
}
