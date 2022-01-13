package heuristik;

import arima.ARMA;
import timeSeries.Observation;

public class BruteForce {
	
	// Method to create new brute force for pq-order of arma models. Return best fit ARMA model.
	// Input: Array of observations, p and q and double value for trainProb (1 = all data used to train model)
	public static ARMA newARMAForce(Observation[] observations, int[] p, int[] q, double trainProb) {
		// At the start bestFit = null
		ARMA bestFit = null;
		// If trainProb == 1 use min TrainSSE as best fit criteria
		if (trainProb == 1) {
			// for every i in p[]
			for (int i = 0; i < p.length; i++) {
				// for every j in q[]
				for (int j = 0; j < q.length; j++) {
					// new ARMA with p[i] and q[i]
					ARMA currentFit = new ARMA(p[i], q[j]);
					// newSampleData with Observation[] and trainProb
					currentFit.newSampleData(observations, trainProb);
					// At the start set bestFit = currentFit
					if (bestFit == null) {
						bestFit = currentFit;
					// Else if current fit train SSE < best fir train SSE set bestFit = currentFit
					} else if (currentFit.getTrainSSE() < bestFit.getTrainSSE()) {
						bestFit = currentFit;
					}
				}
			}
		} else {
			// Use min test SSE as criteria
			// for every i in p[]
			for (int i = 0; i < p.length; i++) {
				// for every j in q[]
				for (int j = 0; j < q.length; j++) {
					// new ARMA with p[i] and q[i]
					ARMA currentFit = new ARMA(p[i], q[j]);
					// newSampleData with Observation[] and trainProb
					currentFit.newSampleData(observations, trainProb);
					// At the start set bestFit = currentFit
					if (bestFit == null) {
						bestFit = currentFit;
					// Else if current fit train SSE < best fir train SSE set bestFit = currentFit
					} else if (currentFit.getTestSSE() < bestFit.getTestSSE()) {
						bestFit = currentFit;
					}
				}
			}
		}
		//Print out result
		// If trainProb == 1 nMeasurement for order selection: Train SSE
		if (trainProb == 1) {
			System.out.println("\nMeasurement for order selection: Train SSE\nNumber of iterations = " 
			+ p.length * q.length + "\nBest found Solution: p = " + bestFit.getP()+ ", q = " + bestFit.getQ());
		// If trainProb == 1 nMeasurement for order selection: Test SSE
		} else {
			System.out.println("\nMeasurement for order selection: Test SSE\nNumber of iterations = " 
					+ p.length * q.length + "\nBest found Solution: p = " + bestFit.getP()+ ", q = " + bestFit.getQ());
		}
		// return bestFit 
		return (bestFit);
	}

}
