package onlim.api.parser;

public class Triple {

	private String subject;
	private String predicate;
	private String object;
	
	public Triple(final String subject, final String predicate, final String object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	public String getSubject() {
		return subject;
	}

	public String getPredicate() {
		return predicate;
	}

	public String getObject() {
		return object;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		result = prime * result + ((predicate == null) ? 0 : predicate.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
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
		Triple other = (Triple) obj;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (!predicate.equals(other.predicate))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.subject + ", " + this.predicate + ", " + this.object;
	}
}
