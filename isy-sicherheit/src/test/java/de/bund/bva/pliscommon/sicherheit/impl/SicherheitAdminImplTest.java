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

import de.bund.bva.pliscommon.sicherheit.accessmgr.AccessManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SicherheitAdminImplTest {

    @Mock
    private AccessManager accessManager;

    @Test
    @SuppressWarnings("unchecked")
    public void testePing() {
        SicherheitAdminImpl sicherheitAdmin = new SicherheitAdminImpl(accessManager);

        when(accessManager.pingAccessManager()).thenReturn(true);
        assertTrue(sicherheitAdmin.pingAccessManager());
        verify(accessManager, times(1)).pingAccessManager();

        when(accessManager.pingAccessManager()).thenReturn(false);
        assertFalse(sicherheitAdmin.pingAccessManager());
        verify(accessManager, times(2)).pingAccessManager();
    }

}
