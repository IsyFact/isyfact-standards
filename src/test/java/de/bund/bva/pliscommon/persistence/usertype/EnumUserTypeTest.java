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
package de.bund.bva.pliscommon.persistence.usertype;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Test;

public class EnumUserTypeTest {

    @Test
    public void testSetParameterValues() {
        EnumUserType userType = new EnumUserType();
        Properties prop = new Properties();
        prop.setProperty("enumClass", Vorgangsstatus.class.getName());
        userType.setParameterValues(prop);
        Assert.assertEquals("B", userType.convertInstanceToString(Vorgangsstatus.IN_BEARBEITUNG));
        Assert.assertEquals(Vorgangsstatus.IN_BEARBEITUNG, userType.convertStringToInstance("B"));
    }

    @Test
    public void testConvertStringToInstance() {
        EnumUserType userType = new EnumUserType();
        userType.setEnumClass(Vorgangsstatus.class);
        Assert.assertEquals("B", userType.convertInstanceToString(Vorgangsstatus.IN_BEARBEITUNG));
    }

    @Test
    public void testConvertInstanceToString() {
        EnumUserType userType = new EnumUserType();
        userType.setEnumClass(Vorgangsstatus.class);
        Assert.assertEquals(Vorgangsstatus.IN_BEARBEITUNG, userType.convertStringToInstance("B"));
    }

}
