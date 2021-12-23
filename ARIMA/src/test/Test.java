package test;

import arima.AR;
import timeSeries.Observation;

public class Test {

	public static void main(String[] args) {
		Observation A = new Observation(0, 60);
		Observation B = new Observation(1, 64.16);
		Observation C = new Observation(2, 63.45);
		Observation D = new Observation(3, 65.27);
		Observation E = new Observation(4, 68.73);
		Observation F = new Observation(5, 70.99);
		Observation G = new Observation(6, 69.56);
		Observation H = new Observation(7, 72.38);
		Observation I = new Observation(8, 73.54);
		Observation J = new Observation(9, 74.83);
		Observation K = new Observation(10, 72.47);
		Observation L = new Observation(11, 80.12);
		Observation M = new Observation(12, 77.54);
		Observation N = new Observation(13, 76.67);
		Observation O = new Observation(14, 81.41);
		Observation P = new Observation(15, 79.73);
		Observation Q = new Observation(16, 78.65);
		Observation R = new Observation(17, 79.56);
		Observation S = new Observation(18, 84.14);
		Observation T = new Observation(19, 84.76);
		Observation U = new Observation(20, 83.67);

		Observation[] allObservations = { A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U };

		// double[] data = AR.OLSDataArray(allObservations, 2);
		AR.errorSetupOLS(allObservations, 5);
		AR.setPrevPValues(allObservations, 5);

		/*
		 * for(int i = 0; i < data.length; i++) { System.out.println(data[i]); }
		 */

		for (int i = 0; i < allObservations.length; i++) {
			System.out.println(allObservations[i].getError());
		}
		for (Observation obs : allObservations) {
			System.out.print(obs.getIndex()+ ": ");
			for (int j = 0; j < 5; j++) {
				if (obs.getPrevPValues() != null) {
					System.out.print(obs.getPrevPValues()[j]+" ");
				}
			}
			System.out.print("\n");

		}

	}
}