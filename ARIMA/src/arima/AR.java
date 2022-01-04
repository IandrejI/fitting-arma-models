package arima;

import timeSeries.Observation;
import java.util.Arrays;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class AR {
	
	//Fields
	protected Observation[] observations;
	protected int p;
	protected double[] data;
	protected double[] estPara;
	protected double SSE;
	
	
	public AR(Observation[] observations, int p) {
		this.p = p;
		this.observations = observations;
		setPrevPValues();
		createOLSData();
		estPara(observations.length-p,p);
		setPrediction();	
	}

	// Set p previous observation values
	protected void setPrevPValues() {
		for (int i = p; i < observations.length; i++) {
			double[] prevPValues = new double[p];
			for (int j = 1; j <= p; j++) {
				prevPValues[j - 1] = observations[i - j].getValue();
				observations[i].setPrevPValues(prevPValues);
			}
		}
	}

	protected void createOLSData(){
		data = new double[0];
		int j = 0;
		// For loop over all observation there index > p
		for (int i = p; i < observations.length; i++) {
			// Enhance data array
			data = Arrays.copyOf(data, data.length+p+1);
			// Store values of Observations
			data[j] = observations[i].getValue();
			j++;
			for (int k = 0; k < p; k++) {
				data[j] = observations[i].getPrevPValues()[k];
				j++;
			}
		}
	}
	
	
	protected void estPara(int numObs, int numPara) {

		// New Multiple Linear Regression Model, solve with OLS.
		OLSMultipleLinearRegression OLS = new OLSMultipleLinearRegression();
		OLS.newSampleData(data, numObs, numPara);
		estPara = OLS.estimateRegressionParameters();
	}
	
	protected void setPrediction() {
		for(int i = 0; i < p; i++){
			observations[i].setError(0);
		}
		for(int i = p; i <observations.length; i++){
			double pred = estPara[0];
			for(int j = 0; j == p; j++) {
				pred += observations[i].getPrevPValues()[j]*estPara[j+1];
			}
		observations[i].setPrediction(pred);
		observations[i].setError();
		}
	}

	public Observation[] getObservations() {
		return observations;
	}

	public int getP() {
		return p;
	}

	public double[] getEstPara() {
		return estPara;
	}

	public double getSSE() {
		return SSE;
	}
	
	
}


	
	
	
	
