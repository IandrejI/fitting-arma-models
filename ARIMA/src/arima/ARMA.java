package arima;

import java.util.Arrays;

import timeSeries.Observation;

public class ARMA extends AR {
	// Fields
	private int q;
	//private double[] qEst;
	private int maxPQ;

	public ARMA(Observation[] observations, int p, int q) {
		super(observations, p);
		this.q = q;
		setMaxPQ();
		setPrevQErrors();
		createOLSData();
		estPara(observations.length-maxPQ,p+q);
	}

	private void setMaxPQ() {
		if (p > q) {
			maxPQ = p;
		} else {
			maxPQ = q;
		}
	}

	public void setPrevQErrors() {
		for (int i = q; i < observations.length; i++) {
			double[] prevQErrors = new double[q];
			for (int j = 1; j <= q; j++) {
				prevQErrors[j - 1] = observations[i - j].getError();
				observations[i].setPrevQErrors(prevQErrors);
			}
		}
	}

	@Override
	protected void createOLSData() {
		data = new double[0];
		int j = 0;
		// For loop over all observation there index > p
		for (int i = maxPQ; i < observations.length; i++) {
			// Enhance data array
			data = Arrays.copyOf(data, data.length + p + q + 1);
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

}
