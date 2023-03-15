package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.GenericDAO;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public abstract class BasicDAOImpl<T> implements GenericDAO<T> {

    protected Class<? extends T> entityClass;

    protected EntityManager em;

    public BasicDAOImpl(Class<? extends T> entityClass, EntityManager em) {
        this.entityClass = entityClass;
        this.em = em;
    }

    @Override
    public T findById(Long id) {

        return em.find(entityClass, id);
    }

    @Override
    public List<T> findAll() {

        return new ArrayList<>(em.createQuery("FROM " + entityClass.getSimpleName(), entityClass).getResultList());
    }
}
