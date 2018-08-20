package com.arnoldgalovics.blog.jpalocking;

import com.arnoldgalovics.blog.jpalocking.repository.Product;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LostUpdateTest {
    @Autowired
    private TransactionalRunner txRunner;

    @Autowired
    private TestHelper helper;

    @After
    public void tearDown() {
        helper.reset();
    }

    @Test
    public void testLostUpdate() {
        // given
        Product p = new Product("Notebook", 5);
        txRunner.doInTransaction(em -> {
            em.persist(p);
        });
        // when
        txRunner.doInTransaction(em1 -> {
            Product p1 = em1.find(Product.class, p.getId());
            txRunner.doInTransaction(em2 -> {
                Product p2 = em2.find(Product.class, p.getId());
                p2.setStock(p2.getStock() - 1);
            });
            p1.setStock(p1.getStock() - 1);
        });
        // then
        txRunner.doInTransaction(em -> {
            Product product = em.find(Product.class, p.getId());
            assertThat(product.getStock()).isEqualTo(4);
        });
    }
}
