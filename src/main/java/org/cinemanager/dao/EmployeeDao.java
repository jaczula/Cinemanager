package org.cinemanager.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import org.cinemanager.entity.Employee;

public class EmployeeDao extends Dao<Employee> {
	
	
	public List<Employee> getAllEmployees() {
		TypedQuery<Employee> query = getEntityManager().createQuery("select e from Employee e", Employee.class);
		return query.getResultList();
	}
}
