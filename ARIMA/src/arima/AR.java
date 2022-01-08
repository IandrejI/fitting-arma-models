package arima;

import timeSeries.Observation;
import java.util.Arrays;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class AR {
	
	//Fields
	protected int p;
	protected double[] data;
	protected double[] estPara;
	protected double intercept;
	protected double[] psiHat;
	protected double SSE;
	protected double[] forecast;
	
	// Constructor for AR
	public AR(int p) {
		this.p = p;
	}
	
	// Wrapper-Method for new AR-Model
	public void newSampleData(Observation[] observations) {
		setPrevPValues(observations);
		createOLSData(observations);
		estPara(observations.length-p,p);
		storePsiHat();
		setPrediction(observations);
		//calcSSE(observations);
	}
	

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
		// Init. new double data array with length 0 
		data = new double[(observations.length-p)*(p+1)];
		// Set j = 0
		int j = 0;
		// For loop over all observation there index > p
		for (int i = p; i < observations.length; i++) {
			// Enhance data array p+1 (for observation value and prev. p-values)
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
		SSE = OLS.calculateResidualSumOfSquares();
	}
	
	// Store estimate for psi and intercept 
	protected void storePsiHat(){
		psiHat = new double[p];
		intercept = estPara[0];
		for(int i = 1; i<=p; i++) {
			psiHat[i-1] = estPara[i];
		}
	}
	
	// Method to set prediction and errors for every observation 
	protected void setPrediction(Observation[] observations) {
		for(int i = 0; i < p; i++){
			observations[i].setError(0);
		}
		for(int i = p; i <observations.length; i++){
			double pred = intercept;
			for(int j = 1; j <= p; j++) {
				pred += observations[i].getPrevPValues()[j-1]*psiHat[j-1];
			}
			observations[i].setPrediction(pred);
			observations[i].setError();
		}
	}
	
	/*
	protected void calcSSE(Observation[] observations) {
		SSE = 0;
		for(Observation obs : observations) {
			SSE += obs.getError()*obs.getError();
		}
	}
	*/
	
	// Method for printing Results 
	public void printResult() {
		String format1 = "\n%1$-10s-%2$-10s-%3$-10s\n";
		String format2 = "%1$-10s| %2$-10s\n";
		System.out.format(format1,"----------" ,"Result: AR("+p+")","----------\n"); 
		System.out.format(format2, "Error", "Value");
		System.out.format(format2, "SSE",SSE+"\n");
		System.out.format(format2, "Param.","Value");
		System.out.format(format2, "c",intercept);
		for(int i = 0; i<psiHat.length; i++) {
		System.out.format(format2, "AR"+(i+1),psiHat[i]);
		}
	}
	
	//concept for forecast method, work in progress
	public void forecast(Observation[] observations, int h) {
		double[] fc = new double[h];
		for(int i = 0; i < h; i++) {
			double fct = intercept;
			for(int j = 0; j < p; j++) {
				fct += observations[observations.length+i-j].getValue() * psiHat[j];
			}
			fc[i] = fct;
		}
		forecast = fc;
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

	public double getIntercept() {
		return intercept;
	}

	public double[] getPsiHat() {
		return psiHat;
	}
	
	
	
}


	
	
	
	
