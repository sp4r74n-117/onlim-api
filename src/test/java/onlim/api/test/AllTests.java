package onlim.api.test;

import onlim.api.generator.test.TemplateBuilderTest;
import onlim.api.parser.test.ParserTest;
import onlim.api.reasoner.test.ReasonerTest;
import onlim.api.services.resources.OfferResourceTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	TemplateBuilderTest.class,
	ParserTest.class,
	OfferResourceTest.class,
	ReasonerTest.class
})
public class AllTests {

}
