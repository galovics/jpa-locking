package com.arnoldgalovics.blog.jpalocking;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class TestHelper {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void reset() {
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
        entityManager.getMetamodel().getEntities().forEach(type -> {
            String entityName = type.getName();
            entityManager.createQuery(String.format("DELETE FROM %s", entityName)).executeUpdate();
        });
        entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
    }
}
