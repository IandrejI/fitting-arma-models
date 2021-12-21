package arima;

import timeSeries.Observation;
import java.util.Arrays;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

public class AR {
	
	// Create Array for OLS Format (y1,x1[1],x1[2],...,x1[p],y2,x2[1]...)
	public static double[] OLSDataArray(Observation[] observations, int p) {
		// Initialize array of doubles to store regression-variables
		double[] data = new double[0];
		int j = 0;
		// For loop over all observation there index > p
		for (int i = p; i < observations.length; i++) {
			// Enhance data array 
			data = Arrays.copyOf(data, data.length+p+1);
			// Store values of Observations 
			for (int q = 0; q <= p; q++) {
				data[j] = observations[i-q].getValue();
				j++;
			}

		}

		return data;
	}

	// Method to set starting Errors
	public static double[] errorSetup(Observation[] observations, int p) {
		double[] data = OLSDataArray(observations,p);
		
		//New Multiple Linear Regression Model, solve with OLS.
		OLSMultipleLinearRegression OLS = new OLSMultipleLinearRegression();
		OLS.newSampleData(data, observations.length-p, p);
		double[] para = OLS.estimateRegressionParameters();
		
		return(para);
	}
		

	}

