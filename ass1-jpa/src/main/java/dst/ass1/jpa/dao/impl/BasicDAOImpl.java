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
    public T findById(Long id) {

        return em.find(targetClass, id);
    }

    @Override
    public List<T> findAll() {

        return new ArrayList<>(em.createQuery("FROM " + targetClass.getSimpleName(), targetClass).getResultList());
    }
}
