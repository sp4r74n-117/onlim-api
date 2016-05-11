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

/**
 * Builder class to construct a {@link onlim.api.generator.Template} object
 */
public class TemplateBuilder {
	private static final Logger LOGGER = LoggerFactory.getLogger(TemplateBuilder.class);
	
	private final String template;
	private final Map<String, Substitutable> substitutables;
	private final Map<String, Object> metas;
	private final List<Constraint> constraints;
	
	/**
	 * Construct a builder with a given text snippet
	 * 
	 * @param template text snipped
	 */
	public TemplateBuilder(final String template) {
		this.template = template;
		this.substitutables = new HashMap<>();
		this.metas = new HashMap<>();
		this.constraints = new LinkedList<>();
	}
	
	/**
	 * Constructs a builder by loading a resource reachable by the classloader
	 * 
	 * @param context ClassLoader to use for lookup
	 * @param name fully qualified path to requested resource
	 * @throws IOException
	 */
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
	
	/**
	 * Associates a given placeholder with a substitutable
	 * 
	 * @param placeholder placeholder
	 * @param substitutable substitutable
	 * @return reference to this 
	 */
	public TemplateBuilder addMapping(final String placeholder, final Substitutable substitutable) {
		final Substitutable oldMapping = this.substitutables.put(placeholder, substitutable);
		if (oldMapping != null) {
			LOGGER.warn("placeholder '{}' was already mapped to '{}'", placeholder, oldMapping.toString());
		}
		return this;
	}
	
	/**
	 * Attach a (key,value) pair to the {@link onlim.api.generator.Template}
	 * 
	 * @param key key
	 * @param value value
	 * @return reference to this
	 */
	public TemplateBuilder addMetaValue(final String key, final Object value) {
		final Object oldMapping = this.metas.put(key, value);
		if (oldMapping != null) {
			LOGGER.warn("meta key '{}' was already mapped to '{}'", key, value.toString());
		}
		return this;
	}
	
	/**
	 * Add an abstract constraint to the {@link onlim.api.generator.Template}
	 * 
	 * @param constraint constraint
	 * @return reference to this
	 */
	public TemplateBuilder addConstraint(final Constraint constraint) {
		this.constraints.add(constraint);
		return this;
	}
	
	/**
	 * Clears all associated mappings, meta values and constraints
	 * 
	 * @return reference to this
	 */
	public TemplateBuilder rewind() {
		this.substitutables.clear();
		this.metas.clear();
		this.constraints.clear();
		return this;
	}
	
	/**
	 * Construct the final {@link onlim.api.generator.Template}
	 * 
	 * @return constructed {@link onlim.api.generator.Template} object
	 */
	public Template build() {
		return new Template(template, substitutables, metas, constraints);
	}
}
