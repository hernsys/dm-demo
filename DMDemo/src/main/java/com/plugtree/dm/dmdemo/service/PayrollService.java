package com.plugtree.dm.dmdemo.service;

import com.plugtree.dm.dmdemo.Employee;

public interface PayrollService {

	void deductSalaryWithTransportation(Employee emp, Integer leaveLength);
	
	void deductSalaryWithAllAllowances(Employee emp, Integer leaveLength);
}
