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

import org.junit.Assert;
import org.junit.Test;

public class RolleImplNpeTest {

    @Test
    public void testEqualsObject() {

        // ID und Name muessen gleich sein (auch NULL moeglich bei Namen)
        Assert.assertTrue(new RolleImpl("r1", "nameR1").equals(new RolleImpl("r1", "nameR1")));
        Assert.assertTrue(new RolleImpl("r1", null).equals(new RolleImpl("r1", null)));

        // False Szenarien
        Assert.assertFalse(new RolleImpl("r1", "nameR1").equals(new RolleImpl("r1", "nameR2")));
        Assert.assertFalse(new RolleImpl("r1", "nameR1").equals(new RolleImpl("r2", "nameR1")));
        Assert.assertFalse(new RolleImpl("r1", null).equals(new RolleImpl("r1", "nameR2")));
        Assert.assertFalse(new RolleImpl("r1", "nameR1").equals(new RolleImpl("r1", null)));
    }
}
