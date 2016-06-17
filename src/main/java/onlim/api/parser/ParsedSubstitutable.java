package onlim.api.parser;

import java.util.LinkedList;
import java.util.List;

import onlim.api.generator.Substitutable;

public class ParsedSubstitutable extends Substitutable {
	private List<String> props;
	private String value;
	
	public ParsedSubstitutable() {
		this.props = new LinkedList<String>();
	}
	
	public void addProperty(String p) {
		this.props.add(p);
	}
	
	public void addProperties(List<String> pLst) {
		this.props.addAll(pLst);
	}
	
	public void setValue(String v) {
		this.value = v;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public List<String> getProperties() {
		return this.props;
	}
	
	@Override
	public String toString() {
		return props.toString() + " -> " + this.value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((props == null) ? 0 : props.hashCode());
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
		ParsedSubstitutable other = (ParsedSubstitutable) obj;
		if (props == null) {
			if (other.props != null)
				return false;
		} else if (!props.equals(other.props))
			return false;
		return true;
	}
}
