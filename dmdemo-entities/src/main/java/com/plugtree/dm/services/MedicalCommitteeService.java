package com.plugtree.dm.services;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.plugtree.dm.entities.Employee;

@WebService
public interface MedicalCommitteeService {

    void startEmployeeMedicalReview(@WebParam(name = "Employee") Employee emp);
}
