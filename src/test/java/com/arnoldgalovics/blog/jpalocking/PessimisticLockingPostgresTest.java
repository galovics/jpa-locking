package com.arnoldgalovics.blog.jpalocking;

import com.arnoldgalovics.blog.jpalocking.repository.Product;
import com.google.common.collect.ImmutableMap;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.LockModeType;
import javax.persistence.LockTimeoutException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureEmbeddedDatabase
public class PessimisticLockingPostgresTest {
    @Autowired
    private TransactionalRunner txRunner;

    @Autowired
    private TestHelper helper;

    @After
    public void tearDown() {
        helper.resetPostgres();
    }

    @Test
    public void testSharedLockCanBeAcquired() {
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

    @Test
    public void testSharedLockCanBeAcquiredByMultipleReaders() {
        // given
        Product p = new Product("Notebook", 5);
        txRunner.doInTransaction(em -> {
            em.persist(p);
        });
        // when
        Product result = txRunner.doInTransaction(em1 -> {
            txRunner.doInTransaction(em2 -> {
                txRunner.doInTransaction(em3 -> {
                    return em3.find(Product.class, p.getId(), LockModeType.PESSIMISTIC_READ);
                });
                return em2.find(Product.class, p.getId(), LockModeType.PESSIMISTIC_READ);
            });
            return em1.find(Product.class, p.getId(), LockModeType.PESSIMISTIC_READ);

        });
        // then
        assertThat(result).isNotNull();
    }

    @Test(expected = LockTimeoutException.class)
    public void testExclusiveLockCantBeAcquiredWhenSharedLockAlreadyPresent() {
        // given
        Product p = new Product("Notebook", 5);
        txRunner.doInTransaction(em -> {
            em.persist(p);
        });
        // when
        Product result = txRunner.doInTransaction(em1 -> {
            Product product = em1.find(Product.class, p.getId(), LockModeType.PESSIMISTIC_READ);
            txRunner.doInTransaction(em2 -> {
                return em2.find(Product.class, p.getId(), LockModeType.PESSIMISTIC_WRITE, ImmutableMap.of("javax.persistence.lock.timeout", 0));
            });
            return product;

        });
        // then exception thrown
    }

    @Test
    public void testSharedLockCanBeAcquiredAfterFetching() {
        // given
        Product p = new Product("Notebook", 5);
        txRunner.doInTransaction(em -> {
            em.persist(p);
        });
        // when
        Product result = txRunner.doInTransaction(em -> {
            Product product = em.find(Product.class, p.getId());
            em.lock(product, LockModeType.PESSIMISTIC_READ);
            return product;
        });
        // then
        assertThat(result).isNotNull();
    }

    @Test
    public void testSharedLockCanBeAcquiredForQuery() {
        // given
        Product p = new Product("Notebook", 5);
        txRunner.doInTransaction(em -> {
            em.persist(p);
        });
        // when
        List<Product> result = txRunner.doInTransaction(em -> {
            return em.createQuery("FROM Product", Product.class).setLockMode(LockModeType.PESSIMISTIC_READ).getResultList();
        });
        // then
        assertThat(result).isNotEmpty();
    }
}
