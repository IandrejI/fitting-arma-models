package arima;

import timeSeries.Observation;

public class ARMA extends AR{

	public static void setPrevQErrors(Observation[] observations, int q) {
		for (int i = q; i < observations.length; i++) {
			double[] prevQErrors = new double[q];
			for (int j = 1; j <= q; j++) {
				prevQErrors[j-1] = observations[i-j].getError();
				observations[i].setPrevQErrors(prevQErrors);
			}
		}
	}

}
