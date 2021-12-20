package assemblyLine;

public class Job {

	// Fields
	private String name;
	private double proTime;
	private double RPW;

	
	
	// Constructor
	public Job(String name, double proTime) {
		this.name = name;
		this.proTime = proTime;
		this.RPW = proTime;
	}
	


	// Getters
	public String getName() {
		return name;
	}

	public double getProTime() {
		return proTime;
	}

	public double getRPW() {
		return RPW;
	}

	public void setRPW(double RPW) {
		this.RPW = RPW;
	}

	
}

