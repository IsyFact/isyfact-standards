package de.bund.bva.isyfact.batchrahmen.persistence.rahmen;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.LockModeType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.bund.bva.isyfact.batchrahmen.core.exception.BatchrahmenInitialisierungException;

@RunWith(SpringJUnit4ClassRunner.class)
public class BatchStatusDaoTest {
    private static String BATCH_ID = "test_id";
    @Mock
    private EntityManagerFactory entityManagerFactory;

    @Mock
    private EntityManager entityManager;

    private BatchRahmenDaoMock batchRahmenDaoMock;
    private BatchStatusDao batchRahmenDao;

    private BatchStatus batchStatus;
    @Before
    public void setup() {
        batchStatus = new BatchStatus();
        batchStatus.setBatchId(BATCH_ID);
        MockitoAnnotations.openMocks(this);
        Mockito.when(entityManager.find(BatchStatus.class, BATCH_ID, LockModeType.PESSIMISTIC_WRITE)).thenReturn(batchStatus);
        batchRahmenDaoMock = new BatchRahmenDaoMock(entityManagerFactory, entityManager);
        batchRahmenDao = new BatchStatusDao(entityManagerFactory);
    }

    @Test(expected = BatchrahmenInitialisierungException.class)
    public void CreateBatchStatusErrorState() {
        batchRahmenDaoMock.createBatchStatus(batchStatus);
    }

    @Test(expected = BatchrahmenInitialisierungException.class)
    public void GetEntityManagerErrorState() {
        batchRahmenDao.getEntityManager();
    }
}
