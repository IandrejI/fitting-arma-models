package test;

import arima.AR;
import arima.ARMA;
import heuristik.BruteForce;
import timeSeries.Observation;
import timeSeries.Utils;

public class Test {

	public static void main(String[] args) {
		
		double[] values = {60, 64.16, 63.45, 65.27, 68.73, 70.99, 69.56, 72.38, 73.54, 74.83, 72.47, 80.12, 77.54,
				76.67, 81.41, 79.73, 78.65, 79.56, 84.14, 84.76, 83.67};
		
	
		Observation[] allObservations = Utils.createObsArray(values);
//		Observation[] allObservations = AR.createObsArray(values);
		
		AR ar = new AR(2);
		ar.fitModel(allObservations, 1);
		ar.printResult();
		ar.forecast(allObservations, 2, false);
		
		
		ARMA arma = new ARMA(2, 2);
		arma.fitModel(allObservations, 1);
		arma.printResult();
		arma.forecast(allObservations, 1, false);
		
		
		int[] p = {0,1,2,3,4,5};
		int[] q = {0,1,2,3,4,5};	
		ARMA finalModel = BruteForce.newARMAForce(allObservations, p, q, 0.95);
		finalModel.printResult();
		Observation[] f = finalModel.forecast(allObservations, 5, false);
		
		/*
		for(int i = 0; i < f.length; i++) {
			System.out.println(f[i].getValue());
		}
		*/
		
	}
}
