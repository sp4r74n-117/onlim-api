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

import onlim.api.generator.Resolver;
import onlim.api.generator.Substitutable;
import onlim.api.generator.Template;
import onlim.api.generator.TemplateStore;
import onlim.api.parser.JsonLdParser;
import onlim.api.parser.SubstitutableGenerator;
import onlim.api.parser.resources.ParsedSubstitutable;
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
		
		SubstitutableGenerator sg = new SubstitutableGenerator(triples);
		List<Template> templates;
		try {
			templates = TemplateStore.get().resolve(sg.generateSubstitutables(), new Resolver() {
				@Override
				public String resolve(Substitutable substitutable) throws Exception {
					return ParsedSubstitutable.class.cast(substitutable).getValue();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(400).entity(e.getMessage()).build();
		}
		
		if (templates.isEmpty())
			return Response.status(200).build();
		
		return Response.status(200).entity(templates.get(0).toString()).build();
	}
}
