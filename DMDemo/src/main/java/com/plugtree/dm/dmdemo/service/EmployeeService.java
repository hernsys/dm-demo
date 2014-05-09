package com.plugtree.dm.dmdemo.service;

import java.util.concurrent.TimeUnit;

import com.plugtree.dm.dmdemo.Employee;
import com.plugtree.dm.dmdemo.LeaveType;

public interface EmployeeService {

	void suspendEmployeeAssignment(Employee emp);
	
	void setSalaryPercent(Employee emp, Double percent);
	
	boolean isValidInstitute(String institute);
	
	int getPendingDays(Employee emp, LeaveType type);
	
	int getUsedDays(Employee emp, LeaveType type, int period, TimeUnit unit);
}
