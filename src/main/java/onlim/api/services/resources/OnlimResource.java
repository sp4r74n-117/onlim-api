package onlim.api.services.resources;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import onlim.api.bridge.Bridge;
import onlim.api.parser.JsonLdParser;
import onlim.api.parser.Triple;

@Path("/")
public class OnlimResource {
	@GET
	public String getOffers() {
		return "Online";
	}

	@POST
	@Consumes("application/json")
	public Response createOffer(InputStream is) {
		String entity = "";
		Status status = Status.OK;
		try {
			final List<Triple> triples = new JsonLdParser().parse(is);
			entity = String.join("\n", Bridge.gen(triples));
		} catch (final Exception e) {
			e.printStackTrace();
			entity = e.getMessage();
			status = Status.BAD_REQUEST;
		}
		return Response.status(status).entity(entity).build();
	}
}
