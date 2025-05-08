package de.bund.bva.isyfact.sicherheit.impl;

import de.bund.bva.isyfact.sicherheit.accessmgr.AccessManager;
import de.bund.bva.isyfact.sicherheit.impl.SicherheitAdminImpl;
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
