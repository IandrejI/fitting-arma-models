package timeSeries;

/**
 * The class ‘observation’ provides the data structure for the input data on the lowest granularity. 
 * That means, each observation is represented by an individual instance of ‘observation’. 
 * A structural decision worth mentioning is, that each observation knows the p previous values, as well as the q previous errors. 
 * This structure appears to be convenient for the calculation of the predictions.
 * @author Christoph Barkey 
 * @author Andrej Muschke
 *
 */

public class Observation {
	
	// Fields
	private int index;
	private double value;
	private double prediction;
	private double error = 0;
	private double[] prevPValues;
	private double[] prevQErrors;
	
	/** Constructor
	 * The constructor for an instance of ‘observation’. 
	 * It takes the index and value as input values.
	 * @param index the index of the respective observation
	 * @param value the actual observation value
	 */
	public Observation(int index, double value) {
		this.index = index;
		this.value = value;
	}

	
	// Getters and Setters
	public double getPrediction() {
		return prediction;
	}

	public void setPrediction(double prediction) {
		this.prediction = prediction;
	}

	public double getError() {
		return error;
	}
	
	/**
	 * Calculate error with e_t = x_t - xHat_t
	 * @param error
	 */
	public void setError() {
		this.error = value-prediction;
	}
	
	/**
	 *  Set error with double value
	 * @param error
	 */
	public void setError(double error) {
		this.error = error;
	}

	public int getIndex() {
		return index;
	}

	public double getValue() {
		return value;
	}


	public void setValue(double value) {
		this.value = value;
	}


	public double[] getPrevPValues() {
		return prevPValues;
	}


	public void setPrevPValues(double[] prevPValues) {
		this.prevPValues = prevPValues;
	}


	public double[] getPrevQErrors() {
		return prevQErrors;
	}


	public void setPrevQErrors(double[] prevQErrors) {
		this.prevQErrors = prevQErrors;
	}
	

}
