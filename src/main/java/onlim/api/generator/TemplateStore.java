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
		
		//OFFER
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
		
		//EVENT
		final Substitutable proEventDesc = pro("<http://schema.org/description>");
		final Substitutable proLocality = pro("<http://schema.org/location>", "<http://schema.org/Place>", "<http://schema.org/address>", "<http://schema.org/PostalAddress>", "<http://schema.org/addressLocality>");
		final Substitutable proRegion = pro("<http://schema.org/location>", "<http://schema.org/Place>", "<http://schema.org/address>", "<http://schema.org/PostalAddress>", "<http://schema.org/addressRegion>");
		final Substitutable proPlaceMap = pro("<http://schema.org/location>", "<http://schema.org/Place>", "<http://schema.org/hasMap>");
		final Substitutable proPlaceUrl = pro("<http://schema.org/location>", "<http://schema.org/Place>", "<http://schema.org/url>");
		final Substitutable proEventName = pro("<http://schema.org/name>");
		
		final Substitutable proOfferPrice = pro("<http://schema.org/offers>", "<http://schema.org/Offer>", "<http://schema.org/price>");
		final Substitutable proOfferCurr = pro("<http://schema.org/offers>", "<http://schema.org/Offer>", "<http://schema.org/priceCurrency>");
		final Substitutable proOfferUrl = pro("<http://schema.org/offers>", "<http://schema.org/Offer>", "<http://schema.org/url>");
		final Substitutable proOfferValidFrom = pro("<http://schema.org/offers>", "<http://schema.org/Offer>", "<http://schema.org/validFrom>");
		final Substitutable proOfferValidThrough = pro("<http://schema.org/offers>", "<http://schema.org/Offer>", "<http://schema.org/validThrough>");
		
		final Substitutable proOfferPrice1 = pro("1", "<http://schema.org/offers>", "<http://schema.org/Offer>", "<http://schema.org/price>");
		final Substitutable proOfferCurr1 = pro("1", "<http://schema.org/offers>", "<http://schema.org/Offer>", "<http://schema.org/priceCurrency>");
		final Substitutable proOfferUrl1 = pro("1", "<http://schema.org/offers>", "<http://schema.org/Offer>", "<http://schema.org/url>");
		final Substitutable proOfferValidFrom1 = pro("1", "<http://schema.org/offers>", "<http://schema.org/Offer>", "<http://schema.org/validFrom>");
		final Substitutable proOfferValidThrough1 = pro("1", "<http://schema.org/offers>", "<http://schema.org/Offer>", "<http://schema.org/validThrough>");
		
		final Substitutable proOfferPrice2 = pro("2", "<http://schema.org/offers>", "<http://schema.org/Offer>", "<http://schema.org/price>");
		final Substitutable proOfferCurr2 = pro("2", "<http://schema.org/offers>", "<http://schema.org/Offer>", "<http://schema.org/priceCurrency>");
		final Substitutable proOfferUrl2 = pro("2", "<http://schema.org/offers>", "<http://schema.org/Offer>", "<http://schema.org/url>");
		final Substitutable proOfferValidFrom2 = pro("2", "<http://schema.org/offers>", "<http://schema.org/Offer>", "<http://schema.org/validFrom>");
		final Substitutable proOfferValidThrough2 = pro("2", "<http://schema.org/offers>", "<http://schema.org/Offer>", "<http://schema.org/validThrough>");
		
		final Substitutable proSponsorName = pro("<http://schema.org/sponsor>", "<http://schema.org/Organization>", "<http://schema.org/name>");
		final Substitutable proSponsorUrl = pro("<http://schema.org/sponsor>", "<http://schema.org/Organization>", "<http://schema.org/url>");
		final Substitutable proEventStartDate = pro("<http://schema.org/startDate>");
		this.templates.add(newTemplate("onlim/api/generator/resources/event.en.template")
			.addMetaValue("language", "en")
			.addMetaValue("schema_type", "<http://schema.org/Event>")
			.addMapping("DESC", proEventDesc)
			.addMapping("LOC", proLocality)
			.addMapping("REGION", proRegion)
			.addMapping("PLACE_MAP", proPlaceMap)
			.addMapping("PLACE_URL", proPlaceUrl)
			.addMapping("NAME", proEventName)			
			.addMapping("OFFER_PRICE", proOfferPrice)
			.addMapping("OFFER_CURR", proOfferCurr)
			.addMapping("OFFER_URL", proOfferUrl)
			.addMapping("OFFER_VALIDFROM", proOfferValidFrom)
			.addMapping("OFFER_VALIDTHROUGH", proOfferValidThrough)			
			.addMapping("OFFER_1_PRICE", proOfferPrice1)
			.addMapping("OFFER_1_CURR", proOfferCurr1)
			.addMapping("OFFER_1_URL", proOfferUrl1)
			.addMapping("OFFER_1_VALIDFROM", proOfferValidFrom1)
			.addMapping("OFFER_1_VALIDTHROUGH", proOfferValidThrough1)			
			.addMapping("OFFER_2_PRICE", proOfferPrice2)
			.addMapping("OFFER_2_CURR", proOfferCurr2)
			.addMapping("OFFER_2_URL", proOfferUrl2)
			.addMapping("OFFER_2_VALIDFROM", proOfferValidFrom2)
			.addMapping("OFFER_2_VALIDTHROUGH", proOfferValidThrough2)			
			.addMapping("SPONSOR_NAME", proSponsorName)
			.addMapping("SPONSOR_URL", proSponsorUrl)
			.addMapping("START_DATE", proEventStartDate)
			.build());
		this.templates.add(newTemplate("onlim/api/generator/resources/event.de.template")
			.addMetaValue("language", "de")
			.addMetaValue("schema_type", "<http://schema.org/Event>")
			.addMapping("DESC", proEventDesc)
			.addMapping("LOC", proLocality)
			.addMapping("REGION", proRegion)
			.addMapping("PLACE_MAP", proPlaceMap)
			.addMapping("PLACE_URL", proPlaceUrl)
			.addMapping("NAME", proEventName)			
			.addMapping("OFFER_PRICE", proOfferPrice)
			.addMapping("OFFER_CURR", proOfferCurr)
			.addMapping("OFFER_URL", proOfferUrl)
			.addMapping("OFFER_VALIDFROM", proOfferValidFrom)
			.addMapping("OFFER_VALIDTHROUGH", proOfferValidThrough)			
			.addMapping("OFFER_1_PRICE", proOfferPrice1)
			.addMapping("OFFER_1_CURR", proOfferCurr1)
			.addMapping("OFFER_1_URL", proOfferUrl1)
			.addMapping("OFFER_1_VALIDFROM", proOfferValidFrom1)
			.addMapping("OFFER_1_VALIDTHROUGH", proOfferValidThrough1)			
			.addMapping("OFFER_2_PRICE", proOfferPrice2)
			.addMapping("OFFER_2_CURR", proOfferCurr2)
			.addMapping("OFFER_2_URL", proOfferUrl2)
			.addMapping("OFFER_2_VALIDFROM", proOfferValidFrom2)
			.addMapping("OFFER_2_VALIDTHROUGH", proOfferValidThrough2)			
			.addMapping("SPONSOR_NAME", proSponsorName)
			.addMapping("SPONSOR_URL", proSponsorUrl)
			.addMapping("START_DATE", proEventStartDate)
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
