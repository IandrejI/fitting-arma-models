package arima;


import timeSeries.Observation;

public class ARMA extends AR {
	// Fields
	private int q;
	private double[] thetaHat;
	private int maxPQ;

	public ARMA(int p, int q) {
		super(p);
		this.q = q;
	}
	
	@Override
	// Wrapper Method ARMA 
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
	
	// Method to set max p,q
	private void setMaxPQ() {
		if (p > q) {
			maxPQ = p;
		} else {
			maxPQ = q;
		}
	}
	
	// Method to set the prev. q errors in the observation array
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
	// Consider the prev. q errors
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
	
	// Method to store ThetaHat
	private void storeThetaHat() {
		thetaHat = new double[q];
		for(int i = p+1; i<=p+q; i++) {
			thetaHat[i-(p+1)] = estPara[i]; 
		}
	}
	
	@Override
	// Added to set for i < maxPQ errors to 0
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
		// Consider the prev. p values and q errors
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
	// Added printing of MA parameters
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
	// Method to forecast the h next steps
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
