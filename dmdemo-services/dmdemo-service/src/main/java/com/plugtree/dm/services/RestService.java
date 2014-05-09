package com.plugtree.dm.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/restService")
@Produces("application/xml")
public interface RestService {

	@GET
	@Path("/getCustomerByIdJson/{id}")
	@Produces("application/json")
	// http://localhost:8081/cxf/projectRest/restService/getCustomerByIdJson/1/
	public String getNameByIdJson(@PathParam("id") Long id);

	@GET
	@Path("/getCustomerByIdXml/{id}")
	@Produces("application/xml")
	// http://localhost:8081/cxf/projectRest/restService/getCustomerByIdXml/1/
	public String getNameByIdXml(@PathParam("id") Long id);

}