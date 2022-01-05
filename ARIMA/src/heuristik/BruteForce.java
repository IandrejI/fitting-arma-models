package heuristik;
import arima.ARMA;
import timeSeries.Observation;
public class BruteForce {
	
	public static ARMA newARMAForce(Observation[] observations, int[] p, int[] q) {
		ARMA bestFit = null;
		for(int i = 0; i<p.length; i++) {
			for(int j = 0; j<q.length; j++) {
				ARMA currentFit = new ARMA(p[i], q[j]);
				currentFit.newARMAData(observations);
				if(bestFit == null) {
					bestFit = currentFit;
				} else if(currentFit.getSSE() < bestFit.getSSE()) {
					bestFit = currentFit;
				}
			}
		}
		return(bestFit);
	}
	
	

}
