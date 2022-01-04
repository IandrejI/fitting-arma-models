package timeSeries;

public class Observation {
	
	// Fields
	private int index;
	private double value;
	private double prediction;
	private double error;
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

	public void setError() {
		this.error = value-prediction;
	}
	public void setError(double error) {
		this.error = error;
	}

	public int getIndex() {
		return index;
	}

	public double getValue() {
		return value;
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
