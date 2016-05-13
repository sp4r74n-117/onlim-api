package onlim.api.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.utils.JsonUtils;

import onlim.api.parser.resources.Triple;

public class JsonLdParser {

	private static final Logger LOGGER = LoggerFactory.getLogger(JsonLdParser.class);

	public JsonLdParser() {}

	public List<Triple> parse(final InputStream input) throws IOException, JsonLdError {
		return parse(generateJson(input));
	}

	public List<Triple> parse(final String input) throws IOException, JsonLdError {
		return parse(generateJson(input));
	}

	private List<Triple> parse(final Object json) throws JsonLdError {
		List<Triple> result = new LinkedList<>();
		String rdf = generateRDF(json);

		for (String t : rdf.split("\n")) {
			LinkedList<String> triple = new LinkedList<>(Arrays.asList(t.split(" (?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1)));
			if (triple.size() != 4) {
				throw new IllegalArgumentException("Error at generating tripples");
			}
			triple.removeLast();
			result.add(buildTriple(triple.get(0), triple.get(1), triple.get(2)));
		}

		return result;
	}

	private String generateRDF(final Object json) throws JsonLdError {
		return JsonLdProcessor.toRDF(json, getDefaultOptions()).toString();
	}

	private Object generateJson(final String input) throws JsonParseException, IOException {
		return JsonUtils.fromString(input);
	}

	private Object generateJson(final InputStream input) throws IOException {
		return JsonUtils.fromInputStream(input);
	}

	private JsonLdOptions getDefaultOptions() {
		JsonLdOptions options = new JsonLdOptions();
		options.format = "application/nquads";
		return options;
	}

	private Triple buildTriple(final String subject, final String predicate, final String object) {
		return new Triple(subject, predicate, object);
	}
}
