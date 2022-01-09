package heuristik;

import arima.ARMA;
import timeSeries.Observation;

public class BruteForce {

	public static ARMA newARMAForce(Observation[] observations, int[] p, int[] q, double trainProb) {
		ARMA bestFit = null;
		if (trainProb == 1) {
			for (int i = 0; i < p.length; i++) {
				for (int j = 0; j < q.length; j++) {
					ARMA currentFit = new ARMA(p[i], q[j]);
					currentFit.newSampleData(observations, trainProb);
					if (bestFit == null) {
						bestFit = currentFit;
					} else if (currentFit.getTrainSSE() < bestFit.getTrainSSE()) {
						bestFit = currentFit;
					}
				}
			}
		} else {
			for (int i = 0; i < p.length; i++) {
				for (int j = 0; j < q.length; j++) {
					ARMA currentFit = new ARMA(p[i], q[j]);
					currentFit.newSampleData(observations, trainProb);
					if (bestFit == null) {
						bestFit = currentFit;
					} else if (currentFit.getTestSSE() < bestFit.getTestSSE()) {
						bestFit = currentFit;
					}
				}
			}
		}
		return (bestFit);
	}

}
