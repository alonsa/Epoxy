package com.alon.main.server.rest;

import com.alon.main.server.enums.AggregationType;
import com.alon.main.server.enums.ErrorType;
import com.alon.main.server.MessageService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.List;

import static com.alon.main.server.conf.Conf.AGGREGATION_APPENDED;
import static com.alon.main.server.conf.Conf.AGGREGATION_COMBINED;

@Path("/epoxy.com/fetch/{arrayJson}")
public class RestImpl {

	//	http://localhost:8090/epoxy.com/fetch/W3siZmlyc3QiOiAiaHR0cHM6Ly9zYWZlLWlubGV0LTgxMDUuaGVyb2t1YXBwLmNvbS9wYXltZW50cyINCn0seyJzZWNvbmQiOiAiaHR0cHM6Ly9zYWZlLWlubGV0LTgxMDUuaGVyb2t1YXBwLmNvbS9wbGFucyINCn0seyJ0aGlyZCI6ICJodHRwczovL3NhZmUtaW5sZXQtODEwNS5oZXJva3VhcHAuY29tL3BheW91dCINCn0seyJmb3VydGgiOiAiQUxPTiINCn0seyJmaWZ0aCI6ICJodHRwczovL3NhZmUtaW5sZXQtODEwNS5oZXJva3VhcHAuY29tL2ZlZWQifV0=/appended

	@QueryParam("errors") protected List<ErrorType> errors;
	@QueryParam("timeout") protected Integer timeout;
	@PathParam("arrayJson") protected String jsonBase64;

	@GET
	@Path("/" + AGGREGATION_COMBINED)
	public Response getCombinedMsg() {

		String response = new MessageService().handleMessage(jsonBase64, timeout, getError(), AggregationType.APPENDED);

		return Response.status(200).entity(response).build();
	}

	@GET
	@Path("/" + AGGREGATION_APPENDED)
	public Response getAppendedMsg() {

		String response = new MessageService().handleMessage(jsonBase64, timeout, getError(), AggregationType.APPENDED);

		return Response.status(200).entity(response).build();
	}

	private ErrorType getError(){
		if (errors != null && !errors.isEmpty()){
			return errors.get(0);
		}else {
			return ErrorType.REPLACE;
		}

	}

}