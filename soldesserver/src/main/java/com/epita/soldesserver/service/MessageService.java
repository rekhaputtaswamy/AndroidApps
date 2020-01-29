package com.epita.soldesserver.service;
import javax.ws.rs.*;

@ApplicationPath("/")
public class MessageService {
    public static String TOKEN;
    
	@PUT @Path("/message") @Produces("application/json")
	public String getToken(@QueryParam("token") String token) {
		MessageService.TOKEN = token;
		return "";
	}
	
}
