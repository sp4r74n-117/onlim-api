package onlim.api.services;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

import onlim.api.services.resources.OfferResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
@ApplicationPath("/onlim-api")
public class OnlimApiApplication extends Application{

	private static final Logger LOGGER = LoggerFactory.getLogger(OnlimApiApplication.class);

	private final Set<Object> singletons = new HashSet<Object>();
	private final Set<Class<?>> classes = new HashSet<Class<?>>();

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
}
