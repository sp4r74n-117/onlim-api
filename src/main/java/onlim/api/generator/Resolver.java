package onlim.api.generator;

/**
 * Marker interface for classes which map a {@link onlim.api.generator.Substitutable}
 * to an abstract java.lang.String value.
 */
public interface Resolver {
	/**
	 * Maps a {@link onlim.api.generator.Substitutable} onto a java.lang.String value.
	 * 
	 * @param substitutable object which shall be mapped
	 * @return resolved value
	 * @throws Exception throw an exception if an error occurs
	 */
	public String resolve(final Substitutable substitutable) throws Exception;
}
