package timeSeries;

public class Utils {

	// Method to create observation array out of array of doubles
	public static Observation[] createObsArray(double[] values) {
		Observation[] observations = new Observation[values.length];
		for(int i = 0; i<values.length; i++) {
			observations[i] = new Observation(i, values[i]);
		}
		return observations;
	}
}
