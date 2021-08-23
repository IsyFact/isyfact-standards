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
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import de.bund.bva.pliscommon.serviceapi.core.serviceimpl.test.impl.RemoteBeanImpl;
import de.bund.bva.pliscommon.sicherheit.common.exception.FehlerhafteServiceKonfigurationRuntimeException;

public class TestMethodMapSicherheitAttributeSource {

    private MethodMapSicherheitAttributeSource source;

    @Before
    public void setUp() {
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
        String[] rechte1 = new String[]{ "Recht_A", "Recht_B" };
        String[] rechte2 = new String[]{ "Recht_A", "Recht_C" };

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
    public void testAddGesichertMethodNameNotFQN() {
        String[] rechte1 = new String[]{ "Recht_A", "Recht_B" };
        source.addGesichertMethod("name", rechte1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddGesichertMethodMothodNotInClass() {
        String[] rechte1 = new String[]{ "Recht_A", "Recht_B" };
        source.addGesichertMethod(RemoteBeanImpl.class, "name", rechte1);
    }

    @Test
    public void testAddGesichertReregisterMethod() {
        String[] rechte1 = new String[]{ "Recht_A", "Recht_B" };
        String[] rechte2 = new String[]{ "Recht_A", "Recht_C" };
        source.addGesichertMethod(RemoteBeanImpl.class, "eineMethode", rechte1);
        source.addGesichertMethod(RemoteBeanImpl.class, "eineMetho*", rechte2);
    }

}
