package onlim.api.reasoner;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Allows to reason about the schema.org class path and to obtain
 * a fully resolved path up to the root THING
 */
public class Reasoner {
	/**
	 * Represents the top of the class path
	 */
	public static final String THING = "<http://schema.org/Thing>";
	/**
	 * Singleton instance of this class
	 */
	private static final Reasoner INSTANCE = new Reasoner();
	/**
	 * Logger instance used to print error and other diagnostic information
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(Reasoner.class);
	/**
	 * Directed graph which holds the hierarchy
	 */
	private final DirectedGraph<String, DefaultEdge> graph;
	/**
	 * Obtain a singleton instance of the reasoner
	 * 
	 * @return instance of the reasoner
	 */
	public static Reasoner get() {
		return INSTANCE;
	}
	/**
	 * Resolved e.g. <http://schema.org/Place> into a set of base classes
	 * 
	 * @param name Class to resolve
	 * @return
	 */
	public Set<String> getClassPath(final String name) {
		final Set<String> result = new HashSet<String>();
		collectVertices(name, result);
		return result;
	}
	/**
	 * Internal routine to walk through the hierarchy
	 * 
	 * @param vertex current vertex to process
	 * @param result current result set
	 */
	private void collectVertices(final String vertex, final Set<String> result) {
		// add the vertex itself to the result set
		result.add(vertex);		
		for (final DefaultEdge edge : this.graph.outgoingEdgesOf(vertex)) {
			collectVertices(this.graph.getEdgeTarget(edge), result);
		}
	}
	/**
	 * Constructs the reasoner and initializes the graph storage
	 */
	private Reasoner() {
		// allocate a new graph structure
		this.graph = new DefaultDirectedGraph<>(DefaultEdge.class);
		// load the extracted schema.org class hierarchies
		final String name = "onlim/api/reasoner/resources/schema.org.class.n3";
		final InputStream in = this.getClass().getClassLoader().getResourceAsStream(name);
		if (in == null) {
			throw new RuntimeException("failed to locate resource '" + name + "'");
		}
		
		// always add Thing to the graph as it is the root of all classes
		this.graph.addVertex(THING);
		// capture @in in try-resource in order to automatically close it
		try (final InputStream is = in; final Scanner sc = new Scanner(is)) {
			while (sc.hasNextLine()) {
				final String line = sc.nextLine();
				final String[] parts = line.split(" ");
				if (parts.length != 4) {
					LOGGER.error("faulty line in schema.org.class.n3: " + line);
					continue;
				}
				
				this.graph.addVertex(parts[0]);
				this.graph.addVertex(parts[2]);
				// add the edge to the graph structure
				this.graph.addEdge(parts[0], parts[2]);
			}
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
}
