package onlim.api.parser.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.jsonldjava.core.JsonLdError;

import onlim.api.parser.JsonLdParser;
import onlim.api.parser.SubstitutableGenerator;
import onlim.api.parser.resources.Triple;

public class ParserTest {
	
	JsonLdParser parser;
	SubstitutableGenerator gen;
	List<Triple> triples;
	
	@Before
	public void setUp() {
		parser = new JsonLdParser();
		try {
			triples = parser.parse(getClass().getClassLoader().getResourceAsStream("onlim/api/parser/test/resources/input.json"));
		} catch (IOException | JsonLdError e) {
			e.printStackTrace();
		}
		
		assertNotEquals(null, triples);
		gen = new SubstitutableGenerator(triples);
	}
	
	@Test
	public void testTriples() {
		assertEquals(triples.size(), 35);
		assertEquals("<http://tourpack.redlink.io/seekda/2015/S000128>, <http://schema.org/makesOffer>, _:b0", triples.get(0).toString());
		assertEquals("_:b0, <http://schema.org/image>, _:b5", triples.get(1).toString());
		assertEquals("_:b0, <http://schema.org/itemOffered>, _:b1", triples.get(2).toString());
		assertEquals("_:b0, <http://schema.org/itemOffered>, _:b4", triples.get(3).toString());
		assertEquals("_:b0, <http://schema.org/name>, \"Alpine Wellness\"@de", triples.get(4).toString());
		assertEquals("_:b0, <http://schema.org/name>, \"Alpine Wellness\"@en", triples.get(5).toString());
		assertEquals("_:b0, <http://schema.org/name>, \"Ben-essere Dolomiti\"@it", triples.get(6).toString());
		assertEquals("_:b0, <http://schema.org/potentialAction>, _:b2", triples.get(7).toString());
		assertEquals("_:b0, <http://schema.org/priceSpecification>, _:b6", triples.get(8).toString());
		assertEquals("_:b0, <http://schema.org/serialNumber>, \"S000128PB1\"", triples.get(9).toString());
		assertEquals("_:b0, <http://schema.org/teaser>, \"Facial 24.7&nbsp; Spa Manicure&nbsp; Revitalising Body Scrub&nbsp; Nourishing body massage\"@en", triples.get(10).toString());
		assertEquals("_:b0, <http://schema.org/teaser>, \"Gesichts 24.7&nbsp; Spa-Maniküre&nbsp; Revital Body Scrub&nbsp; Pflegende Körpermassage\"@de", triples.get(11).toString());
		assertEquals("_:b0, <http://schema.org/teaser>, \"Trattamento viso 24.7 Manicure Spa Scrub corpo vitalizzante Massaggio corpo nutriente&nbsp;\"@it",triples.get(12).toString());;
		assertEquals("_:b0, <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>, <http://schema.org/Offer>",triples.get(13).toString());
		assertEquals("_:b1, <http://schema.org/name>, \"Massage\"@en",triples.get(14).toString());
		assertEquals("_:b1, <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>, <http://schema.org/Service>",triples.get(15).toString());
		assertEquals("_:b2, <http://schema.org/target>, \"http://www.fancyhotel.com/offer/buy/12345\"",triples.get(16).toString());
		assertEquals("_:b2, <http://schema.org/target>, _:b3",triples.get(17).toString());
		assertEquals("_:b2, <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>, <http://schema.org/BuyAction>",triples.get(18).toString());
		assertEquals("_:b3, <http://schema.org/contentType:>, \"application/json+ld\"",triples.get(19).toString());
		assertEquals("_:b3, <http://schema.org/urlTemplate:>, \"http://api.fancyhotel.com/offer/buy/12345\"",triples.get(20).toString());
		assertEquals("_:b3, <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>, <http://schema.org/EntryPoint>",triples.get(21).toString());
		assertEquals("_:b4, <http://schema.org/name>, \"Day Spa\"",triples.get(22).toString());
		assertEquals("_:b4, <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>, <http://schema.org/Service>",triples.get(23).toString());
		assertEquals("_:b5, <http://schema.org/caption>, \"Massage and Spa\"@de",triples.get(24).toString());
		assertEquals("_:b5, <http://schema.org/caption>, \"Massage and Spa\"@en",triples.get(25).toString());
		assertEquals("_:b5, <http://schema.org/caption>, \"Massaggio and Spa\"@it",triples.get(26).toString());
		assertEquals("_:b5, <http://schema.org/contentUrl>, \"https://images.seekda.net/S000128/H.Europeo-47.jpg?orig\"",triples.get(27).toString());
		assertEquals("_:b5, <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>, <http://schema.org/ImageObject>",triples.get(28).toString());
		assertEquals("_:b6, <http://schema.org/price>, \"2.2E2\"^^<http://www.w3.org/2001/XMLSchema#double>",triples.get(29).toString());
		assertEquals("_:b6, <http://schema.org/priceCurrency>, \"EUR\"",triples.get(30).toString());
		assertEquals("_:b6, <http://schema.org/validFrom>, \"2016-07-10T00:00:00+00:00\"^^<http://www.w3.org/2001/XMLSchema#dateTime>",triples.get(31).toString());
		assertEquals("_:b6, <http://schema.org/validThrough>, \"2015-06-18T09:00:08+00:00\"^^<http://www.w3.org/2001/XMLSchema#dateTime>",triples.get(32).toString());
		assertEquals("_:b6, <http://schema.org/valueAddedTaxIncluded>, \"true\"^^<http://www.w3.org/2001/XMLSchema#boolean>",triples.get(33).toString());
		assertEquals("_:b6, <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>, <http://schema.org/PriceSpecification>",triples.get(34).toString());
	}
	
	@Test
	public void testRoots() {
		assertEquals(1, gen.getRoots().size());
	}
	
	@Test
	public void testAssociatedTriples() {
		assertEquals(13, gen.getAssociatedTriples("_:b0").size());
		assertEquals(2, gen.getAssociatedTriples("_:b1").size());
		assertEquals(3, gen.getAssociatedTriples("_:b2").size());
		assertEquals(3, gen.getAssociatedTriples("_:b3").size());
		assertEquals(2, gen.getAssociatedTriples("_:b4").size());
		assertEquals(5, gen.getAssociatedTriples("_:b5").size());
		assertEquals(6, gen.getAssociatedTriples("_:b6").size());
	}
}
