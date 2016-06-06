package onlim.api.generator;

import static onlim.api.bridge.Bridge.pro;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
		
		final Substitutable proName = pro("<http://schema.org/name>");
		final Substitutable proTeaser = pro("<http://schema.org/teaser>");
		final Substitutable proPrice = pro("<http://schema.org/priceSpecification>", "<http://schema.org/PriceSpecification>", "<http://schema.org/price>");
		final Substitutable proCurr = pro("<http://schema.org/priceSpecification>", "<http://schema.org/PriceSpecification>", "<http://schema.org/priceCurrency>");
		final Substitutable proFrom = pro("<http://schema.org/priceSpecification>", "<http://schema.org/PriceSpecification>", "<http://schema.org/validFrom>");
		final Substitutable proTo = pro("<http://schema.org/priceSpecification>", "<http://schema.org/PriceSpecification>", "<http://schema.org/validThrough>");
		final Substitutable proTarget = pro("<http://schema.org/potentialAction>", "<http://schema.org/BuyAction>", "<http://schema.org/target>");
		final Substitutable proUrlTemplate = pro("<http://schema.org/potentialAction>", "<http://schema.org/BuyAction>", "1", "<http://schema.org/target>", "<http://schema.org/EntryPoint>", "<http://schema.org/urlTemplate:>");
		this.templates.add(newTemplate("onlim/api/generator/resources/offer.en.template")
			.addMetaValue("language", "en")
			.addMetaValue("schema_type", "<http://schema.org/Offer>")
			.addMapping("NAME", proName)
			.addMapping("TEASER", proTeaser)
			.addMapping("PRICE", proPrice)
			.addMapping("CURR", proCurr)
			.addMapping("FROM", proFrom)
			.addMapping("TO", proTo)
			.addMapping("TARGET", proTarget)
			.addMapping("URL_TEMPLATE", proUrlTemplate)
			.build());
		this.templates.add(newTemplate("onlim/api/generator/resources/offer.de.template")
			.addMetaValue("language", "de")
			.addMetaValue("schema_type", "<http://schema.org/Offer>")
			.addMapping("NAME", proName)
			.addMapping("TEASER", proTeaser)
			.addMapping("PRICE", proPrice)
			.addMapping("CURR", proCurr)
			.addMapping("FROM", proFrom)
			.addMapping("TO", proTo)
			.addMapping("TARGET", proTarget)
			.addMapping("URL_TEMPLATE", proUrlTemplate)
			.build());
	}
	
	public <T extends Substitutable> List<Template> resolve(final List<T> substitutables, final Resolver resolver) throws Exception {
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
