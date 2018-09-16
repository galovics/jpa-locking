package com.arnoldgalovics.blog.jpalocking;

import com.arnoldgalovics.blog.jpalocking.repository.Product;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.LockModeType;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PessimisticLockingH2Test {
    @Autowired
    private TransactionalRunner txRunner;

    @Autowired
    private TestHelper helper;

    @After
    public void tearDown() {
        helper.reset();
    }

    @Test
    public void testExclusiveLockCanBeAcquired() {
        // given
        Product p = new Product("Notebook", 5);
        txRunner.doInTransaction(em -> {
            em.persist(p);
        });
        // when
        Product result = txRunner.doInTransaction(em -> {
            return em.find(Product.class, p.getId(), LockModeType.PESSIMISTIC_WRITE);
        });
        // then
        assertThat(result).isNotNull();
    }

    @Test
    public void testSharedLockCantBeAcquired() {
        // given
        Product p = new Product("Notebook", 5);
        txRunner.doInTransaction(em -> {
            em.persist(p);
        });
        // when
        Product result = txRunner.doInTransaction(em -> {
            return em.find(Product.class, p.getId(), LockModeType.PESSIMISTIC_READ);
        });
        // then
        assertThat(result).isNotNull();
    }
}
