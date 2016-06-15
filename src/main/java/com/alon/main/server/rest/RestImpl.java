package com.alon.main.server.rest;

import com.alon.main.server.MessageService;
import com.alon.main.server.enums.AggregationType;
import com.alon.main.server.enums.ErrorType;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Base64;

import static com.alon.main.server.conf.Const.*;

@Path("/epoxy.com/fetch/{arrayJson}")
public class RestImpl {

	//	http://localhost:8090/epoxy.com/fetch/WyJodHRwczovL3NhZmUtaW5sZXQtODEwNS5oZXJva3VhcHAuY29tL3BheW1lbnRzIiwgImh0dHBzOi8vc2FmZS1pbmxldC04MTA1Lmhlcm9rdWFwcC5jb20vcGxhbnMiLCAiaHR0cHM6Ly9zYWZlLWlubGV0LTgxMDUuaGVyb2t1YXBwLmNvbS9mZWVkIiwgImh0dHBzOi8vc2FmZS1pbmxldC04MTA1Lmhlcm9rdWFwcC5jb20vcGF5b3V0Il0=/appended

	@PathParam("arrayJson") protected String jsonBase64;
	@DefaultValue(ERROR_REPLACE) @QueryParam("errors") protected ErrorType error;

	// I add a query param, In order to return with a base64 response, just add zip=true
	@DefaultValue("false") @QueryParam("zip") protected Boolean zip;
	@QueryParam("timeout") protected Integer timeout;

	@GET
	@Path("/" + AGGREGATION_COMBINED)
	public Response getCombinedMsg() {
		return getResponse(AggregationType.COMBINED);
	}

	@GET
	@Path("/" + AGGREGATION_APPENDED)
	public Response getAppendedMsg() {
		return getResponse(AggregationType.APPENDED);
	}

	private String zip(String str){
		String result;
		if (zip){
			result = Base64.getEncoder().encodeToString(str.getBytes());
		}else {
			result = str;
		}

		return result;
	}

	private Response getResponse(AggregationType aggregationType) {
		try {
			String response = new MessageService(error).handleMessage(jsonBase64, timeout, aggregationType);
			return Response.status(200).entity(zip(response)).build();
		}catch (Exception e){
			e.printStackTrace();
			return Response.status(500).entity(e.getMessage()).build();
		}
	}
}