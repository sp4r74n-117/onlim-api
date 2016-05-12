package onlim.api.generator;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Abstract marker interface used by {@link onlim.api.generator.Template}
 * to replace certain placeholders with a given value
 */
public abstract class Substitutable {
	private final List<Constraint> constraints;
	
	public Substitutable() {
		this.constraints = new LinkedList<Constraint>();
	}
	
	/**
	 * Add an additional constraint to this object
	 * 
	 * @param constraint constraint to add
	 */
	public void addConstraint(final Constraint constraint) {
		this.constraints.add(constraint);
	}
	
	/**
	 * Checks whether all associated constraints are satisfied
	 * 
	 * @return true iff all constraints are satisfied
	 */
	public boolean isSatisfiable(final Map<String, Object> data) {
		for (final Constraint c : constraints) {
			if (!c.evaluate(data))
				return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((constraints == null) ? 0 : constraints.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Substitutable other = (Substitutable) obj;
		if (constraints == null) {
			if (other.constraints != null)
				return false;
		} else if (!constraints.equals(other.constraints))
			return false;
		return true;
	}
}
