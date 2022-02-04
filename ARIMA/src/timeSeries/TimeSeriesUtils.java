package timeSeries;
/**
 * Useful methods to make life more easy.
 * @author Christoph Barkey 
 * @author Andrej Muschke
 *
 */

public class TimeSeriesUtils {
	
	/**
	 * Creates an array of {@link Observation#Observation(int, double)} out of an array of doubles.
	 * Is used to prepare the input data more conveniently.
	 * @param values array of doubles (eg. the actual observation value)
	 * @return array of observations
	 */
	public static Observation[] createObsArray(double[] values) {
		Observation[] observations = new Observation[values.length];
		for(int i = 0; i<values.length; i++) {
			observations[i] = new Observation(i, values[i]);
		}
		return observations;
	}
}
