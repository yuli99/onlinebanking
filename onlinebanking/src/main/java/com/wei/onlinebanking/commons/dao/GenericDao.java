package com.wei.onlinebanking.commons.dao;

import java.io.Serializable;

public interface GenericDao<T, ID extends Serializable> {

	// save new record into database
	T save(T entity);

	// update existing record in the database
	T update(T entity);

	// delete record from database (for future use)
	void delete(T entity);

	// load record by its id
	T findById(ID id);

}
