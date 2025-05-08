package de.bund.bva.isyfact.sonderzeichen.stringlatin1_1.core.transformation.impl;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.bund.bva.isyfact.sonderzeichen.stringlatin1_1.core.transformation.Transformator;

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
		komplexeTransformation.addErsetzung("alt", "neu", regeln);
		assertEquals("neu", komplexeTransformation.getErsetzung("alt", 0));
	}
}
