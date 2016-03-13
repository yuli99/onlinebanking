package com.wei.onlinebanking.commons.domain;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseEntity implements Serializable {
	private static final long serialVersionUID = -7743876330751587451L;

	@Id
	@GeneratedValue
	private Long id;

	@Override
	public boolean equals(Object other) {
		return (other != null && getClass() == other.getClass() && id != null) ? id
				.equals(((BaseEntity) other).id) : (other == this);
	}

	@Override
	public int hashCode() {
		return (id != null) ? (getClass().hashCode() + id.hashCode()) : super
				.hashCode();
	}

	@Override
	public String toString() {
		return String.format("%s - ID %d", getClass().getSimpleName(), id);
	}

	// getter and setter
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
