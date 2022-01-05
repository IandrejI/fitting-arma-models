package arima;

import timeSeries.Observation;
import java.util.Arrays;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class AR {
	
	//Fields
	protected int p;
	protected int maxPQ;
	protected double[] data;
	protected double[] estPara;
	protected double intercept;
	protected double[] psiHat;
	protected double SSE;
	
	
	public AR(int p) {
		this.p = p;
	}
	
	public void newARData(Observation[] observations) {
		setPrevPValues(observations);
		createOLSData(observations);
		estPara(observations.length-p,p);
		storePsiHat();
		setPrediction(observations);
		calcSSE(observations);
	}
	

	// Set p previous observation values
	protected void setPrevPValues(Observation[] observations) {
		for(int i = p; i < observations.length; i++) {
			double[] prevPValues = new double[p];
			for (int j = 1; j < p+1; j++) {
				prevPValues[j - 1] = observations[i - j].getValue();	
			}
			observations[i].setPrevPValues(prevPValues);
		}
	}

	protected void createOLSData(Observation[] observations){
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
		SSE = OLS.calculateTotalSumOfSquares();
	}
	
	protected void storePsiHat(){
		psiHat = new double[p];
		intercept = estPara[0];
		for(int i = 1; i<=p; i++) {
			psiHat[i-1] = estPara[i];
		}
	}

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
	
	protected void calcSSE(Observation[] observations) {
		SSE = 0;
		for(Observation obs : observations) {
			SSE += obs.getError()*obs.getError();
		}
	}

	public void printResult() {
		String format1 = "%1$-10s-%2$-10s-%3$-10s\n";
		String format2 = "%1$-10s| %2$-10s\n";
		System.out.format(format1,"----------" ,"Result: AR("+p+")","----------\n"); 
		System.out.format(format2, "Error", "Value");
		System.out.format(format2, "SSE",SSE+"\n");
		System.out.format(format2, "Param.","Value");
		System.out.format(format2, "c",intercept);
		for(int i = 0; i<psiHat.length; i++) {
		System.out.format(format2, "AR"+i,psiHat[i]);
		}
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


	
	
	
	
