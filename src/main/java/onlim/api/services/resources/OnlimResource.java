package onlim.api.services.resources;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import onlim.api.bridge.Bridge;
import onlim.api.generator.Template;
import onlim.api.generator.TemplateStore;
import onlim.api.parser.JsonLdParser;
import onlim.api.parser.ParsedSubstitutable;
import onlim.api.parser.SubstitutableGenerator;
import onlim.api.parser.Triple;

@Path("/")
public class OnlimResource {

	private final Logger LOGGER = LoggerFactory.getLogger(OnlimResource.class);

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
			LOGGER.info("-- Request to create Offer received -- ");
			final List<Triple> triples = new JsonLdParser().parse(is);
			entity = StringUtils.join(Bridge.gen(triples), "\n\n");
		} catch (final Exception e) {
			LOGGER.error("{}", e.getMessage(), e);
			entity = e.getMessage();
			status = Status.BAD_REQUEST;
		}
		return Response.status(status).entity(entity).build();
	}

	@POST
	@Consumes("application/json")
	@Path("convert/triple")
	public Response listTriple(InputStream is) {
		String entity = "";
		Status status = Status.OK;
		try {
			LOGGER.info("-- Request to list triples received");
			final List<Triple> triples = new JsonLdParser().parse(is);
			StringBuilder sb = new StringBuilder();
			for (Triple t : triples) {
				sb.append(t.toString()).append("\n");
			}

			entity = sb.toString();
		} catch (final Exception e) {
			LOGGER.error("{}", e.getMessage(), e);
			entity = e.getMessage();
			status = Status.BAD_REQUEST;
		}
		return Response.status(status).entity(entity).build();
	}

	@POST
	@Consumes("application/json")
	@Path("convert/subs")
	public Response listSubstitutes(InputStream is) {
		String entity = "";
		Status status = Status.OK;
		try {
			LOGGER.info("-- Request to list substitutes received -- ");
			final List<Triple> triples = new JsonLdParser().parse(is);
			SubstitutableGenerator subGenerator = new SubstitutableGenerator(triples);
			StringBuilder sb = new StringBuilder();

			for (ParsedSubstitutable sub : subGenerator.generateSubstitutables()) {
				sb.append(sub.toString()).append("\n");
			}
			entity = sb.toString();

		} catch (final Exception e) {
			LOGGER.error("{}", e.getMessage(), e);
			entity = e.getMessage();
			status = Status.BAD_REQUEST;
		}
		return Response.status(status).entity(entity).build();
	}

	@GET
	@Path("list/{lang: [a-zA-Z][a-zA-Z]+}/{type: [a-zA-Z]+}")
	@Produces("text/html")
	public Response listTemplates(@PathParam("lang") String language, @PathParam("type") String type) {
		String entity = "";
		Status status = Status.OK;
		try {
			StringBuilder sb = new StringBuilder();
			LOGGER.info("-- Request to list all templates received -- ");
			for (Template template : TemplateStore.get().getTemplates()) {
				if (!template.getMetaValue("language").toString().equalsIgnoreCase(language)) {
					continue;
				}
				
				if (!template.getMetaValue("schema_type").toString().toLowerCase().contains(type.toLowerCase())) {
					continue;
				}
				sb.append(templateToHtml(template));
			}
			entity = sb.toString();
		} catch (final Exception e) {
			LOGGER.error("{}", e.getMessage(), e);
			entity = e.getMessage();
			status = Status.BAD_REQUEST;
		}
		if (entity.length() > 0) {
			return Response.status(status).entity(entity).build();
		} else {
			return Response.status(Status.BAD_REQUEST)
					.entity("Could not find a template for " + language + " and type " + type).build();
		}
	}

	@GET
	@Path("list/{lang: [a-zA-Z][a-zA-Z]+}")
	@Produces("text/html")
	public Response listTemplates(@PathParam("lang") String language) {
		String entity = "";
		Status status = Status.OK;
		try {
			StringBuilder sb = new StringBuilder();
			LOGGER.info("-- Request to list all templates received -- ");
			for (Template template : TemplateStore.get().getTemplates()) {
				if (!template.getMetaValue("language").toString().equalsIgnoreCase(language)) {
					continue;
				}
				sb.append(templateToHtml(template));
			}
			entity = sb.toString();
		} catch (final Exception e) {
			LOGGER.error("{}", e.getMessage(), e);
			entity = e.getMessage();
			status = Status.BAD_REQUEST;
		}
		if (entity.length() > 0) {
			return Response.status(status).entity(entity).build();
		} else {
			return Response.status(Status.BAD_REQUEST)
					.entity("Could not find any templates for language:" + language).build();
		}
	}

	@GET
	@Path("list")
	@Produces("text/html")
	public Response listTemplates() {
		String entity = "";
		Status status = Status.OK;
		try {
			StringBuilder sb = new StringBuilder();
			LOGGER.info("-- Request to list all templates received -- ");
			for (Template template : TemplateStore.get().getTemplates()) {
				sb.append(templateToHtml(template));
			}
			entity = sb.toString();
		} catch (final Exception e) {
			LOGGER.error("{}", e.getMessage(), e);
			entity = e.getMessage();
			status = Status.BAD_REQUEST;
		}
		if (entity.length() > 0) {
			return Response.status(status).entity(entity).build();
		} else {
			return Response.status(Status.BAD_REQUEST)
					.entity("Could not find any templates").build();
		}
	}

	private String templateToHtml(Template template) {
		String type = "";
		String typeShort = "";
		StringBuilder sb = new StringBuilder();

		sb.append("<h1>").append("Template for ");
		type = template.getMetaValue("schema_type").toString().trim();
		typeShort = type.substring(1, type.length() - 1);
		type = "<a href=\"" + typeShort + "\">" + typeShort + "</a>";
		sb.append(type);
		sb.append("(").append(template.getMetaValue("language").toString()).append(")");
		sb.append("</h1>");
		sb.append(template.toString());
		sb.append("<br/>");
		return sb.toString();
	}
}
