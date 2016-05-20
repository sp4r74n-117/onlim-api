package onlim.api.services;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import onlim.api.services.resources.OfferResource;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
@ApplicationPath("/onlim-api")
public class OnlimApiApplication extends Application{

	private static final Logger LOGGER = LoggerFactory.getLogger(OnlimApiApplication.class);
    public static final String BASE_URI = "http://localhost:8080/onlim-api/";

	private final Set<Object> singletons = new HashSet<Object>();
	private final Set<Class<?>> classes = new HashSet<Class<?>>();
	
    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.example package
        final ResourceConfig rc = new ResourceConfig().packages("onlim.api.services");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

	public OnlimApiApplication() {
		// providers
		//singletons.add(XY);

		//resources
		classes.add(OfferResource.class);

	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}

	@Override
	public Set<Class<?>> getClasses() {
		return classes;
	}
	
    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%s\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();
    }
}
