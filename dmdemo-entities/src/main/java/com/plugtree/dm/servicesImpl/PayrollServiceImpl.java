package com.plugtree.dm.servicesImpl;

import javax.jws.WebService;

import com.plugtree.dm.entities.Employee;
import com.plugtree.dm.services.PayrollService;

@WebService(endpointInterface = "com.plugtree.dm.services.PayrollService")
public class PayrollServiceImpl implements PayrollService {

    @Override
    public void deductSalaryWithTransportation(Employee emp, Integer leaveLength) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deductSalaryWithAllAllowances(Employee emp, Integer leaveLength) {
        // TODO Auto-generated method stub

    }

}
