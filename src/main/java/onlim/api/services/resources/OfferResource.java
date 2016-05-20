package onlim.api.services.resources;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.github.jsonldjava.core.JsonLdError;

import onlim.api.parser.JsonLdParser;
import onlim.api.parser.resources.Triple;

/**
 */
@Path("/")
public class OfferResource {


	@GET
	public String getOffers() {
		return "Online";
	}

	@POST
	@Consumes("application/json")
	public Response createOffer(InputStream is) {
		List<Triple> triples = null;
		try {
			triples = new JsonLdParser().parse(is);
		} catch (IOException e) {
			e.printStackTrace();
			return Response.status(400).entity(e.getMessage()).build();
		} catch (JsonLdError e) {
			e.printStackTrace();
			return Response.status(400).entity(e.getMessage()).build();
		}
		
		StringBuilder builder = new StringBuilder();
		for(Triple t : triples){
			builder.append(t.toString()).append("\n");
			// System.out.println(t.toString());
		}

		return Response.status(200).entity(builder.toString()).build();
	}
}
