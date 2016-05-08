package onlim.api.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 */
public class EntryServlet extends HttpServlet{

	private final Logger LOGGER = LoggerFactory.getLogger(EntryServlet.class);

	private final String baseUrl = "http://localhost:8080/";

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		super.doGet(req, resp);

		// TODO: 05/05/16 enhance logging
		LOGGER.info("GET request");
	}

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPost(req, resp);

		// TODO: 05/05/16 enhance logging
		LOGGER.info("POST request");
	}

	@Override
	protected void doPut(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPut(req, resp);

		// TODO: 05/05/16 enhance logging
		LOGGER.info("PUT request");
	}

	@Override
	protected void doDelete(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		super.doDelete(req, resp);

		// TODO: 05/05/16 enhance logging
		LOGGER.info("DELrequest");
	}
}
