package com.plugtree.dm.services;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.concurrent.TimeUnit;

import com.plugtree.dm.entities.Employee;
import com.plugtree.dm.entities.LeaveType;

@WebService
public interface EmployeeService {

    @WebMethod(operationName = "suspendEmployeeAssignment")
    void suspendEmployeeAssignment(@WebParam(name = "Employee") Employee emp);

    @WebMethod(operationName = "setSalaryPercent")
    void setSalaryPercent(@WebParam(name = "Employee") Employee emp, @WebParam(name = "percent") Double percent);

    @WebMethod(operationName = "isValidInstitute")
    boolean isValidInstitute(@WebParam(name = "institute") String institute);

    @WebMethod(operationName = "getPendingDays")
    int getPendingDays(@WebParam(name = "Employee") Employee emp, @WebParam(name = "LeaveType") LeaveType type);

    int getUsedDays(@WebParam(name = "Employee") Employee emp, @WebParam(name = "LeaveType") LeaveType type,
            @WebParam(name = "period") int period, @WebParam(name = "unit") TimeUnit unit);
}
