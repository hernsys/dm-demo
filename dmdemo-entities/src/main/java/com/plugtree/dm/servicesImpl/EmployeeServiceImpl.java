package com.plugtree.dm.servicesImpl;

import java.util.concurrent.TimeUnit;

import javax.jws.WebService;

import com.plugtree.dm.entities.Employee;
import com.plugtree.dm.entities.LeaveType;
import com.plugtree.dm.services.EmployeeService;

@WebService(endpointInterface="com.plugtree.dm.services.EmployeeService")
public class EmployeeServiceImpl implements EmployeeService {

	@Override
	public void suspendEmployeeAssignment(Employee emp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSalaryPercent(Employee emp, Double percent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isValidInstitute(String institute) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getPendingDays(Employee emp, LeaveType type) {
		// TODO Auto-generated method stub
		return 0;
	}

    @Override
    public int getUsedDays(Employee emp, LeaveType type, int period, TimeUnit unit) {
        // TODO Auto-generated method stub
        return 0;
    }

}
