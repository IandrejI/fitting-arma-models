package arima;


import timeSeries.Observation;

/**
 * Class to solve an ARMA-Model using a linear regression.
 * First solving the AR part of the model and subsequently solving the MA part,
 * both with an OLS regression, minimizing the sum of squared errors.
 * By solving the AR part, we obtain the p parameter values and with these we can calculate 
 * the predicted values and thus the residuals for all observations.
 * @author Christoph Barkey 
 * @author Andrej Muschke
 *
 */

public class ARMA extends AR {
	
	// Fields
	private int q;
	private double[] thetaHat;
	private int maxPQ;
	
	/**
	 * Constructor for an ARMA-Model. Only uses p and q as input.
	 * @param p order p for AR polynomial
	 * @param q order q for MA polynomial
	 */
	public ARMA(int p, int q) {
		super(p);
		this.q = q;
	}
	
	@Override
	/**
	 * Wrapper function that includes data preparation steps, parameter estimations and evaluation.
	 * Invokes a new AR-Model at the beginning to set errors.
	 * @param observations array of observations
	 * @param probTrain probability of training data [0,1]
	 */ 
	public void fitModel(Observation[] observations, double probTrain){
		AR ar = new AR(p);
		ar.fitModel(observations, probTrain);
		nTrain = (int) Math.round(probTrain*observations.length);
		setMaxPQ();
		setPrevQErrors(observations);
		createOLSData(observations);
		estPara(nTrain-maxPQ,p+q);
		storePhiHat();
		storeThetaHat();
		storePrediction(observations);
		calcSSE(observations);
	}
	
	/**
	 * Method to store the max(p,q) value
	 */
	private void setMaxPQ() {
		if (p > q) {
			maxPQ = p;
		} else {
			maxPQ = q;
		}
	}
	
	/**
	 * Method to allocate the previous q errors to all observations
	 * @param observations array of observations
	 */
	public void setPrevQErrors(Observation[] observations) {
		for (int i = q; i < observations.length; i++) {
			double[] prevQErrors = new double[q];
			for (int j = 1; j <= q; j++) {
				prevQErrors[j - 1] = observations[i - j].getError();
				observations[i].setPrevQErrors(prevQErrors);
			}
		}
	}


	
	@Override
	/**
	 *  Method to create OLSData-array. Considering prevPValues and prevQErrors.
	 *  Format: (y_1, x_11, x_12, ... ,x_1p, y_n, x_n1, x_n2,...,x_np)
	 * @param observations array of observations
	 */
	protected void createOLSData(Observation[] observations) {
		// Init. new double data array with length (n-p)*(p+q+1)
		data = new double[(nTrain-maxPQ)*(p+q+1)];
		int j = 0;
		// For loop over all observation there index > p
		for (int i = maxPQ; i < nTrain; i++) {
			// Store values of Observations
			data[j] = observations[i].getValue();
			j++;
			for (int k = 0; k < p; k++) {
				data[j] = observations[i].getPrevPValues()[k];
				j++;
			}
			for (int k = 0; k < q; k++) {
				data[j] = observations[i].getPrevQErrors()[k];
				j++;
			}
		}
	}
	
	/**
	 * Method to stores the MA parameters into the field thetaHat
	 */
	private void storeThetaHat() {
		thetaHat = new double[q];
		for(int i = p+1; i<=p+q; i++) {
			thetaHat[i-(p+1)] = estPara[i]; 
		}
	}
	
	@Override
	/**
	 * Creates actual predictions and stores them into the respective field prediction. 
	 * Then setError is invoked, calculating, and storing the results in error
	 * Considering maxPQ as boundaries and set error to 0 for i < maxPQ
	 * @param observations array of observations
	 */
	protected void storePrediction(Observation[] observations) {
		for(int i = 0; i < maxPQ; i++){
			observations[i].setError(0);
		}
		for(int i = maxPQ; i <observations.length; i++){
			double pred = predict(observations[i]);
			observations[i].setPrediction(pred);
			observations[i].setError();
		}
	}
	

	
	@Override
	/**
	 * Separate predict function, to calculate predicted value based on prevPValues and PsiHat
	 and prevQErrors and thetaHat.
	 * @param observation array of observations
	 * @return predicted value for observation
	 */
		protected double predict(Observation observation) {
			double pred = intercept;
			for(int j = 0; j < p; j++) {
				pred += observation.getPrevPValues()[j]*phiHat[j];
			}
			for(int j = 0; j < q; j++) {
				pred += observation.getPrevQErrors()[j]*thetaHat[j];
			}
			return pred;
		}
	
	
	
	@Override
	/**
	 * Method to print the relevant data of an ARMA model. 
	 * Prints the order p,q, n, the parameter values and the model performance measures
	 */
	public void printResult() {
		String format1 = "\n%1$-10s-%2$-10s-%3$-10s\n";
		String format2 = "%1$-10s| %2$-10s\n";
		System.out.format(format1,"----------" ,"Result: ARMA("+p+","+q+")","----------\n"); 
		System.out.println("n = "+nTrain+"\n"); 
		System.out.format(format2, "Error", "Value");
		System.out.format(format2, "Train SSE",trainSSE);
		System.out.format(format2, "Test  SSE",testSSE);
		System.out.format(format2, "Train MSE",trainMSE);
		System.out.format(format2, "Test  MSE",testMSE+"\n");
		System.out.format(format2, "Param.","Value");
		System.out.format(format2, "c",intercept);
		for(int i = 0; i<phiHat.length; i++) {
			System.out.format(format2, "AR"+(i+1),phiHat[i]);
		}
		for(int i = 0; i<thetaHat.length; i++) {
			System.out.format(format2, "MA"+(i+1),thetaHat[i]);
		}
	}
	
	@Override
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
			//setPrevQErrors(observations);
			setPrevQErrors(observations);
			//estimate forecast value by predicting the value for the new observation
			double fct = predict(observations[observations.length - 1]);
			//set the resulting forecast value as value of the the observation
			observations[observations.length - 1].setValue(fct);
			//store result in fc array
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
	
	
	// Getters
	public int getQ() {
		return q;
	}

	public int getMaxPQ() {
		return maxPQ;
	}

	public double[] getPhiHat() {
		return phiHat;
	}

	public double[] getThetaHat() {
		return thetaHat;
	}

}
