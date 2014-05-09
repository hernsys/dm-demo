package com.plugtree.dm.services;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService
public interface SoaService {

	@WebMethod(operationName = "getNameById")
	@WebResult(name = "Proyecto")
	public String getNameById(@WebParam(name = "id") String id);

	

}
