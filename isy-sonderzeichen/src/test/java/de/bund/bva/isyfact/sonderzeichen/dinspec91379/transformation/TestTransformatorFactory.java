package de.bund.bva.isyfact.sonderzeichen.dinspec91379.transformation;

import org.junit.Before;
import org.junit.Test;

import de.bund.bva.isyfact.sonderzeichen.dinspec91379.transformation.impl.IdentischerTransformator;

import static org.junit.Assert.*;

public class TestTransformatorFactory {

	private TransformatorFactory transFactory;
	private IdentischerTransformator transformator;
	private String transformationsTabelle;
	
	@Before
	public void setUp(){
		transFactory = new TransformatorFactory();
		transformationsTabelle = "/tabellen/zusaetzliche.transform";
	}
	
	@Test
	public void testGetObjectType() {
		transformator = new IdentischerTransformator();
		transFactory.setTransformator(transformator);
		assertEquals(Transformator.class, transFactory.getObjectType());
	}
	
	@Test
	public void testGetObjectNullType() {
		transFactory.setTransformator(transformator);
		assertNull(transFactory.getObjectType());
	}
	
	@Test
	public void testGetObject() {
		transformator = new IdentischerTransformator();
		transFactory.setTransformator(transformator);
		assertEquals(transformator, transFactory.getObject());
	}
	
	@Test
	public void getIsSingleton(){
		transformator = new IdentischerTransformator();
		transFactory.setTransformator(transformator);
		assertNotEquals(false, transFactory.isSingleton());
		
	}
	
	@Test
	public void testSetTransformationsTabelle(){
		transformator = new IdentischerTransformator();
		transFactory.setTransformator(transformator);
		transFactory.setTransformationsTabelle(transformationsTabelle);
		String zeichenkette = "\u0410";
		assertNotEquals("K", transformator.transformiere(zeichenkette));
	}

}
