package heuristik;

import arima.ARMA;
import timeSeries.Observation;

public class BruteForce {

	// Method to create new brute force for pq-order of arma models. Return best fit
	// Input: Array of observations, p and q and double value for trainProb (1 = all data used to train model)
	public static ARMA newARMAForce(Observation[] observations, int[] p, int[] q, double trainProb) {
		// At the start bestFit = null
		// If trainProb == 1 use min TrainSSE as best fit criteria
		if (trainProb == 1) {
			return(compareARMATrain(observations, p, q, trainProb));
		} else {
			return(compareARMATest(observations, p, q, trainProb));
		}
	}
	
	// Method Brute Force with train SSE as criteria
	private static ARMA compareARMATrain(Observation[] observations, int[] p, int[] q, double trainProb) {
		ARMA bestFit = null;
		for (int i = 0; i < p.length; i++) {
			// for every j in q[]
			for (int j = 0; j < q.length; j++) {
				// new ARMA with p[i] and q[i]
				ARMA currentFit = new ARMA(p[i], q[j]);
				// newSampleData with Observation[] and trainProb
				currentFit.newSampleData(observations, trainProb);
				// At the start set bestFit = currentFit or if current fit train SSE < best fir train SSE set bestFit = currentFit
				if (bestFit == null || currentFit.getTrainSSE() < bestFit.getTrainSSE()) {
					bestFit = currentFit;
				}
			}
		}
		
		// Print result
		System.out.println("\nMeasurement for order selection: Train SSE\nNumber of iterations = "
		+ p.length * q.length + "\nBest found Solution: p = " + bestFit.getP() + ", q = " + bestFit.getQ());
		
		//return best found fit
		return(bestFit);
	}
	
	// Method Brute Force with test SSE as criteria
	private static ARMA compareARMATest(Observation[] observations, int[] p, int[] q, double trainProb) {
		ARMA bestFit = null;
		for (int i = 0; i < p.length; i++) {
			// for every j in q[]
			for (int j = 0; j < q.length; j++) {
				// new ARMA with p[i] and q[i]
				ARMA currentFit = new ARMA(p[i], q[j]);
				// newSampleData with Observation[] and trainProb
				currentFit.newSampleData(observations, trainProb);
				// At the start set bestFit = currentFit or if current fit test SSE < best fir test SSE set bestFit = currentFit
				if (bestFit == null || currentFit.getTestSSE() < bestFit.getTestSSE()) {
					bestFit = currentFit;
				}
			}
		}
		// Print result
		System.out.println("\nMeasurement for order selection: Test SSE\nNumber of iterations = "
		+ p.length * q.length + "\nBest found Solution: p = " + bestFit.getP() + ", q = " + bestFit.getQ());
		
		// Return best found fit
		return(bestFit);
	}
}
