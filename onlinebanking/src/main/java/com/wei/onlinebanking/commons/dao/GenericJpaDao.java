package com.wei.onlinebanking.commons.dao;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

import com.wei.onlinebanking.commons.domain.BaseEntity;

@Transactional
public abstract class GenericJpaDao<T, ID extends Serializable> implements
		GenericDao<T, ID> {

	private Class<T> persistentClass;

	private EntityManager entityManager;

	public GenericJpaDao(Class<T> persistentClass) {
		this.persistentClass = persistentClass;
	}

	protected EntityManager getEntityManager() {
		return entityManager;
	}

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public Class<T> getPersistentClass() {
		return persistentClass;
	}

	// save new record into database
	public T save(T entity) {
		getEntityManager().persist(entity);
		return entity;
	}

	// update existing record in the database
	public T update(T entity) {
		T mergedEntity = getEntityManager().merge(entity);
		return mergedEntity;
	}

	// delete record from database (for future use)
	public void delete(T entity) {
		if (BaseEntity.class.isAssignableFrom(persistentClass)) {
			getEntityManager().remove(
					getEntityManager().getReference(entity.getClass(),
							((BaseEntity) entity).getId()));
		} else {
			entity = getEntityManager().merge(entity);
			getEntityManager().remove(entity);
		}
	}

	// load record by its id
	@Transactional(readOnly = true)
	public T findById(ID id) {
		T entity = (T) getEntityManager().find(getPersistentClass(), id);
		return entity;
	}

}