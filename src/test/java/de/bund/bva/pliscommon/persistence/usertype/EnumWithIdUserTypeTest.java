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

public class EnumWithIdUserTypeTest {

    @Test
    public void testSetParameterValues() {
        final String sollId = Vermerkstyp.NACHRICHT_EMPFANGEN.getId();
        EnumWithIdUserType userType = new EnumWithIdUserType();
        Properties prop = new Properties();
        prop.setProperty("enumClass", Vermerkstyp.class.getName());
        userType.setParameterValues(prop);
        Assert.assertEquals(sollId, userType.convertInstanceToString(Vermerkstyp.NACHRICHT_EMPFANGEN));
        Assert.assertEquals(Vermerkstyp.NACHRICHT_EMPFANGEN, userType.convertStringToInstance(sollId));
    }

    @Test
    public void testConvertStringToInstance() {
        final String sollId = Vermerkstyp.NACHRICHT_EMPFANGEN.getId();
        EnumWithIdUserType userType = new EnumWithIdUserType();
        userType.setEnumClass(Vermerkstyp.class);
        Assert.assertEquals(sollId, userType.convertInstanceToString(Vermerkstyp.NACHRICHT_EMPFANGEN));
    }

    @Test
    public void testConvertInstanceToString() {
        final String sollId = Vermerkstyp.NACHRICHT_EMPFANGEN.getId();
        EnumWithIdUserType userType = new EnumWithIdUserType();
        userType.setEnumClass(Vermerkstyp.class);
        Assert.assertEquals(Vermerkstyp.NACHRICHT_EMPFANGEN, userType.convertStringToInstance(sollId));
    }

}
