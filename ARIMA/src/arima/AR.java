package arima;

import timeSeries.Observation;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class AR {
	
	//Fields
	protected int p;
	protected double[] data;
	protected int nTrain;
	protected double[] estPara;
	protected double intercept;
	protected double[] phiHat;
	protected double trainSSE;
	protected double testSSE;
	protected double trainMSE;
	protected double testMSE;
	
	// Constructor for AR
	public AR(int p) {
		this.p = p;
	}
	
	// Wrapper-Method for new AR-Model
	public void fitModel(Observation[] observations, double probTrain) {
		this.nTrain = (int) Math.round(probTrain*observations.length);
		setPrevPValues(observations);
		createOLSData(observations);
		estPara(nTrain-p,p);
		storePhiHat();
		storePrediction(observations);
		calcSSE(observations);
	}
	
	
//	// Method to create observation array out of array of doubles
//	public static Observation[] createObsArray(double[] values) {
//		Observation[] observations = new Observation[values.length];
//		for(int i = 0; i<values.length; i++) {
//			observations[i] = new Observation(i, values[i]);
//		}
//		return observations;
//	}
	

	// Set p previous observation values for every Observation there t>p
	protected void setPrevPValues(Observation[] observations) {
		// starting with i = p
		for(int i = p; i < observations.length; i++) {
			// init. new double with length p
			double[] prevPValues = new double[p];
			// Set prevPValue with prev. i-j observation value
			for (int j = 1; j < p+1; j++) {
				prevPValues[j - 1] = observations[i - j].getValue();	
			}
			observations[i].setPrevPValues(prevPValues);
		}
	}
	


	// Method to create OLSData with array. Format: (y_1, x_11, x_12, ... ,x_1p, y_2, x_21, x_22,...,x2p,...)
	protected void createOLSData(Observation[] observations){
		// Init. new double data array with length (n-p)*(p+1)
		data = new double[(nTrain-p)*(p+1)];
		// Set j = 0
		int j = 0;
		// For loop over all observation there index > p
		for (int i = p; i < nTrain; i++) {
			// Store value of observation i
			data[j] = observations[i].getValue();
			j++;
			// Store previous p values of observation i
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
		// Store parameters
		estPara = OLS.estimateRegressionParameters();
		// Store SSE
		//trainSSE = OLS.calculateResidualSumOfSquares();
	}
	
	// Store estimate for phi and intercept 
	protected void storePhiHat(){
		phiHat = new double[p];
		intercept = estPara[0];
		for(int i = 1; i<=p; i++) {
			phiHat[i-1] = estPara[i];
		}
	}
	
	// Method to set prediction and errors for every observation 
	protected void storePrediction(Observation[] observations) {
		/*for(int i = 0; i < p; i++){
			observations[i].setError(0);
		}
		*/
		for(int i = p; i <observations.length; i++){
			//invoke predict method
			double pred = predict(observations[i]);
			observations[i].setPrediction(pred);
			observations[i].setError();
		}
	}
	
	//separate predict function, to calc predicted value based on prevPValues and PsiHat


	protected double predict(Observation observation) {
		double pred = intercept;
		for(int j = 0; j < p; j++) {
			pred += observation.getPrevPValues()[j]*phiHat[j];
		}
		return pred;
	}

	protected void calcSSE(Observation[] observations) {
		trainSSE = 0;
		testSSE = 0;
		trainMSE = 0;
		testMSE = 0;
		for(int i = 0; i<nTrain; i++) {
			trainSSE += observations[i].getError()*observations[i].getError();
		}
		trainMSE = trainSSE / nTrain;
		for(int i = nTrain; i<observations.length; i++) {
			testSSE += observations[i].getError()*observations[i].getError();
		}
		if(observations.length != nTrain) {
		testMSE = testSSE / (observations.length-nTrain);
		}
	}

	
	// Method for printing Results 
	public void printResult() {
		String format1 = "\n%1$-10s-%2$-10s-%3$-10s\n";
		String format2 = "%1$-10s| %2$-10s\n";
		System.out.format(format1,"----------" ,"Result: AR("+p+")","----------"); 
		System.out.println("n = "+nTrain+"\n"); 
		System.out.format(format2, "Error", "Value\n");
		System.out.format(format2, "Train SSE",trainSSE);
		System.out.format(format2, "Test  SSE",testSSE);
		System.out.format(format2, "Train MSE",trainMSE);
		System.out.format(format2, "Test  MSE",testMSE+"\n");
		System.out.format(format2, "Param.","Value");
		System.out.format(format2, "c",intercept);
		for(int i = 0; i<phiHat.length; i++) {
			System.out.format(format2, "AR"+(i+1),phiHat[i]);
		}
		/*
		for(int i = 0; i<forecast.length; i++) {
			System.out.format(format2, "Forecast"+(i+1),forecast[i]);
		}
		*/
	}
	

	//method to forecast the h next steps
		public Observation[] forecast(Observation[] observations, int h, boolean all) {
			//init. double array for h fc values
			Observation[] forecasts = new Observation[h];
			//iterate h times 
			for(int i = 0; i < h; i++) {	
				//create new observation with value 0
				Observation ob = new Observation(observations.length+1, 0);
				//add new observation to LOCAL observation array only
				observations = addObservation(observations, ob);
				//set prev p values for all observations
				setPrevPValues(observations);
				//estimate forecast value by predicting the value for the new observation
				double fct = predict(observations[observations.length - 1]);
				//set the resulting forecast value as value of the the observation
				observations[observations.length - 1].setValue(fct);
				//store only forecasts
				forecasts[i] = observations[observations.length - 1];
			}
			String format = "%1$-10s| %2$-10s| %3$-10s\n";	
			System.out.print("\n");
			System.out.format(format, "Forecast","Index","Prediction");
			for(int i = 0; i<forecasts.length; i++) {
				System.out.format(format, "h"+(i+1),forecasts[i].getIndex(),forecasts[i].getValue());
			}
			//set fc value array as field forecast
			if(all) {
			return observations;
			} else {
				return(forecasts);
			}
			
		}
	
	
	
	
	//helper function, to add an observation to an array of observations and return the new array
	protected static Observation[] addObservation(Observation[] observations, Observation newObservation) {
		ArrayList<Observation> observationList = new ArrayList<Observation>(Arrays.asList(observations));
		observationList.add(newObservation);
		Observation[] observationArray = new Observation[observationList.size()];
		observationList.toArray(observationArray);
		return observationArray;
	}
	

	public int getP() {
		return p;
	}

	public double[] getEstPara() {
		return estPara;
	}

	public double getTrainSSE() {
		return trainSSE;
	}

	public double getIntercept() {
		return intercept;
	}

	public double[] getPhiHat() {
		return phiHat;
	}

	public double[] getData() {
		return data;
	}

	public int getnTrain() {
		return nTrain;
	}

	public double getTestSSE() {
		return testSSE;
	}
	
	
	
}


	
	
	
	
