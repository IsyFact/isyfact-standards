package de.bund.bva.isyfact.sonderzeichen.dinspec91379.transformation.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.bund.bva.isyfact.sonderzeichen.dinspec91379.transformation.Transformator;

public class KomplexeTransformationTest {
	
	Transformator transformator;
	KomplexeTransformation komplexeTransformation;

	@Before
	public void setUp(){
		transformator = new IdentischerTransformator();
		komplexeTransformation = new KomplexeTransformation(transformator);
	}
	
	@Test
	public void testAddErsetzung() {
		komplexeTransformation.addErsetzung("Wagner", "Jens");
		assertEquals("Jens", komplexeTransformation.getErsetzung("Wagner", 0));

	}

	@Test
	public void testAddErsetzungMitRegeln() {
		String[] regeln = new String[] { "1", "2" };
		komplexeTransformation.addErsetzung("plis", "isy", regeln);
		assertEquals("isy", komplexeTransformation.getErsetzung("plis", 0));
	}
}
