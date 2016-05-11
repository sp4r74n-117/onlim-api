package onlim.api.generator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(TemplateBuilder.class);
	
	private final String template;
	private final Map<String, Substitutable> substitutables;
	private final Map<String, Object> metas;
	private final List<Constraint> constraints;
	
	public TemplateBuilder(final String template) {
		this.template = template;
		this.substitutables = new HashMap<>();
		this.metas = new HashMap<>();
		this.constraints = new LinkedList<>();
	}
	
	public TemplateBuilder(final ClassLoader context, final String name) throws IOException {
		final InputStream in = this.getClass().getClassLoader().getResourceAsStream(name);
		if (in == null) {
			throw new IOException("failed to locate resource '" + name + "'");
		}
		
		final StringBuilder sb = new StringBuilder();
		// capture @in in try-resource in order to automatically close it
		try (final InputStream is = in; final Reader rd = new InputStreamReader(is, Charset.defaultCharset())) {
			int numOfBytes;
			final char[] buffer = new char[512];
			
			while (true) {
				numOfBytes = rd.read(buffer);
				if (numOfBytes < 0)
					break;
				
				sb.append(buffer, 0, numOfBytes);
			}
		}
		this.template = sb.toString();
		this.substitutables = new HashMap<>();
		this.metas = new HashMap<>();
		this.constraints = new LinkedList<>();
	}
	
	public TemplateBuilder addMapping(final String placeholder, final Substitutable substitutable) {
		final Substitutable oldMapping = this.substitutables.put(placeholder, substitutable);
		if (oldMapping != null) {
			LOGGER.warn("placeholder '{}' was already mapped to '{}'", placeholder, oldMapping.toString());
		}
		return this;
	}
	
	public TemplateBuilder addMetaValue(final String key, final Object value) {
		final Object oldMapping = this.metas.put(key, value);
		if (oldMapping != null) {
			LOGGER.warn("meta key '{}' was already mapped to '{}'", key, value.toString());
		}
		return this;
	}
	
	public TemplateBuilder addConstraint(final Constraint constraint) {
		this.constraints.add(constraint);
		return this;
	}
	
	public TemplateBuilder rewind() {
		this.substitutables.clear();
		this.metas.clear();
		this.constraints.clear();
		return this;
	}
	
	public Template build() {
		return new Template(template, substitutables, metas, constraints);
	}
}
