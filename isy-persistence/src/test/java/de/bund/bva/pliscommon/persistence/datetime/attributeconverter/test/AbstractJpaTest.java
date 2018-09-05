package de.bund.bva.pliscommon.persistence.datetime.attributeconverter.test;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/testdao.xml")
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    TransactionDbUnitTestExecutionListener.class })
@Transactional
public abstract class AbstractJpaTest {

    @PersistenceContext
    protected EntityManager entityManager;

    @After
    public void commit() {
        entityManager.flush();
    }
}