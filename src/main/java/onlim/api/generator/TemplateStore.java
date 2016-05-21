package onlim.api.generator;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static onlim.api.bridge.Bridge.pro;

public class TemplateStore {
	private final static TemplateStore INSTANCE = new TemplateStore();
	private final List<Template> templates;
	
	public static TemplateStore get() {
		return INSTANCE;
	}

	private TemplateBuilder newTemplate(final String name) {
		try {
			return new TemplateBuilder(getClass().getClassLoader(), name);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private TemplateStore() {
		this.templates = new LinkedList<>();
		this.templates.add(newTemplate("onlim/api/generator/resources/offer.template")
			.addMetaValue("language", "en")
			.addMetaValue("schema_type", "<http://schema.org/Offer>")
			.addMapping("NAME", pro("<http://schema.org/name>"))
			.addMapping("TEASER", pro("<http://schema.org/teaser>"))
			.addMapping("PRICE", pro("<http://schema.org/priceSpecification>", "<http://schema.org/PriceSpecification>", "<http://schema.org/price>"))
			.addMapping("CURR", pro("<http://schema.org/priceSpecification>", "<http://schema.org/PriceSpecification>", "<http://schema.org/priceCurrency>"))
			.addMapping("FROM", pro("<http://schema.org/priceSpecification>", "<http://schema.org/PriceSpecification>", "<http://schema.org/validFrom>"))
			.addMapping("TO", pro("<http://schema.org/priceSpecification>", "<http://schema.org/PriceSpecification>", "<http://schema.org/validThrough>"))
			.addMapping("TARGET", pro("<http://schema.org/potentialAction>", "<http://schema.org/BuyAction>", "<http://schema.org/target>"))
			.addMapping("URL_TEMPLATE", pro("<http://schema.org/potentialAction>", "<http://schema.org/BuyAction>", "<http://schema.org/target>", "<http://schema.org/EntryPoint>", "<http://schema.org/urlTemplate:>"))
			.build());
	}
	
	public <T extends Substitutable> List<Template> resolve(final Set<T> substitutables, final Resolver resolver) throws Exception {
		final List<Template> result = new LinkedList<>();
		for (final Template template : this.templates) {
			if (!template.isResolvable(substitutables)) continue;
			
			final Template cloned = template.clone();
			for (final Substitutable substitutable : substitutables)
				cloned.substitute(substitutable, resolver.resolve(substitutable));
			result.add(cloned);
		}
		return result;
	}
}
