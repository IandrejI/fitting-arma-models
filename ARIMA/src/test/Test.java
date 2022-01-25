package test;

import arima.AR;
import arima.ARMA;
import heuristik.BruteForce;
import timeSeries.Observation;
import timeSeries.TimeSeriesUtils;
/**
 * In the Test class at first the given sample data is loaded and put into an array of observations. 
 * Then an AR model and subsequently an ARMA model is created, fitted and the output printed. 
 * @author Christoph Barkey 
 * @author Andrej Muschke
 *
 */

public class Test {

	public static void main(String[] args) {
		
		// Given values
		double[] values = {60, 64.16, 63.45, 65.27, 68.73, 70.99, 69.56, 72.38, 73.54, 74.83, 72.47, 80.12, 77.54,
				76.67, 81.41, 79.73, 78.65, 79.56, 84.14, 84.76, 83.67};
		
		// Create array of observation
		Observation[] allObservations = TimeSeriesUtils.createObsArray(values);
		
		// Fit new AR(2) Model and forecast 2 periods
		AR ar = new AR(2);
		ar.fitModel(allObservations, 1);
		ar.printResult();
		ar.forecast(allObservations, 2, false);
		
		// Fit new ARMA(2) Model and forecast 2 periods
		ARMA arma = new ARMA(2, 2);
		arma.fitModel(allObservations, 1);
		arma.printResult();
		arma.forecast(allObservations, 2, false);
		
		// Fit brute force heuristik with P in [0,5] and Q in [0,5] and forecast 5 periods
		int[] p = {0,1,2,3,4,5};
		int[] q = {0,1,2,3,4,5};	
		ARMA finalModel = BruteForce.newARMAForce(allObservations, p, q, 0.95);
		finalModel.printResult();
		Observation[] forecast = finalModel.forecast(allObservations, 5, false);
		
		// Pint values of forecast to show difference of true and false in forecast-method 
		
		/*
		for(int i = 0; i < forecast.length; i++) {
			System.out.println(forecast[i].getValue());
		}
		*/
	
		
	}
}
