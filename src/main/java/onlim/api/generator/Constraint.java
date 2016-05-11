package onlim.api.generator;

import java.util.Map;

public interface Constraint {
	public boolean evaluate(final Map<String, Object> data);
}
