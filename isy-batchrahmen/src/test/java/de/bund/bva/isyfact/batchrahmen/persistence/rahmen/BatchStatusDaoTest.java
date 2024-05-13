package de.bund.bva.isyfact.batchrahmen.persistence.rahmen;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenInitialisierungException;

public class BatchStatusDaoTest {

    @Mock
    private EntityManagerProvider entityManagerProvider;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private BatchStatusDao batchStatusDao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(entityManagerProvider.getTransactionalEntityManager()).thenReturn(entityManager);
    }

    @Test
    public void testCreateBatchStatus_NewStatus() {
        BatchStatus newStatus = new BatchStatus();
        newStatus.setBatchId("newId");
        when(entityManager.find(BatchStatus.class, "newId", LockModeType.PESSIMISTIC_WRITE)).thenReturn(null);

        batchStatusDao.createBatchStatus(newStatus);

        verify(entityManager).persist(newStatus);
    }

    @Test
    public void testLeseBatchStatus_ExistingId() {
        BatchStatus expectedStatus = new BatchStatus();
        when(entityManager.find(BatchStatus.class, "existingId", LockModeType.PESSIMISTIC_WRITE)).thenReturn(expectedStatus);

        BatchStatus result = batchStatusDao.leseBatchStatus("existingId");

        assertNotNull(result);
        verify(entityManager).find(BatchStatus.class, "existingId", LockModeType.PESSIMISTIC_WRITE);
    }

    @Test(expected = BatchrahmenInitialisierungException.class)
    public void testLeseBatchStatus_NoEntityManager() {
        when(entityManagerProvider.getTransactionalEntityManager()).thenReturn(null);

        batchStatusDao.leseBatchStatus("anyId");
    }

    @Test(expected = BatchrahmenInitialisierungException.class)
    public void testCreateBatchStatus_ExistingStatus() {
        BatchStatus existingStatus = new BatchStatus();
        existingStatus.setBatchId("existingId");
        when(entityManager.find(BatchStatus.class, "existingId", LockModeType.PESSIMISTIC_WRITE)).thenReturn(existingStatus);

        batchStatusDao.createBatchStatus(existingStatus);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateBatchStatus_NullInput() {
        batchStatusDao.createBatchStatus(null);
    }

    @Test(expected = RuntimeException.class)
    public void testEntityManagerThrowsException() {
        when(entityManager.find(BatchStatus.class, "faultyId", LockModeType.PESSIMISTIC_WRITE)).thenThrow(RuntimeException.class);

        batchStatusDao.leseBatchStatus("faultyId");
    }

    @Test(expected = BatchrahmenInitialisierungException.class)
    public void testEmptyBatchId() {
        batchStatusDao.leseBatchStatus("");
    }

    @Test(expected = BatchrahmenInitialisierungException.class)
    public void testLeseBatchStatus_NullId() {
        batchStatusDao.leseBatchStatus(null);
    }

    @Test(expected = BatchrahmenInitialisierungException.class)
    public void testCreateBatchStatus_NoEntityManagerWhilePersisting() {
        BatchStatus newStatus = new BatchStatus();
        newStatus.setBatchId("persistId");
        when(entityManager.find(BatchStatus.class, "persistId", LockModeType.PESSIMISTIC_WRITE)).thenReturn(null);
        when(entityManagerProvider.getTransactionalEntityManager()).thenReturn(null);

        batchStatusDao.createBatchStatus(newStatus);
    }

    @Test
    public void testLeseBatchStatus_NonExistentId() {
        when(entityManager.find(BatchStatus.class, "nonExistentId", LockModeType.PESSIMISTIC_WRITE)).thenReturn(null);

        BatchStatus result = batchStatusDao.leseBatchStatus("nonExistentId");

        assertNull(result);
    }

    @Test(expected = RuntimeException.class)
    public void testEntityManagerThrowsUncheckedExceptionDuringFind() {
        when(entityManager.find(BatchStatus.class, "errorId", LockModeType.PESSIMISTIC_WRITE)).thenThrow(new RuntimeException("Database error"));

        batchStatusDao.leseBatchStatus("errorId");
    }

    @Test(expected = RuntimeException.class)
    public void testPersistThrowsRuntimeException() {
        BatchStatus newStatus = new BatchStatus();
        newStatus.setBatchId("errorPersistId");
        when(entityManager.find(BatchStatus.class, "errorPersistId", LockModeType.PESSIMISTIC_WRITE)).thenReturn(null);
        doThrow(new RuntimeException("Persist error")).when(entityManager).persist(any(BatchStatus.class));

        batchStatusDao.createBatchStatus(newStatus);
    }

}
