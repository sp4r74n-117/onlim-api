package onlim.api.generator;

import java.util.Map;

/**
 * Models an abstract constraint which influences the satisfiability of a
 * given {@link onlim.api.generator.Template} object based on (key,value) pairs.
 */
public interface Constraint {
	/**
	 * This function facilitates the main method of {@link onlim.api.generator.Constraint}
	 * 
	 * @param data input data to evaluate
	 * @return true iff the constraint is fulfilled
	 */
	public boolean evaluate(final Map<String, Object> data);
}
