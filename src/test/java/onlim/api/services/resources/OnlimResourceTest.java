package onlim.api.services.resources;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import onlim.api.services.OnlimApiApplication;

public class OnlimResourceTest {
	private HttpServer server;
	private WebTarget target;
	
    @Before
    public void setUp() throws Exception {
        server = OnlimApiApplication.startServer();
        // create the client
        Client c = ClientBuilder.newClient();

        // uncomment the following line if you want to enable
        // support for JSON in the client (you also have to uncomment
        // dependency on jersey-media-json module in pom.xml and Main.startServer())
        // --
        // c.configuration().enable(new org.glassfish.jersey.media.json.JsonJaxbFeature());

        target = c.target(OnlimApiApplication.BASE_URI);
    }

    
	@After
	@SuppressWarnings("deprecation")
    public void tearDown() throws Exception {
        server.stop();
    }


	@Test 
	public void testResponse() {
		String responseMsg = target.request().get(String.class);
		assertEquals("Online", responseMsg);
	}

	@Test 
	public void testJsonPost() throws IOException {
		final String[] resources = {
			"onlim/api/parser/test/resources/offer.json",
			"onlim/api/parser/test/resources/event.json",
			"onlim/api/parser/test/resources/LodgingBusiness.json",
			"onlim/api/parser/test/resources/restaurant.json"
		};
		for (final String resource : resources) {
			postJson(resource);
		}
	}
	
	private void postJson(final String resource) throws IOException {
		InputStream jsonFile = getClass().getClassLoader().getResourceAsStream(resource);
		Scanner scanner = new Scanner(jsonFile);

		URL url = new URL(OnlimApiApplication.BASE_URI);
		URLConnection connection = url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setConnectTimeout(5000);
		connection.setReadTimeout(5000);
		OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
		out.write(scanner.useDelimiter("\\Z").next());
		out.close();
		scanner.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		String response;
		StringBuilder returnStr = new StringBuilder();
		while ((response = in.readLine()) != null) {
			returnStr.append(response);
		}
		in.close();
		assertTrue(returnStr.length() > 0);
	}
}
