package onlim.api.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;

/**
 * This class parses an input file or string (in JSON LD format) and produces an application/nquads RDF, for which 
 * it generates a list of {@link Triple}
 * Note that this class is just a simple wrapper with some extensions of the http://json-ld.org/ lib
 */
public class JsonLdParser {

	private final Logger LOGGER = LoggerFactory.getLogger(JsonLdParser.class);

	public JsonLdParser() {}

	/**
	 * parse function to process an input stream
	 * @param input
	 * @return list of {@link Triple}
	 * @throws IOException
	 * @throws JsonLdError
	 */
	public List<Triple> parse(final InputStream input) throws IOException, JsonLdError {
		return parse(generateJson(input));
	}

	/**
	 * parse function to process a string
	 * @param input
	 * @return list of {@link Triple}
	 * @throws IOException
	 * @throws JsonLdError
	 */
	public List<Triple> parse(final String input) throws IOException, JsonLdError {
		return parse(generateJson(input));
	}

	/**
	 * this function produces a list of {@link Triple} out of application/nquads RDF
	 * @param json - JSON Object
	 * @return list of {@link Triple}
	 * @throws JsonLdError
	 */
	private List<Triple> parse(final Object json) throws JsonLdError {
		List<Triple> result = new LinkedList<>();
		String rdf = generateRDF(json);
		Triple tri = null;

		LOGGER.info("Parsing json, found following triples:");
		for (String t : rdf.split("\n")) {
			// this regex simply splits the string by space, but does not split at spaces in between quotes
			String[] triple = t.split(" (?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
			if (triple.length != 4) {
				throw new IllegalArgumentException("Error at generating triples");
			}
			tri = buildTriple(triple[0], triple[1], triple[2]);
			result.add(tri);
			LOGGER.info("{}", tri);
		}

		return result;
	}

	/**
	 * this function generates an application/nquads RDF out of a JSON Object
	 * @param json
	 * @return application/nquads RDF
	 * @throws JsonLdError
	 */
	private String generateRDF(final Object json) throws JsonLdError {
		return JsonLdProcessor.toRDF(json, getDefaultOptions()).toString();
	}

	/**
	 * this function generates a JSON Object out of a String
	 * @param input
	 * @return JSON Object
	 * @throws JsonParseException
	 * @throws IOException
	 */
	private Object generateJson(final String input) throws JsonParseException, IOException {
		return JsonUtils.fromString(input);
	}

	/**
	 * this function generates a JSON Object out of a input stream
	 * @param input
	 * @return JSON Object
	 * @throws JsonParseException
	 * @throws IOException
	 */
	private Object generateJson(final InputStream input) throws IOException {
		return JsonUtils.fromInputStream(input);
	}

	/**
	 * this function return a default JsonLdOptions object using the application/nquads format
	 * @return JsonLdOptions
	 */
	private JsonLdOptions getDefaultOptions() {
		JsonLdOptions options = new JsonLdOptions();
		options.format = "application/nquads";
		return options;
	}

	/**
	 * this function simply build a Triple object
	 * @param subject of the triple
	 * @param predicate of the triple
	 * @param object of the triple
	 * @return Triple Object
	 */
	private Triple buildTriple(final String subject, final String predicate, final String object) {
		return new Triple(subject, predicate, object);
	}
}
