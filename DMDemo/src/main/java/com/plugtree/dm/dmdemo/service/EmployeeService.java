package com.plugtree.dm.dmdemo.service;

import com.plugtree.dm.dmdemo.Employee;

public interface EmployeeService {

	void suspendEmployeeAssignment(Employee emp);
	
	void setSalaryPercent(Employee emp, Double percent);
	
	boolean isValidInstitute(String institute);
}
