package timeSeries;

public class Observation {
	
	// Fields
	private int index;
	private double value;
	private double prediction;
	private double error = 0;
	private double[] prevPValues;
	private double[] prevQErrors;
	
	// Constructor
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
	
	// Calculate error with e_t = x_t - xHat_t
	public void setError() {
		this.error = value-prediction;
	}
	
	// Set error with double value
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
