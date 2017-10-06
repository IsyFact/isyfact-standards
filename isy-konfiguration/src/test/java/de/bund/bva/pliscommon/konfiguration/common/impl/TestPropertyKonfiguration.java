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
package de.bund.bva.pliscommon.konfiguration.common.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.bund.bva.pliscommon.konfiguration.common.exception.KonfigurationDateiException;
import de.bund.bva.pliscommon.konfiguration.common.exception.KonfigurationParameterException;

public class TestPropertyKonfiguration {

	private static final String CONFIG_A = "/config/config_A.properties";

	private static final String CONFIG_B = "/config/config_B.properties";

	private PropertyKonfiguration konf;

	@Before
	public void setUp() throws Exception {
		List<String> list = new ArrayList<String>();
		list.add(CONFIG_A);
		list.add(CONFIG_B);
		konf = new PropertyKonfiguration(list);
	}

	@Test
	public void testGetSchluessel() {
		Set<String> testSet = konf.getSchluessel();
		assertEquals(9, testSet.size());
		assertTrue(testSet.contains("parameter.string"));
		assertTrue(testSet.contains("parameter.rawstring"));
		assertTrue(testSet.contains("parameter.int.2"));
	}
	
	@Test
	public void testContainsKey(){
		assertTrue(konf.containsKey("parameter.string"));
	}

	@Test
	public void testGetValue(){
		PropertyKonfiguration konfCopy = new PropertyKonfiguration(konf.getProperties());
		assertEquals("Hans ", konfCopy.getValue("parameter.rawstring"));
	}
	
	@Test(expected = KonfigurationDateiException.class)	
	public void testInitializationOnException(){
		List<String> list = new ArrayList<String>();
		list.add("foo bar");
		new PropertyKonfiguration(list);
	}
	
	/**
	 * Testen der getAs...() Methoden aus AbstractKonfiguration
	 */

	@Test
	public void testAbstractKonfiguration() {
		assertEquals(false, konf.getAsBoolean("parameter.boolean", true));
		assertEquals(false, konf.getAsBoolean("parameter.boolean.2", true));
		assertEquals(true, konf.getAsBoolean("parameter.boolean.3", false));
		
		assertEquals(2000, konf.getAsInteger("parameter.int", 0));		
		assertEquals(1000, konf.getAsLong("parameter.int.2", 0));		
		assertEquals(100.5, konf.getAsDouble("parameter.double", 0.0), 0.0);
		
		assertEquals("", konf.getAsString("parameter.defined", null));
		//testet ob die AsString Methode trimmt
		assertEquals("Hans", konf.getAsString("parameter.rawstring", null));
		
		assertEquals("Hans ", konf.getAsRawString("parameter.rawstring", null));
				
		
		assertEquals(true, konf.getAsBoolean("parameter", true));		
		assertEquals(0, konf.getAsInteger("parameter", 0));
		assertEquals(0l, konf.getAsLong("parameter", 0l));
		assertEquals(0.0, konf.getAsDouble("parameter", 0.0), 0.0);
		assertEquals(null, konf.getAsString("parameter", null));
		assertEquals(null, konf.getAsRawString("parameter", null));	
		
		assertEquals(false, konf.getAsBoolean("parameter.boolean"));
		assertEquals(2000, konf.getAsInteger("parameter.int"));		
		assertEquals(1000, konf.getAsLong("parameter.int.2"));		
		assertEquals(100.5, konf.getAsDouble("parameter.double"), 0.0);
		assertEquals("Hans", konf.getAsString("parameter.rawstring"));
		assertEquals("Hans ", konf.getAsRawString("parameter.rawstring"));	
				
	}
	
	@Test(expected = KonfigurationParameterException.class)
	public void testAsStringOnException(){
		konf.getAsString("parameter");
	}
	
	@Test(expected = KonfigurationParameterException.class)
	public void testAsRawStringOnException(){
		konf.getAsRawString("parameter");
	}
	
	@Test(expected = KonfigurationParameterException.class)
	public void testAsBooleanOnException(){
		konf.getAsBoolean("parameter");
	}
	@Test(expected = KonfigurationParameterException.class)
	public void testAsBooleanWrongFormatOnException(){
		konf.getAsBoolean("parameter.int");
	}
	
	@Test(expected = KonfigurationParameterException.class)
	public void testAsLongOnException(){
		konf.getAsLong("parameter");
	}
	@Test(expected = KonfigurationParameterException.class)
	public void testAsLongWrongFormatOnException(){
		konf.getAsLong("parameter.string");
	}
	@Test(expected = KonfigurationParameterException.class)
	public void testAsDoubleOnException(){
		konf.getAsDouble("parameter");
	}
	@Test(expected = KonfigurationParameterException.class)
	public void testAsDoubleWrongFormatOnException(){
		konf.getAsDouble("parameter.string");
	}
	@Test(expected = KonfigurationParameterException.class)
	public void testAsIntegerOnException(){
		konf.getAsInteger("parameter");
	}
	@Test(expected = KonfigurationParameterException.class)
	public void testAsIntegernWrongFormatOnException(){
		konf.getAsInteger("parameter.string");
	}
	
}
