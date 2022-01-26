package arima;

import timeSeries.Observation;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

/**
 * Class to solve an AR-Model using a linear regression
 * @author Christoph Barkey 
 * @author Andrej Muschke
 *
 */
public class AR {
	
	// Fields
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
	
	/**
	 * Constructor for an AR-Model. Only uses p as input.
	 * @param p order p for AR polynomial
	 */
	public AR(int p) {
		this.p = p;
	}
	
	/**
	 * Wrapper function that includes data preparation steps, parameter estimations and evaluation.
	 * @param observations array of observations
	 * @param probTrain probability of training data [0,1]
	 */
	public void fitModel(Observation[] observations, double probTrain) {
		this.nTrain = (int) Math.round(probTrain*observations.length);
		setPrevPValues(observations);
		createOLSData(observations);
		estPara(nTrain-p,p);
		storePhiHat();
		storePrediction(observations);
		calcSSE(observations);
	}
	

	/**
	 * Set p previous observation values for every Observation there t>p
	 * @param observations array of observations
	 */
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
	


	/**
	 *  Method to create OLSData-array. Considering prevPValues.
	 *  Format: (y_1, x_11, x_12, ... ,x_1p, y_n, x_n1, x_n2,...,x_np)
	 * @param observations array of observations
	 */
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
	
	/**
	 * Takes the previously created input data from data, estimates the regression parameters, and stores them into estPara
	 * @param numObs number of observations
	 * @param numPara number of parameters
	 */
	protected void estPara(int numObs, int numPara) {

		// New Multiple Linear Regression Model, solve with OLS.
		OLSMultipleLinearRegression OLS = new OLSMultipleLinearRegression();
		OLS.newSampleData(data, numObs, numPara);
		// Store parameters
		estPara = OLS.estimateRegressionParameters();
		// Store SSE
		//trainSSE = OLS.calculateResidualSumOfSquares();
	}
	
	/**
	 * Stores the intercept and AR parameters separately into the respective fields intercept and phiHat 
	 */
	protected void storePhiHat(){
		phiHat = new double[p];
		intercept = estPara[0];
		for(int i = 1; i<=p; i++) {
			phiHat[i-1] = estPara[i];
		}
	}
	
	/**
	 * Creates actual predictions and stores them into the respective field prediction. 
	 * Then setError is invoked, calculating, and storing the results in error
	 * @param observations array of observations
	 */
	protected void storePrediction(Observation[] observations) {
		for(int i = p; i <observations.length; i++){
			//invoke predict method
			double pred = predict(observations[i]);
			observations[i].setPrediction(pred);
			observations[i].setError();
		}
	}
	
	/**
	 * Separate predict function, to calculate predicted value based on prevPValues and PsiHat
	 * @param observation array of observations
	 * @return predicted value for observation
	 */
	protected double predict(Observation observation) {
		double pred = intercept;
		for(int j = 0; j < p; j++) {
			pred += observation.getPrevPValues()[j]*phiHat[j];
		}
		return pred;
	}

	/**
	 * Method to calculate the error measurements.
	 * train/test SSE/MSE
	 * @param observations array of observations
	 */
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

	
	/**
	 * Method to print the relevant data of an AR model. 
	 * Prints the order p, n, the parameter values and the model performance measures
	 */
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
	}
	

	/**
	 *  Method to forecast the h next steps
	 *  Creates forecast values and prints them directly. 
	 *  The user can decide if he/she  wants to store all observations or only the forecasts.
	 * @param observations array of observations
	 * @param h periods to forecast 
	 * @param all if true -> return observation + forecasts
	 * @return forecast  or observations + forecasts
	 */
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
	
	
	
	
	/**
	 * Helper function, to add an observation to an array of observations and return the new array.
	 * @param observations array of observations
	 * @param newObservation observation to add
	 * @return enhanced array of observations
	 */
	protected static Observation[] addObservation(Observation[] observations, Observation newObservation) {
		ArrayList<Observation> observationList = new ArrayList<Observation>(Arrays.asList(observations));
		observationList.add(newObservation);
		Observation[] observationArray = new Observation[observationList.size()];
		observationList.toArray(observationArray);
		return observationArray;
	}
	

	// Getters
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


	
	
	
	
