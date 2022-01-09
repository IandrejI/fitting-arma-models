package arima;

import java.util.Arrays;

import timeSeries.Observation;

public class ARMA extends AR {
	// Fields
	private int q;
	private double[] thetaHat;
	private int maxPQ;
	private double[] forecast;

	public ARMA(int p, int q) {
		super(p);
		this.q = q;
	}
	
	@Override
	public void newSampleData(Observation[] observations){
		AR ar = new AR(p);
		ar.newSampleData(observations);
		setMaxPQ();
		setPrevQErrors(observations);
		createOLSData(observations);
		estPara(observations.length-maxPQ,p+q);
		storePsiHat();
		storeThetaHat();
		setPrediction(observations);
		//calcSSE(observations);
	}
	

	private void setMaxPQ() {
		if (p > q) {
			maxPQ = p;
		} else {
			maxPQ = q;
		}
	}

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
	protected void createOLSData(Observation[] observations) {
		// Init. new double data array with length (n-p)*(p+q+1)
		data = new double[(observations.length-maxPQ)*(p+q+1)];
		int j = 0;
		// For loop over all observation there index > p
		for (int i = maxPQ; i < observations.length; i++) {
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
	
	
	private void storeThetaHat() {
		thetaHat = new double[q];
		for(int i = p+1; i<=p+q; i++) {
			thetaHat[i-(p+1)] = estPara[i]; 
		}
	}
	
	@Override
	protected void setPrediction(Observation[] observations) {
		for(int i = 0; i < maxPQ; i++){
			observations[i].setError(0);
		}
		for(int i = maxPQ; i <observations.length; i++){
			double pred = intercept;
			for(int j = 1; j <= p; j++) {
				pred += observations[i].getPrevPValues()[j-1]*psiHat[j-1];
			}
			for(int j = 1; j <= q; j++) {
				pred += observations[i].getPrevQErrors()[j-1]*thetaHat[j-1];
			}
		observations[i].setPrediction(pred);
		observations[i].setError();
		}
	}
	
	@Override
	public void printResult() {
		String format1 = "\n%1$-10s-%2$-10s-%3$-10s\n";
		String format2 = "%1$-10s| %2$-10s\n";
		System.out.format(format1,"----------" ,"Result: ARMA("+p+","+q+")","----------\n"); 
		System.out.format(format2, "Error", "Value");
		System.out.format(format2, "SSE",SSE+"\n");
		System.out.format(format2, "Param.","Value");
		System.out.format(format2, "c",intercept);
		for(int i = 0; i<psiHat.length; i++) {
		System.out.format(format2, "AR"+(i+1),psiHat[i]);
		}
		for(int i = 0; i<thetaHat.length; i++) {
			System.out.format(format2, "MA"+(i+1),thetaHat[i]);
		}
		for(int i = 0; i<forecast.length; i++) {
			System.out.format(format2, "Forecast"+(i+1),forecast[i]);
		}
	}
	
	@Override
	//method to forecast the h next steps
	public void forecast(Observation[] observations, int h) {
		//init. double array for h fc values
		double[] fc = new double[h];
		//iterate h times 
		for(int i = 0; i < h; i++) {
			//init. fc value with intercept
			double fct = intercept;
			//iterate through the parameters
			for(int j = 0; j < p; j++) {
				//multiply the last value with p1, the second last with p2, ... add to fc value
				fct += observations[observations.length-1-j].getValue() * getPsiHat()[j];
				System.out.println("p " + j +" "+ observations[observations.length-1-j].getValue());
			}
			for(int j = 0; j < q; j++) {
				//multiply the last value with p1, the second last with p2, ... add to fc value
				fct += getZ()[j] * getTetaHat()[j];
				System.out.println("q " + j+ " " + observations[observations.length-1-j].getError());
			}
			//store result in fc array
			fc[i] = fct;
			//create new observation with the forecast value as value
			Observation ob = new Observation(observations.length, fct);
			//add new observation to LOCAL observation array only
			observations = addObservation(observations, ob);
		}
		//set fc value array as field forecast
		forecast = fc;
	}
	
	public double[] getPsiHat() {
		return psiHat;
	}

	public double[] getTetaHat() {
		return thetaHat;
	}

}
