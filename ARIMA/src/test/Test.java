package test;

import arima.AR;
import arima.ARMA;
import heuristik.BruteForce;
import timeSeries.Observation;

public class Test {

	public static void main(String[] args) {
		
		double[] values = {60, 64.16, 63.45, 65.27, 68.73, 70.99, 69.56, 72.38, 73.54, 74.83, 72.47, 80.12, 77.54,
				76.67, 81.41, 79.73, 78.65, 79.56, 84.14, 84.76, 83.67};
		
		Observation[] allObservations = AR.createObsArray(values);
		
		AR ar = new AR(2);
		ar.newSampleData(allObservations);
		ar.printResult();
		
		ARMA arma = new ARMA(2, 2);
		arma.newSampleData(allObservations);
		arma.printResult();
		
		int[] p = {0,1,2,3,4,5};
		int[] q = {0,1,2,3,4,5};
		
		ARMA finalModel = BruteForce.newARMAForce(allObservations, p, q);
		finalModel.printResult();
	}
}
