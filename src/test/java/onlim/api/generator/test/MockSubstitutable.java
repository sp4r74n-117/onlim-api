package onlim.api.generator.test;

import onlim.api.generator.Substitutable;

public class MockSubstitutable extends Substitutable {
	private final String property;
	
	MockSubstitutable(final String property) {
		this.property = property;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((property == null) ? 0 : property.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MockSubstitutable other = (MockSubstitutable) obj;
		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.equals(other.property))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return property;
	}
}
