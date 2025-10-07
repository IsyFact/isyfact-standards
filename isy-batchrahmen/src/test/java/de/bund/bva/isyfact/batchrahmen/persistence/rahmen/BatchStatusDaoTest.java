package de.bund.bva.isyfact.batchrahmen.persistence.rahmen;

import static org.junit.jupiter.api.Assertions.assertThrows;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenInitialisierungException;

@ExtendWith(SpringExtension.class)
public class BatchStatusDaoTest {
    private static String BATCH_ID = "test_id";
    @Mock
    private EntityManagerFactory entityManagerFactory;

    @Mock
    private EntityManager entityManager;

    private BatchRahmenDaoMock batchRahmenDaoMock;
    private BatchStatusDao batchRahmenDao;

    private BatchStatus batchStatus;
    @BeforeEach
    public void setup() {
        batchStatus = new BatchStatus();
        batchStatus.setBatchId(BATCH_ID);
        MockitoAnnotations.openMocks(this);
        Mockito.when(entityManager.find(BatchStatus.class, BATCH_ID, LockModeType.PESSIMISTIC_WRITE)).thenReturn(batchStatus);
        batchRahmenDaoMock = new BatchRahmenDaoMock(entityManagerFactory, entityManager);
        batchRahmenDao = new BatchStatusDao(entityManagerFactory);
    }

    @Test
    public void CreateBatchStatusErrorState() {
        assertThrows(BatchrahmenInitialisierungException.class, () ->
            batchRahmenDaoMock.createBatchStatus(batchStatus));
    }

    @Test
    public void GetEntityManagerErrorState() {
        assertThrows(BatchrahmenInitialisierungException.class, () ->
            batchRahmenDao.getEntityManager());
    }
}
