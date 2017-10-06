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
package de.bund.bva.pliscommon.sicherheit.impl;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class RechtImplTest {

	@Test(expected = IllegalArgumentException.class)
	public void testRechtOhneID() {
		RechtImpl recht = new RechtImpl(null, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testRechtLeereID() {
		RechtImpl recht = new RechtImpl("", null);
	}
	
	@Test()
	public void testRechtNullProps() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("key", "value");
		RechtImpl recht = new RechtImpl("ro1", map);
		String str = recht.toString();
		assertEquals("value", recht.getProperty("key"));
	}
	
	@Test
	public void testEqualsRechteIdentisch(){
		RechtImpl recht = new RechtImpl("id", new HashMap<String, Object>());
		assertTrue(recht.equals(recht));
	}
	
	@Test
	public void testEqualsZweitesRechtNull(){
		RechtImpl recht = new RechtImpl("id", new HashMap<String, Object>());
		assertFalse(recht.equals(null));
	}
	
	@Test
	public void testEqualsZweitesRechtAnderesObjekt(){
		RechtImpl recht = new RechtImpl("id", new HashMap<String, Object>());
		assertFalse(recht.equals(new Integer(1)));
	}
	
	@Test
	public void testEqualsRechteIdsGleich(){
		RechtImpl r1 = new RechtImpl("r1", new HashMap<String, Object>());
		RechtImpl r2 = new RechtImpl("r1", new HashMap<String, Object>());
		assertTrue(r1.equals(r2));
	}

	@Test
	public void testEqualsRechteIdsVerschieden(){
		RechtImpl r1 = new RechtImpl("r1", new HashMap<String, Object>());
		RechtImpl r2 = new RechtImpl("r2", new HashMap<String, Object>());
		assertFalse(r1.equals(r2));
	}
}
