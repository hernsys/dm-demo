package com.plugtree.dm.services;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.plugtree.dm.entities.Employee;

@WebService
public interface PayrollService {

    void deductSalaryWithTransportation(@WebParam(name = "Employee") Employee emp,
            @WebParam(name = "leaveLength") Integer leaveLength);

    void deductSalaryWithAllAllowances(@WebParam(name = "Employee") Employee emp,
            @WebParam(name = "leaveLength") Integer leaveLength);
}
