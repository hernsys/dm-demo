package com.plugtree.dm.servicesImpl;

import javax.jws.WebService;

import com.plugtree.dm.entities.Employee;
import com.plugtree.dm.services.MedicalCommitteeService;

@WebService(endpointInterface="com.plugtree.dm.services.MedicalCommitteeService")
public class MedicalCommitteeServiceImpl implements MedicalCommitteeService {

	@Override
	public void startEmployeeMedicalReview(Employee emp) {
		// TODO Auto-generated method stub
		
	}

}
