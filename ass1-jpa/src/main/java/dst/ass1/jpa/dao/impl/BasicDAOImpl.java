package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.GenericDAO;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public abstract class BasicDAOImpl<T> implements GenericDAO<T> {

    protected Class<? extends T> targetClass;

    protected EntityManager em;

    public BasicDAOImpl(Class<? extends T> targetClass, EntityManager em) {
        this.targetClass = targetClass;
        this.em = em;
    }

    @Override
    public boolean delete(Long id) {
        var entityToRemove = findById(id);

        if (entityToRemove == null) {
            return false;
        }

        try {
            em.remove(entityToRemove);
        } catch (IllegalArgumentException e) {
            return false;
        }

        return true;
    }

    @Override
    public T findById(Long id) {

        return em.find(targetClass, id);
    }

    public T save(T entity) {
        em.persist(entity);
        return entity;
    }

    @Override
    public List<T> findAll() {

        return new ArrayList<>(em.createQuery("FROM " + targetClass.getSimpleName(), targetClass).getResultList());
    }
}
