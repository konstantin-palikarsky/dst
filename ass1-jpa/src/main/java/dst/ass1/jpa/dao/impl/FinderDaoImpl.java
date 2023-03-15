package dst.ass1.jpa.dao.impl;

import dst.ass1.jpa.dao.GenericDAO;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.LinkedList;
import java.util.List;

public abstract class FinderDaoImpl<T, S extends T> implements GenericDAO<T> {

    protected Class<S> entityClass;

    @PersistenceContext
    protected EntityManager em;

    public FinderDaoImpl(Class<S> entityClass, EntityManager em) {
        super();
        this.entityClass = entityClass;
        this.em = em;
    }

    @Override
    public T findById(Long id) {

        return em.find(entityClass, id);
    }

    @Override
    public List<T> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        //TODO found this on SO, would be nice to understand it
        CriteriaQuery<S> cq = cb.createQuery(entityClass);
        Root<S> rootEntry = cq.from(entityClass);
        CriteriaQuery<S> all =  cq.select(rootEntry);
        TypedQuery<S> allQuery = em.createQuery(all);

        return new LinkedList<>(allQuery.getResultList());
    }
}
