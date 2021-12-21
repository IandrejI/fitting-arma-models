package timeSeries;

public class Observation {
	
	// Fields
	private int index;
	private double value;
	private double prediction;
	private double error;
	
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

	public int getIndex() {
		return index;
	}

	public double getValue() {
		return value;
	}

}
