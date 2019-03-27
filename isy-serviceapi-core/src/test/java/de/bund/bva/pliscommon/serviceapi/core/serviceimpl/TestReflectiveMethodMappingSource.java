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
package de.bund.bva.pliscommon.serviceapi.core.serviceimpl;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.RemoteBean;
import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.impl.RemoteBeanImpl;
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
