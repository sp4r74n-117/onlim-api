package onlim.api.services.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.net.URI;

/**
 */
@Path("/")
public class OfferResource {

	/*
	@GET
	@Produces("text/plain")
	public Response getOffers() {
		return Response.ok("Foobar").build();
	}
	*/
	@GET
	public String getOffers() {
		return "foobar";
	}

	@POST
	@Consumes("application/json")
	public Response createOffer() {
		return Response.created(URI.create("/offer/1")).build();

	}
}
