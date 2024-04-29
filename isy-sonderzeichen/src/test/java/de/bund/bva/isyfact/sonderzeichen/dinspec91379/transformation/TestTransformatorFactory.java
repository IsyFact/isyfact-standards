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
package de.bund.bva.isyfact.sonderzeichen.dinspec91379.transformation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import de.bund.bva.isyfact.sonderzeichen.dinspec91379.transformation.impl.IdentischerTransformator;

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
